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

package raw.sources.filesystem.dropbox

import com.dropbox.core._
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.dropbox.core.v2.files.{DownloadErrorException, FolderMetadata, Metadata, FileMetadata => DropboxFileMetadata}
import raw.config.RawSettings
import raw.creds.{BasicAuth, BearerToken, NewHttpAuth}
import raw.sources.bytestream.{DelegatingSeekableInputStream, GenericSkippableInputStream, SeekableInputStream}
import raw.sources.filesystem._

import java.io.InputStream
import scala.collection.JavaConverters._
import scala.collection.mutable

object DropboxFileSystem {
  private val DROPBOX_CLIENT_ID = "raw.sources.dropbox.clientId"

  // Method also used by testing infrastructure
  private[sources] def buildDbxClientV2(cred: NewHttpAuth)(implicit settings: RawSettings): DbxClientV2 = {
    val clientId = settings.getString(DROPBOX_CLIENT_ID)
    val dropboxConfig = DbxRequestConfig.newBuilder(clientId).build()
    val dbxCred = cred match {
      case BasicAuth(user, password, _) => new DbxCredential(null, null, null, user, password)
      case BearerToken(token, _) => new DbxCredential(token)
      case _ => ???
    }
    new DbxClientV2(dropboxConfig, dbxCred)
  }
}

// TODO: Catch unauthorized to throw specific exception?
class DropboxFileSystem(cred: NewHttpAuth, val name: String = "")(implicit settings: RawSettings)
    extends BaseFileSystem {

  import DropboxFileSystem._

  // TODO: this is arguable for Windows users...
  private[sources] val fileSeparator: String = "/"
  private val fileSeparatorRegex: String = "/"

  private lazy val client = buildDbxClientV2(cred)

  private def sanitizePath(path: String): String = {
    val p = path.replaceAll(s"$fileSeparatorRegex+", fileSeparator).stripSuffix(fileSeparator)
    if (p.isEmpty) fileSeparator else p
  }

  private def getMetadata(path: String): Metadata = {
    try {
      client.files().getMetadata(path)
    } catch {
      case ex: DbxException =>
        logger.warn(s"Failed gracefully to get Dropbox path metadata: $path.", ex)
        throwSourceException(ex, path)
      case ex: IllegalArgumentException =>
        // Dropbox fails "other exceptions" as well when path isn't accessible/correct.
        logger.warn(s"Failed unexpectedly to get Dropbox path metadata: $path.", ex)
        throw new PathNotFoundException(path, ex)
    }
  }

  override def metadata(path: String): FileSystemMetadata = {
    val sanitizedPath = sanitizePath(path)
    if (sanitizedPath == "/") return DirectoryMetadata(None)
    getMetadata(sanitizedPath) match {
      case m: DropboxFileMetadata => FileMetadata(Some(m.getClientModified.toInstant), Some(m.getSize), Array.empty)
      case _: FolderMetadata => DirectoryMetadata(None)
      case _ => throw new PathNotFoundException(path, null)
    }
  }

  override def getInputStream(file: String): InputStream = {
    try {
      client.files().download(sanitizePath(file)).getInputStream
    } catch {
      // TODO: This misses NotAFileException I believe?
      case ex: DbxException =>
        logger.warn(s"Failed to get Dropbox file inputstream: $file.", ex)
        throwSourceException(ex, file)
      case ex: IllegalArgumentException => throw new PathInvalidException(file, ex)

    }
  }

  override def getSeekableInputStream(path: String): SeekableInputStream = {
    val skipableInputStream = new GenericSkippableInputStream(() => getInputStream(path))
    new DelegatingSeekableInputStream(skipableInputStream) {
      override def getPos: Long = skipableInputStream.getPos

      override def seek(newPos: Long): Unit = skipableInputStream.seek(newPos)
    }
  }

  override def listContents(path: String): Iterator[String] = {
    listContentsWithMetadata(path).map(_._1)
  }

  override def listContentsWithMetadata(path: String): Iterator[(String, FileSystemMetadata)] = {
    val sanitizedPath = sanitizePath(path)
    if (!hasGlob(sanitizedPath)) {
      // If no glob, then it is either a file or a directory.
      getContents(sanitizedPath, recursive = false)
    } else {
      // If it has glob, obtain basepath before glob, list recursively and apply our own walker filter.
      val pathBeforeGlob = sanitizedPath
        .dropWhile(_ == '/')
        .split("/")
        .takeWhile(p => !hasGlob(p))
        .mkString("/", "/", "")
      val matcher = new AntPathMatcher
      matcher.setPathSeparator(fileSeparator)
      getContents(pathBeforeGlob, recursive = true)
        .filter(p => matcher.`match`(sanitizedPath, p._1))
    }
  }

  private def getContents(path: String, recursive: Boolean): Iterator[(String, FileSystemMetadata)] = {
    if (path == "/") {
      listFolder("", recursive)
    } else {
      assert(!hasGlob(path))
      getMetadata(path) match {
        case fm: DropboxFileMetadata => Iterator(
            (fm.getPathDisplay, FileMetadata(Some(fm.getClientModified.toInstant), Some(fm.getSize), Array.empty))
          )
        case fm: FolderMetadata => listFolder(fm.getPathDisplay, recursive)
      }
    }
  }

  private def listFolder(path: String, recursive: Boolean): Iterator[(String, FileSystemMetadata)] = {
    assert(!hasGlob(path))
    val files = mutable.ListBuffer[(String, FileSystemMetadata)]()
    var continue = true
    var lfr =
      try {
        if (recursive) client.files().listFolderBuilder(path).withRecursive(true).start()
        else client.files().listFolder(path)
      } catch {
        case ex: DbxException =>
          logger.warn(s"Failed to list Dropbox folder: $path.", ex)
          throwSourceException(ex, path)
      }
    while (continue) {
      lfr.getEntries.asScala.foreach {
        case m: DropboxFileMetadata => files.append(
            (m.getPathDisplay, FileMetadata(Some(m.getClientModified.toInstant), Some(m.getSize), Array.empty))
          )
        case m: FolderMetadata => files.append((m.getPathDisplay, DirectoryMetadata(None)))
        case _ =>
      }
      if (lfr.getHasMore) {
        lfr =
          try {
            client.files().listFolderContinue(lfr.getCursor)
          } catch {
            case ex: DbxException =>
              logger.warn(s"Failed to continue to list Dropbox folder: $path.", ex)
              throwSourceException(ex, path)
          }
      } else {
        continue = false
      }
    }
    files.to
  }

  private def throwSourceException(ex: DbxException, path: String) = {
    ex match {
      case _ @(_: ServerException | _: RetryException | _: NetworkIOException) =>
        throw new FileSystemUnavailableException(path, ex)
      case _: InvalidAccessTokenException => throw new PathUnauthorizedException(path, ex)
      case e: DownloadErrorException =>
        // TODO: This can't be handled generally here! Only where we want files...
        if (e.errorValue.getPathValue.isNotFile) throw new NotAFileException(path, e)
        else throw new PathNotFoundException(path, e)
      case _ => // Everything else, assume file does not exist
        throw new PathNotFoundException(path, ex)
    }
  }

  override def testAccess(path: String): Unit = {
    listContents(path) // Throws exception if not found.
  }

}
