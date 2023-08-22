/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.sources.filesystem.s3

import com.amazonaws.auth.{AWSStaticCredentialsProvider, AnonymousAWSCredentials, BasicAWSCredentials}
import com.amazonaws.retry.PredefinedRetryPolicies
import com.amazonaws.services.s3.model.{ListObjectsRequest, S3Object, S3ObjectSummary}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.thirdparty.apache.http.conn.ConnectTimeoutException
import com.amazonaws.{AmazonClientException, AmazonServiceException, ClientConfiguration}
import com.google.common.collect.AbstractIterator
import raw.config._
import raw.creds.{AWSCredentials, S3Bucket}
import raw.sources.bytestream.{DelegatingSeekableInputStream, GenericSkippableInputStream, SeekableInputStream}
import raw.sources.filesystem._
import raw.utils.RawUtils

import java.io.InputStream
import java.util
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._

object S3FileSystem {
  private val S3_CONNECT_TIMEOUT = "raw.sources.s3.connect-timeout"
  private val S3_READ_TIMEOUT = "raw.sources.s3.read-timeout"
  private val S3_MAX_CONNECTIONS = "raw.sources.s3.max-connections"
  private val S3_DEFAULT_REGION = "raw.sources.s3.default-region"
}

class S3FileSystem(val bucket: S3Bucket)(implicit settings: RawSettings) extends BaseFileSystem {

  import S3FileSystem._

  private[sources] val fileSeparator: String = "/"
  private val fileSeparatorRegex: String = RawUtils.descape(fileSeparator)

  private val s3ConnectTimeout = settings.getDuration(S3_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
  private val s3ReadTimeout = settings.getDuration(S3_READ_TIMEOUT, TimeUnit.MILLISECONDS)
  private val s3MaxConnections = settings.getInt(S3_MAX_CONNECTIONS)

  def bucketName: String = bucket.name

  private lazy val client: AmazonS3 = {
    val builder = AmazonS3ClientBuilder
      .standard()
      // see https://github.com/aws/aws-sdk-java/issues/1366
      .enableForceGlobalBucketAccess()
      .withRegion(bucket.region.getOrElse(settings.getString(S3_DEFAULT_REGION)))

    bucket.credentials match {
      case Some(AWSCredentials(accessKey, secretKey)) =>
        builder.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
      case None => builder.withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials))
    }

    builder.setClientConfiguration(
      new ClientConfiguration()
        .withSocketTimeout(s3ConnectTimeout.toInt)
        .withConnectionTimeout(s3ReadTimeout.toInt)
        .withMaxConnections(s3MaxConnections)
        .withRetryPolicy(PredefinedRetryPolicies.getDefaultRetryPolicy())
    )
    builder.build()
  }

  /**
   * This is questionable but has to remain. Here's why:
   *
   * By sanitizing paths, we are limiting the keys that users can access.
   * S3 doesn't actually have the concept of separators or wildcards, so when we sanitize it, we make a potentially
   * valid key inaccessible to us. Not likely a problem, since only "strange keys" - e.g. with multiple consecutive
   * slashes - would be inaccessible, but it's something to keep in mind.
   *
   * However, accepting every key would make some parts of this file system weird. For instance, when listing contents,
   * we have to manually add the "intermediary directories" to the path result, so that it mimics a normal file system.
   * This wouldn't be possible without some form of path "sanitizer".
   */
  private def sanitizePath(path: String): String = {
    //(CTM) Added the stripPrefix from the original file committed long time ago
    path.replaceAll(s"$fileSeparatorRegex+", fileSeparator).stripSuffix(fileSeparator).stripPrefix(fileSeparator)
  }

  private def getS3Object(file: String): S3Object = {
    withCatchAmazonExceptions(file, client.getObject(bucket.name, file))
  }

  private def withCatchAmazonExceptions[R](path: String, f: => R): R = {
    try {
      f
    } catch {
      case ex: AmazonServiceException if ex.getStatusCode == 403 => throw new PathUnauthorizedException(path, ex)
      case ex: AmazonServiceException => throw new PathNotFoundException(path, ex)
      case ex: AmazonClientException => ex.getCause match {
          case ex: ConnectTimeoutException => throw new FileSystemTimeoutException(Some(path), ex)
          case _ =>
            logger.warn(s"error getting s3 object: ${ex.getMessage}", ex)
            throw new FileSystemUnavailableException(path, ex)
        }
    }
  }

  override def metadata(path: String): FileSystemMetadata = {
    val sanitizedPath = sanitizePath(path)

    if (hasGlob(path)) throw new PathInvalidException(path, null)

    if (sanitizedPath == "") {
      return DirectoryMetadata(None)
    }
    val s3Object =
      try {
        getS3Object(sanitizedPath)
      } catch {
        case _: PathNotFoundException =>
          listContents(path).hasNext // If there are contents, then try as "directory", which may throw exception
          return DirectoryMetadata(None)
      }
    try {
      val metadata = s3Object.getObjectMetadata
      FileMetadata(
        Option(metadata.getLastModified).map(_.toInstant),
        Option(metadata.getContentLength),
        Array.empty
      )
    } finally {
      s3Object.close()
    }
  }

  override def getInputStream(file: String): InputStream = {
    try {
      getS3Object(sanitizePath(file)).getObjectContent
    } catch {
      case ex: PathNotFoundException =>
        // If there are contents, then try as "directory", which may throw exception
        listContents(file)
        throw new NotAFileException(file, ex)
    }
  }

  override def getSeekableInputStream(file: String): SeekableInputStream = {

    // The getInputStream will use getObject with uses withCatchAmazonExceptions
    // which already catches and rethrows our exceptions
    val is = new GenericSkippableInputStream(() => getInputStream(file))
    new DelegatingSeekableInputStream(is) {
      override def getPos: Long = is.getPos

      override def seek(newPos: Long): Unit = is.seek(newPos)
    }

  }

  override def listContents(path: String): Iterator[String] = {
    listContentsWithMetadata(path).map(_._1)
  }

  override def listContentsWithMetadata(path: String): Iterator[(String, FileSystemMetadata)] = {
    val it = new ListingIterator(path).asScala
    // We check that it hasNext so that we fire an error if the path doesn't exist or is not accessible.
    it.hasNext
    it
  }

  private class ListingIterator(val path: String) extends AbstractIterator[(String, FileSystemMetadata)] {

    private val sanitizedPath = sanitizePath(path)
    private val _hasGlob = hasGlob(sanitizedPath)
    private val pathBeforeGlob = sanitizedPath
      .split(fileSeparatorRegex)
      .takeWhile(p => !hasGlob(p))
      .mkString(fileSeparator)
    logger.debug(s"Path: $path, sanitizedPath: $sanitizedPath (glob: ${_hasGlob}), pathBeforeGlob: $pathBeforeGlob")

    private val matcher = new AntPathMatcher
    matcher.setPathSeparator(fileSeparator)
    private var objectListing = {
      val s3Request = new ListObjectsRequest()
        .withBucketName(bucket.name)
        .withPrefix(pathBeforeGlob)

      withCatchAmazonExceptions(path, client.listObjects(s3Request))
    }
    private var summariesIterator = objectListing.getObjectSummaries.iterator()

    // Keep track of the number of objects/files found. This does not include directory entries.
    // It is necessary to handle the special cases where the results contain a single file.
    private var filesCount = 0

    // Processing a single object summary may yield several entries in the final result: one file
    // plus zero or more directories. This queue keeps all the entries found.
    private val readyEntries = new util.ArrayDeque[(String, FileSystemMetadata)]()
    // Keeps track of all the directories found so far to ensure each directory is returned only once.
    private val directoriesFound = new java.util.HashSet[String]()

    private def addDirectory(dir: String): Unit = {
      // (CTM) Adding only sanitized directories, was having repeated folders like /path, //path, path/
      val sanitizedDir = sanitizePath(dir)
      if (directoriesFound.add(sanitizedDir)) {
        readyEntries.add((sanitizedDir, new DirectoryMetadata(None)))
      }
    }

    /**
     * Analyses an object summary returned by S3 and adds 1 or more elements to the readyEntries.
     * It will always add a (key, fileMetadata) entry for the object represented by the object summary.
     * It will add 0 or more entries to represent the directories that are on the path of the key
     * of this object.
     */
    private def processObject(file: S3ObjectSummary): Unit = {
      val p = file.getKey
      val meta = FileMetadata(Some(file.getLastModified.toInstant), Some(file.getSize), Array.empty)

      // S3 will not list "directories" when listing objects.
      // Therefore, look at all matching objects and add all intermediate "directories".
      val intermediates = p.split(fileSeparatorRegex)
      var sep = ""
      var cur = ""
      for (i <- 0 until intermediates.length - 1) {
        cur += sep + intermediates(i)
        addDirectory(cur)
        sep = fileSeparator
      }
      filesCount += 1
      readyEntries.add((p, meta))
    }

    private def nextBatch(): Unit = {
      if (!summariesIterator.hasNext) {
        // Try to get the next batch of objects from S3
        if (objectListing.isTruncated) {
          objectListing = withCatchAmazonExceptions(path, client.listNextBatchOfObjects(objectListing))
          summariesIterator = objectListing.getObjectSummaries.iterator()
        }
      }
      while (summariesIterator.hasNext) {
        processObject(summariesIterator.next())
      }
    }

    override def computeNext(): (String, FileSystemMetadata) = {
      while (true) {
        // Ensure there are entries available
        if (readyEntries.isEmpty) {
          nextBatch()
        }
        if (readyEntries.isEmpty) {
          endOfData()
          // If nothing found, throw error if not root of the bucket
          if (filesCount == 0 && sanitizedPath != "") {
            throw new PathNotFoundException(path, null)
          } else {
            // Doesn't matter what we return, we already called endOfData() in the base class,
            // so it will ignore the return of this method
            return null
          }
        } else {
          val entry = readyEntries.remove()
          val key = entry._1
          val md = entry._2
          var passesFilters =
            if (_hasGlob) {
              matcher.`match`(sanitizedPath, key)
            } else {
              val strippedKey = key.stripSuffix(fileSeparator)
              // If we obtained a single file entry and this entry matches the path entry, return it.
              if (filesCount == 1 && key == sanitizedPath && md.isInstanceOf[FileMetadata]) {
                readyEntries.clear()
                return entry
              }
              // Match as directory, i.e. equivalent to '<path>/*'
              if (sanitizedPath == "") {
                // Root case is peculiar. Need to match two patterns.
                matcher.`match`("*", strippedKey) || matcher.`match`(fileSeparator + "*", strippedKey)
              } else {
                val pattern =
                  if (sanitizedPath.endsWith(fileSeparator)) {
                    sanitizedPath + "*"
                  } else {
                    sanitizedPath + fileSeparator + "*"
                  }
                matcher.`match`(pattern, strippedKey)
              }
            }

          // There is at least one match, so add root directory to set
          addDirectory(pathBeforeGlob)

          if (passesFilters) {
            // Filter AWS console convention where files with 0 size and ending in / actually represent "directories"
            passesFilters = md match {
              case FileMetadata(_, Some(0), _) if key.endsWith(fileSeparator) =>
                addDirectory(key)
                false
              case _ => true
            }
          }
          if (passesFilters) {
            return entry
          }
        }
      }
      ???
    }
  }

  @throws[FileSystemException]
  def testBucketAccess(): Unit = {
    try {
      client.listObjects(new ListObjectsRequest().withBucketName(bucket.name))
    } catch {
      case ex: AmazonServiceException if ex.getStatusCode == 403 => throw new PathUnauthorizedException(bucket.name, ex)
      case ex: AmazonServiceException => throw new PathNotFoundException(bucket.name, ex)
      case ex: AmazonClientException => throw new FileSystemUnavailableException(s"s3://${bucket.name}", ex)
    }
  }

  override def testAccess(path: String): Unit = {
    if (listContents(path).isEmpty) {
      throw new PathNotFoundException(path, null)
    }
  }

}
