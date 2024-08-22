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

package com.rawlabs.utils.sources.filesystem.local

import org.springframework.util.AntPathMatcher
import com.rawlabs.utils.sources.bytestream.api.{DelegatingSeekableInputStream, SeekableInputStream}
import com.rawlabs.utils.sources.filesystem.api._
import com.rawlabs.utils.core._

import java.io._
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import scala.collection.mutable.ArrayBuffer

object LocalFileSystem extends BaseFileSystem {

  private[sources] val fileSeparator: String = File.separator

  private val fileSeparatorRegex: String = RawUtils.descape(fileSeparator)

  private def sanitizePath(path: String): String = {
    // Currently there's no need to sanitize path because the Java APIs handle it for us.
    if (RawUtils.isWindows) path.replaceFirst("^/(.:/)", "$1")
    else path
  }

  private def handleException(path: String, e: Exception) = e match {
    case _: FileNotFoundException | _: NoSuchFileException => throw new PathNotFoundException(path, e)
    case _: FileAlreadyExistsException => throw new PathAlreadyExistsException(path, e)
    case _: SecurityException | _: ReadOnlyFileSystemException | _: AccessDeniedException =>
      throw new PathUnauthorizedException(path, e)
    case _: InvalidPathException => throw new PathInvalidException(path, e)
    case _: IOException => throw new UnexpectedFileSystemErrorException(e)
    case _ => throw e
  }

  override def testAccess(path: String): Unit = {
    getPath(path)
  }

  private def getPath(path: String): Path = {
    try {
      val p = Paths.get(path)
      if (!Files.exists(p) || (!Files.isDirectory(p) && !Files.isRegularFile(p)))
        throw new PathNotFoundException(path, null)
      else if (!Files.isReadable(p) || !p.isAbsolute) throw new PathUnauthorizedException(path, null)
      else p
    } catch {
      case ex: Exception => handleException(path, ex)
    }
  }

  override def metadata(path: String): FileSystemMetadata = {
    val sanitizedPath = sanitizePath(path)
    val p = getPath(sanitizedPath)
    try {
      if (Files.isDirectory(p)) {
        val modified = Files.getLastModifiedTime(p)
        DirectoryMetadata(Some(modified.toInstant))
      } else {
        val modified = Files.getLastModifiedTime(p)
        val size = Files.size(p)
        FileMetadata(Some(modified.toInstant), Some(size), Array.empty)
      }
    } catch {
      case ex: Exception => handleException(path, ex)
    }
  }

  override def listContents(path: String): Iterator[String] = {
    try {
      val sanitizedPath = sanitizePath(path)
      if (!hasGlob(sanitizedPath)) {
        val p = getPath(sanitizedPath)
        if (Files.isDirectory(p)) {
          // List path contents
          val stream = Files.list(p)
          try {
            stream
              .map[String](_.toAbsolutePath.toString)
              .toArray(new Array[String](_))
              .iterator
          } finally {
            stream.close()
          }
        } else {
          Iterator(sanitizedPath)
        }
      } else {
        val pathBeforeGlob = sanitizedPath
          .split(fileSeparatorRegex)
          .takeWhile(p => !hasGlob(p))
          .mkString(fileSeparator)
        val matcher = new AntPathMatcher
        matcher.setPathSeparator(fileSeparator)
        val matches = new ArrayBuffer[String]()
        Files.walkFileTree(
          getPath(pathBeforeGlob),
          new SimpleFileVisitor[Path] {
            override def visitFileFailed(file: Path, exc: IOException): FileVisitResult = {
              FileVisitResult.SKIP_SUBTREE
            }
            override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
              val absPath = dir.toAbsolutePath.toString
              if (matcher.`match`(sanitizedPath, absPath)) {
                matches += absPath
                FileVisitResult.CONTINUE
              } else if (matcher.matchStart(sanitizedPath, absPath)) {
                FileVisitResult.CONTINUE
              } else {
                FileVisitResult.SKIP_SUBTREE
              }
            }

            override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
              val absPath = file.toAbsolutePath.toString
              if (matcher.`match`(sanitizedPath, absPath)) {
                matches += absPath
              }
              FileVisitResult.CONTINUE
            }
          }
        )
        matches.iterator
      }
    } catch {
      case ex: Exception => handleException(path, ex)
    }
  }

  override def getInputStream(file: String): FileInputStream = {
    val sanitizedPath = sanitizePath(file)
    val path = getPath(sanitizedPath)
    try {
      if (Files.isDirectory(path)) throw new NotAFileException(sanitizedPath, null)
      new FileInputStream(path.toFile)
    } catch {
      case ex: Exception => handleException(sanitizedPath, ex)
    }
  }

  override def getSeekableInputStream(pathName: String): SeekableInputStream = {
    val is = getInputStream(pathName)
    new DelegatingSeekableInputStream(is) {

      def getPos: Long = is.getChannel.position()

      def seek(newPos: Long): Unit = is.getChannel.position(newPos)
    }
  }

}
