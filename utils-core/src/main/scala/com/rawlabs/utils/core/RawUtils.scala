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

package com.rawlabs.utils.core

import com.google.common.io.Resources
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.FileUtils

import java.io.{ByteArrayOutputStream, InputStream}
import java.math.{MathContext, RoundingMode}
import java.net.URL
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{FileSystemNotFoundException, Files, Path, Paths, StandardCopyOption}
import java.util.Locale
import java.util.concurrent.{ExecutorService, SynchronousQueue, ThreadFactory, ThreadPoolExecutor, TimeUnit}
import java.util.zip.ZipFile
import scala.util.control.NonFatal
import scala.collection.JavaConverters._
import org.apache.commons.text.StringEscapeUtils

/**
 * "Random" collection of utility methods.
 *
 * NOTE that this classes uses the `StrictLogging` trait from the `com.typesafe.scalalogging` package.
 * TODO (msb): Remove StrictLogging and make logger a parameter where needed.
 */
object RawUtils extends StrictLogging {

  /**
   * Convert a user string back to its original "intended" representation.
   * e.g. if the user types "\t" we get the a single '\t' char out instead of the two byte string "\t".
   */
  def escape(s: String): String = StringEscapeUtils.unescapeJava(s)

  /** Does the opposite of the method `escape`. */
  def descape(s: String): String = StringEscapeUtils.escapeJava(s)

  def readEntireFile(path: Path, charset: Charset = StandardCharsets.UTF_8): String = {
    new String(Files.readAllBytes(path), charset)
  }

  /**
   * Creates a cached thread pool that scales the number of threads from `minSize` to `maxSize`. This avoids keeping around
   * idle threads when they are not needed. The factory methods in the `Executors` class do not allow specifying the min and
   * max number of threads, they default to 0 and Integer.MAX_VALUE.
   *
   * @param minSize
   * @param maxSize
   * @param name
   * @return
   */
  def newBoundedCachedThreadPool(minSize: Int, maxSize: Int, name: String): ExecutorService = {
    new ThreadPoolExecutor(
      minSize,
      maxSize,
      60L,
      TimeUnit.SECONDS,
      new SynchronousQueue[Runnable],
      newThreadFactory(name)
    )
  }

  def newThreadFactory(name: String, daemon: Boolean = true): ThreadFactory = {
    new ThreadFactoryBuilder()
      .setNameFormat(s"$name-%d")
      .setUncaughtExceptionHandler((t: Thread, e: Throwable) => {
        logger.warn(s"Uncaught exception on thread: ${t.getName}", e)
      })
      .setDaemon(daemon)
      .build()
  }

  def readInputStreamAsString(inputStream: InputStream, charset: Charset = StandardCharsets.UTF_8): String = {
    val result = new ByteArrayOutputStream()
    val buffer = new Array[Byte](1024)
    var length: Int = inputStream.read(buffer)
    while (length != -1) {
      result.write(buffer, 0, length)
      length = inputStream.read(buffer)
    }
    result.toString(charset.name)
  }

  def endsWithIgnoreCase(str: String, ch: Char): Boolean = {
    val last = str.charAt(str.length - 1)
    Character.toLowerCase(last) == Character.toLowerCase(ch)
  }

  lazy val isWindows: Boolean = {
    System.getProperty("os.name").contains("Windows")
  }

  lazy val isMacOS: Boolean = {
    System.getProperty("os.name").toLowerCase().contains("mac os x")
  }

  def withSuppressNonFatalException(f: => Unit, silent: Boolean = false): Unit = {
    if (Thread.interrupted()) {
      throw new InterruptedException()
    }
    try {
      f
    } catch {
      case NonFatal(t) => if (!silent) {
          logger.warn("Suppressing uncaught exception: ", t)
        }
    }
  }

  def withSuppressNonFatalExceptionUninterruptible(f: => Unit, silent: Boolean = false): Unit = {
    try {
      f
    } catch {
      case NonFatal(t) => if (!silent) {
          logger.warn("Suppressing uncaught exception: ", t)
        }
    }
  }

  def deleteTestPath(path: Path): Unit = {
    if (Files.isDirectory(path)) {
      deleteTestDirectory(path)
    } else {
      deleteTestFile(path)
    }
  }

  def deleteTestDirectory(directory: Path): Unit = {
    withSuppressNonFatalException {
      FileUtils.deleteDirectory(directory.toFile)
    }
  }

  def deleteTestFile(file: Path): Unit = {
    withSuppressNonFatalException {
      val deleted = Files.deleteIfExists(file)
      if (!deleted) {
        logger.warn(s"Could not delete test file: $file")
      }
    }
  }

  def stackTraceToString(t: Throwable, maxStack: Int = Integer.MAX_VALUE): String = {
    val sb = new StringBuilder
    sb.append(t.toString + "\n")
    sb.append(
      t.getStackTrace
        .take(maxStack)
        .mkString("\tat ", "\n\tat ", "")
    )
    if (maxStack < t.getStackTrace.length) {
      sb.append("\n\t...")
    }
    if (t.getCause != null) {
      sb.toString + "\nCaused by: " + stackTraceToString(t.getCause, maxStack)
    } else {
      sb.toString()
    }
  }

  def getTemporaryPath: Path = Paths.get(System.getProperty("java.io.tmpdir"))

  def getTemporaryPath(dirname: String): Path = Paths.get(System.getProperty("java.io.tmpdir"), dirname)

  private val tempDirLock = new Object
  private var tempDir: Path = _

  def getResource(resource: String): Path = {
    val maybeResourcePath = {
      try {
        val resourcePath = Paths.get(Resources.getResource(resource).toURI)
        if (Files.exists(resourcePath)) {
          Some(resourcePath)
        } else {
          None
        }
      } catch {
        case _: FileSystemNotFoundException => None
      }
    }
    maybeResourcePath match {
      case Some(resourcePath) => resourcePath
      case None =>
        // (msb) The following is a hack to load test data.
        // We have test files in src/test/resources but these are in a JAR test dependency.
        // In that case, we cannot read them as a file system: JARs because ZIPs and we cannot just read or list their contents.
        // Therefore, if we are trying to access data/ files, we inspect the classpath for 'raw-sources' JARs.
        // We then open those up, and create a temporary directory where their data is placed.
        // This allows us to read it from that temp directory as if they were in a regular file system.
        if (resource.startsWith("data/")) {
          val classpath = System.getProperty("java.class.path")
          val classpathEntries = classpath.split(System.getProperty("path.separator"))
          val jarFiles = classpathEntries.filter(p => p.contains("snapi-") && p.endsWith("-tests.jar"))
          if (jarFiles.isEmpty) {
            // We are not running in JAR mode, so just return the resource directly.
            Paths.get(Resources.getResource(resource).toURI)
          } else {
            tempDirLock.synchronized {
              // We are running in JAR mode, so read all data/ files in the JARs and copy them over to temporary directories.
              if (tempDir == null) {
                tempDir = Files.createTempDirectory("resource")
                jarFiles.foreach { jarFile =>
                  val jar = new ZipFile(jarFile)
                  try {
                    jar.entries.asScala.foreach { entry =>
                      if (entry.getName.startsWith("data/") && !entry.isDirectory) {
                        val outputPath = tempDir.resolve(entry.getName)
                        Files.createDirectories(outputPath.getParent)
                        val in = jar.getInputStream(entry)
                        try {
                          Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING)
                        } finally {
                          in.close()
                        }
                      }
                    }
                  } finally {
                    jar.close()
                  }
                }
              }
            }
            tempDir.resolve(resource)
          }
        } else {
          // If not running from a JAR, can access it directly.
          Paths.get(Resources.getResource(resource).toURI)
        }
    }
  }

  /**
   * The deleteOnExit() will leave the file around until the JVM exits. This will lead to a large
   * number of tmp files left behind when running tests from InteliJ or in the CI server, which
   * run the tests in the host JVM. There's also the risk of registering a potentially unlimited
   * number of files with the delete hook of the JVM.
   */
  def saveToTemporaryFileNoDeleteOnExit(
      contents: String,
      prefix: String,
      postfix: String,
      charset: Charset = StandardCharsets.UTF_8
  ): Path = {
    val p: Path = Files.createTempFile(prefix, postfix)
    Files.write(p, contents.getBytes(charset))
    p
  }

  /**
   * Creates a temporary file with the given contents, runs the lamdba given in the second argument
   * list, then delete the temporary file.
   */
  def withSavedAsTemporaryFile[T](
      contents: String,
      prefix: String,
      postfix: String,
      charset: Charset = StandardCharsets.UTF_8
  )(f: (Path) => T): T = {
    val path = saveToTemporaryFileNoDeleteOnExit(contents, prefix, postfix, charset)
    try {
      f(path)
    } finally {
      Files.delete(path)
    }
  }

  def readResource(resource: String): String = {
    val release: URL = Resources.getResource(resource)
    Resources.toString(release, StandardCharsets.UTF_8)
  }

  def escapeLanguage(code: String): String = {
    val tquote = "\"\"\""
    s"""$tquote ${descape(code)} $tquote""".stripMargin
  }

  def bytesToString(size: Long): String = bytesToString(BigInt(size))

  def bytesToString(size: BigInt): String = {
    val EiB = 1L << 60
    val PiB = 1L << 50
    val TiB = 1L << 40
    val GiB = 1L << 30
    val MiB = 1L << 20
    val KiB = 1L << 10

    if (size >= BigInt(1L << 11) * EiB) {
      // The number is too large, show it in scientific notation.
      BigDecimal(size, new MathContext(3, RoundingMode.HALF_UP)).toString() + " B"
    } else {
      val (value, unit) = {
        if (size >= 2 * EiB) {
          (BigDecimal(size) / EiB, "EiB")
        } else if (size >= 2 * PiB) {
          (BigDecimal(size) / PiB, "PiB")
        } else if (size >= 2 * TiB) {
          (BigDecimal(size) / TiB, "TiB")
        } else if (size >= 2 * GiB) {
          (BigDecimal(size) / GiB, "GiB")
        } else if (size >= 2 * MiB) {
          (BigDecimal(size) / MiB, "MiB")
        } else if (size >= 2 * KiB) {
          (BigDecimal(size) / KiB, "KiB")
        } else {
          (BigDecimal(size), "B")
        }
      }
      "%.1f %s".formatLocal(Locale.US, value, unit)
    }
  }

  def trimNumbers(v: String): String = {
    val tv = v.trim
    val isNeg = tv.head == '-'
    if (isNeg) {
      val cv = tv.drop(1).dropWhile(_ == '0')
      if (cv.isEmpty) "-0" else "-" + cv
    } else {
      val cv = tv.dropWhile(_ == '0')
      if (cv.isEmpty) "0" else cv
    }
  }

  def getFileNameWithoutExtension(path: Path): String = {
    com.google.common.io.Files.getNameWithoutExtension(path.getFileName.toString)
  }

}
