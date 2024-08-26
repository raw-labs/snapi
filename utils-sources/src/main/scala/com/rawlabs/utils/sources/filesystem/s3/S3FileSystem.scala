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

package com.rawlabs.utils.sources.filesystem.s3

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration

import java.net.ConnectException
import com.google.common.collect.AbstractIterator
import org.springframework.util.AntPathMatcher
import com.rawlabs.utils.sources.bytestream.api.{
  DelegatingSeekableInputStream,
  GenericSkippableInputStream,
  SeekableInputStream
}
import com.rawlabs.utils.sources.filesystem.api._
import com.rawlabs.utils.core._
import software.amazon.awssdk.auth.credentials.{
  AnonymousCredentialsProvider,
  AwsBasicCredentials,
  StaticCredentialsProvider
}
import software.amazon.awssdk.core.exception.{SdkClientException, SdkException, SdkServiceException}
import software.amazon.awssdk.http.SdkHttpClient
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.services.s3.model.{
  GetBucketLocationRequest,
  GetObjectRequest,
  ListObjectsV2Request,
  ListObjectsV2Response,
  S3Exception,
  S3Object
}

import java.io.InputStream
import java.util
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._

object S3FileSystem {
  private val CONNECT_TIMEOUT = "raw.utils.sources.s3.connect-timeout"
  private val READ_TIMEOUT = "raw.utils.sources.s3.read-timeout"
  private val MAX_CONNECTIONS = "raw.utils.sources.s3.max-connections"
  private val DEFAULT_REGION = "raw.utils.sources.s3.default-region"
}

class S3FileSystem(
    val bucket: String,
    val maybeRegion: Option[String],
    val maybeAccessKey: Option[String],
    val maybeSecretKey: Option[String]
)(implicit settings: RawSettings)
    extends BaseFileSystem {

  import S3FileSystem._

  private lazy val defaultRegion = settings.getString(DEFAULT_REGION)

  private[sources] val fileSeparator: String = "/"
  private val fileSeparatorRegex: String = RawUtils.descape(fileSeparator)

  private val s3ConnectTimeout = settings.getDuration(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
  private val s3ReadTimeout = settings.getDuration(READ_TIMEOUT, TimeUnit.MILLISECONDS)
  private val s3MaxConnections = settings.getInt(MAX_CONNECTIONS)

  private def guessBucketRegion(): Region = {
    // Try to guess the bucket.
    // That said, we can only get the bucket if we have credentials.
    // If we don't have credentials, we also don't have permissions to find the bucket region.
    if (maybeAccessKey.isDefined && maybeSecretKey.isDefined) {
      val builder = S3Client.builder()
      builder.region(Region.of(defaultRegion))
      builder.credentialsProvider(
        StaticCredentialsProvider.create(AwsBasicCredentials.create(maybeAccessKey.get, maybeSecretKey.get))
      )
      val client = builder.build()
      try {
        val location = client.getBucketLocation(GetBucketLocationRequest.builder().bucket(bucket).build())
        val bucketRegion = location.locationConstraint()
        // 'US_EAST_1' is returned as "null" by AWS SDK
        if (bucketRegion == null || bucketRegion.toString == "null" || bucketRegion.toString.isEmpty) Region.US_EAST_1
        else Region.of(bucketRegion.toString)
      } finally {
        client.close()
      }
    } else {
      Region.of(defaultRegion)
    }
  }

  private lazy val client = {
    val builder = S3Client.builder()

    val s3Config = S3Configuration
      .builder()
      .useArnRegionEnabled(true) // Equivalent to enableForceGlobalBucketAccess in SDK v1
      .build()

    builder.serviceConfiguration(s3Config)

    // Setting the region
    val region = maybeRegion match {
      case Some(regionValue) => Region.of(regionValue)
      case None => guessBucketRegion()
    }
    builder.region(region)

    // Set credentials
    if (maybeAccessKey.isDefined && maybeSecretKey.isDefined) {
      builder.credentialsProvider(
        StaticCredentialsProvider.create(AwsBasicCredentials.create(maybeAccessKey.get, maybeSecretKey.get))
      )
    } else {
      builder.credentialsProvider(AnonymousCredentialsProvider.create())
    }

    val httpClient: SdkHttpClient = ApacheHttpClient
      .builder()
      .socketTimeout(java.time.Duration.ofMillis(s3ConnectTimeout.toInt))
      .connectionTimeout(java.time.Duration.ofMillis(s3ReadTimeout.toInt))
      .maxConnections(s3MaxConnections)
      .build()

    builder.httpClient(httpClient)

//    // Set retry policy
//    builder.retryPolicy(RetryPolicy.defaultRetryPolicy())

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
    // (CTM) Added the stripPrefix from the original file committed long time ago.
    path.replaceAll(s"$fileSeparatorRegex+", fileSeparator).stripSuffix(fileSeparator).stripPrefix(fileSeparator)
  }

  private def getS3ObjectResponse(file: String) = {
    withCatchAmazonExceptions(file, getS3Object(file).response())
  }

  private def getS3Object(file: String) = {
    val getObjectRequest = GetObjectRequest
      .builder()
      .bucket(bucket)
      .key(file)
      .build()

    withCatchAmazonExceptions(file, client.getObject(getObjectRequest))
  }

  private def withCatchAmazonExceptions[R](path: String, f: => R): R = {
    try {
      f
    } catch {
      case ex: S3Exception if ex.statusCode() == 403 => throw new PathUnauthorizedException(path, ex)
      case ex: S3Exception if ex.awsErrorDetails().errorCode() == "NoSuchKey" =>
        throw new PathNotFoundException(path, ex)
      case ex: SdkException => ex.getCause match {
          case cause: ConnectException => throw new FileSystemTimeoutException(Some(path), cause)
          case _ =>
            logger.warn("Unhandled S3 exception", ex)
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
    val s3Object = {
      try {
        getS3ObjectResponse(sanitizedPath)
      } catch {
        case _: PathNotFoundException =>
          listContents(path).hasNext // If there are contents, then try as "directory", which may throw exception
          return DirectoryMetadata(None)
      }
    }
    FileMetadata(
      Option(s3Object.lastModified()),
      Option(s3Object.contentLength()),
      Array.empty
    )
  }

  override def getInputStream(file: String): InputStream = {
    try {
      new S3InputStream(getS3Object(sanitizePath(file)))
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
    logger.trace(s"Path: $path, sanitizedPath: $sanitizedPath (glob: ${_hasGlob}), pathBeforeGlob: $pathBeforeGlob")

    private val matcher = new AntPathMatcher
    matcher.setPathSeparator(fileSeparator)

    private var objectListing: ListObjectsV2Response = {
      val s3Request = ListObjectsV2Request
        .builder()
        .bucket(bucket)
        .prefix(pathBeforeGlob)
        .build()

      withCatchAmazonExceptions(path, client.listObjectsV2(s3Request))
    }

    private var summariesIterator = objectListing.contents().iterator()

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
    private def processObject(file: S3Object): Unit = {
      val p = file.key()
      val meta = FileMetadata(Some(file.lastModified()), Some(file.size()), Array.empty)

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
        // Check if there are more objects to list
        if (objectListing.isTruncated) {
          val nextRequest = ListObjectsV2Request
            .builder()
            .bucket(bucket)
            .prefix(pathBeforeGlob)
            .continuationToken(objectListing.nextContinuationToken())
            .build()

          objectListing = withCatchAmazonExceptions(path, client.listObjectsV2(nextRequest))
          summariesIterator = objectListing.contents().iterator()
        }
      }

      while (summariesIterator.hasNext) {
        val summary = summariesIterator.next()
        processObject(summary) // Ensure processObject is compatible with SDK v2 summary object
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

  def testBucketAccess(): Unit = {
    try {
      client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucket).build())
    } catch {
      case ex: SdkServiceException if ex.statusCode() == 403 => throw new PathUnauthorizedException(bucket, ex)

      case ex: SdkServiceException => throw new PathNotFoundException(bucket, ex)

      case ex: SdkClientException => throw new FileSystemUnavailableException(s"s3://$bucket", ex)
    }
  }

  override def testAccess(path: String): Unit = {
    if (listContents(path).isEmpty) {
      throw new PathNotFoundException(path, null)
    }
  }

}
