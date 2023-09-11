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

package raw.compiler.scala2

import org.apache.commons.io.FileUtils
import raw.api.RawException
import raw.compiler.CompilerException
import raw.compiler.jvm.{JvmCode, JvmCompiler, RawMutableURLClassLoader}
import raw.config.RawSettings
import raw.runtime.JvmEntrypoint

import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.{FileSystems, Files, Path, Paths}
import java.util.UUID
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.{Global, Settings}
import scala.collection.mutable
import scala.collection.JavaConverters._

final case class ScalaCode(code: String) extends JvmCode

object Scala2JvmCompiler {
  private val SCALA2_COMPILER_SETTINGS = "raw.compiler.scala2.settings"
  private val SCALA2_MAX_CLASSES_ON_STARTUP = "raw.compiler.scala2.max-classes-on-startup"
  private val SCALA2_COMPILATION_DIRECTORY = "raw.compiler.scala2.compilation-directory"
  private val SCALA2_CLASSPATH = "raw.compiler.scala2.classpath"

  // Unique counter (per prefix).
  private val counterByIdn = mutable.HashMap[String, Int]()
  private val counterByIdnLock = new Object

  def next(prefix: String, suffix: String): String = {
    counterByIdnLock.synchronized {
      if (!counterByIdn.contains(prefix)) {
        counterByIdn.put(prefix, 0)
      }
      val n = counterByIdn(prefix)
      counterByIdn.put(prefix, n + 1)
      if (suffix.isEmpty) {
        s"_${prefix}_$$$n"
      } else {
        s"_${prefix}_$$$n$$$suffix"
      }
    }
  }

}

/**
 * Compiler of Scala code.
 */
class Scala2JvmCompiler(
    mutableClassLoader: RawMutableURLClassLoader,
    val classLoader: ClassLoader
)(
    implicit settings: RawSettings
) extends JvmCompiler {

  import Scala2JvmCompiler._

  private val compilerCmdSettings = settings.getString(SCALA2_COMPILER_SETTINGS)

  private val maxClassesOnStartup = settings.getInt(SCALA2_MAX_CLASSES_ON_STARTUP)

  // Create a unique base directory
  private val baseDir = Paths.get(settings.getString(SCALA2_COMPILATION_DIRECTORY)).resolve("20221027T1030")

  // Location for the generated Scala sources
  private val sourceDir = baseDir.resolve("src")

  // Location for the compiled class files
  private val classDir = baseDir.resolve("classes")

  // Initialize compiler
  private var scalacSettings: Settings = _
  private var compilerReporter: StoreReporter = _
  private var compiler: Global = _

  initializeCompiler()

  private def initializeCompiler(): Unit = {
    scalacSettings = new Settings
    scalacSettings.embeddedDefaults(classLoader)
    settings.getStringList(SCALA2_CLASSPATH).foreach(p => scalacSettings.classpath.append(p))

    // Needed for running unit tests.
    scalacSettings.usejavacp.value = true
    // optWarnings Possible values:
    // none, at-inline-failed-summary, at-inline-failed, any-inline-failed, no-inline-mixed,
    // no-inline-missing-bytecode, no-inline-missing-attribute)
    // scalacSettings.optWarnings.enable(scalacSettings.optWarningsChoices.anyInlineFailed)

    // Emit deprecation warnings (disabling for performance)
    //  scalacSettings.deprecation.value = true

    // Limit the size to avoid exceeding the max size in Linux volumes encrypted with eCryptFs.
    // https://unix.stackexchange.com/a/32834
    scalacSettings.maxClassfileName.value = 140
    //settings.showPlugins only works if you're not compiling a file, same as -help

    val (success, unprocessed) = scalacSettings.processArgumentString(compilerCmdSettings)
    if (!success) {
      throw new CompilerException(
        s"Unprocessed compiler settings: $unprocessed. Original settings: $compilerCmdSettings"
      )
    }
    logger.info("Compiler settings:\n" + scalacSettings.toString())

    // TODO: StoreReporter does not print all compilation messages, the ConsoleReporter is better but
    // we cannot easily intersect the output like with the store reporter. Check if we can combine them.
    compilerReporter = new StoreReporter(scalacSettings)

    withLockedDirectory(() => {

      def createDirectoriesIfNotExist(): Unit = {
        Files.createDirectories(sourceDir)
        if (!Files.isDirectory(sourceDir)) {
          throw new RawException(s"Source output directory not found: ${sourceDir.toString}")
        }
        Files.createDirectories(classDir)
        if (!Files.isDirectory(classDir)) {
          throw new RawException(s"Class output directory not found: ${classDir.toString}")
        }
      }

      // Prepare compilation directories.
      createDirectoriesIfNotExist()

      // Find all existing classes in code cache.
      // If there are far too many classes, we delete them all and start over again fresh.
      // This is done to prevent having too many classes loaded.
      // This heuristic assumes there are many "old classes around" that are no longer needed.
      val classesToLoad = listAllClassesInCache()
      if (maxClassesOnStartup >= 0 && classesToLoad.size > maxClassesOnStartup) {
        logger.info(
          s"Maximum number of classes on startup reached (found: ${classesToLoad.size}; limit: $maxClassesOnStartup). Deleting and recreating."
        )
        FileUtils.deleteDirectory(sourceDir.toFile)
        FileUtils.deleteDirectory(classDir.toFile)
        createDirectoriesIfNotExist()
      } else {
        logger.debug(s"${classesToLoad.length} classes loaded.")
        loadClasses(classesToLoad)
      }
    })

    // Create an instance of the compiler in advance of receiving a request. Creating a compiler
    // takes 200 to 400ms, which is sufficient to be noticeable in interactive sessions.
    // Additionally, using a single thread for compilation may make more efficient use of the
    // CPU caches, if the OS keeps this thread running in the same core.
    compiler = new Global(scalacSettings, compilerReporter)
    logger.trace("Compiler classpath: " + compiler.classPath.asURLs.mkString("\n"))
  }

  private def listAllClassesInCache(): Seq[Path] = {
    // Find all existing classes in code cache.
    // Looks up over our 3 part sub-directory structure (see method doCompile)
    val matcher = FileSystems.getDefault.getPathMatcher(s"glob:$classDir/*/*/*")
    Files
      .walk(classDir)
      .iterator()
      .asScala
      .filter(f => matcher.matches(f) && Files.isDirectory(f))
      .toSeq
  }

  private def loadClasses(classesToLoad: Seq[Path]): Unit = {
    classesToLoad.foreach { cp =>
      val id = cp.getFileName.toString
      if (!isClassCompiled(id)) {
        logger.trace(s"Loading class at $cp")

        // Add to list of compiled classes.
        addClassCompiled(id)

        // Add new output class directory to the classloader so its reachable.
        mutableClassLoader.addURL(cp.toUri.toURL)

        // Add to Scala2 compiler classpath, so that its found when compiling new code..
        scalacSettings.classpath.append(cp.toString)
      }
    }
  }

  def loadEntrypoint(entrypoint: JvmEntrypoint): Class[_] = {
    Thread.currentThread().setContextClassLoader(classLoader)
    classLoader.loadClass(entrypoint.className)
  }

  override protected def doCompile(id: String, jvmCode: JvmCode): Unit = {
    val scalaCode = jvmCode.asInstanceOf[ScalaCode]

    // Split the 'id' into 3 parts.
    // Since 'id' is variable length, we do it in a somewhat flexible manner.
    val idSubLen = id.length / 3
    val idPart1 = id.substring(0, idSubLen)
    val idPart2 = id.substring(idSubLen, 2 * idSubLen)

    withLockedDirectory(() => {

      // Directory structure to prevent having too many files in a single directory.
      val programSourcePath = sourceDir.resolve(idPart1).resolve(idPart2).resolve(id)
      val programClassPath = classDir.resolve(idPart1).resolve(idPart2).resolve(id)

      // Just before emitting the code, check if it already exists on DISK.
      // This could happen if:
      // a) in the past, a previous run left files on disk that generated this class.
      // b) another process concurrently generated this class and we don't know about it (?)
      // If this happens, then reload all classes once again - just as we do during startup -, which means the one
      // we needed is now available. We load all classes because this class may itself depend on other classes.
      //  Since we are under a directory lock, this is a safe operation.
      if (Files.exists(programClassPath)) {
        logger.debug(s"Found code cache hit for $id. Re-loading all code caches and skipping Scala compilation.")
        // Load everything again.
        // Why load everything? Not strictly needed but this way we load all dependencies as well.
        loadClasses(listAllClassesInCache())
        // Nothing more to do since the code is now loaded and available.
        return
      }

      // Create directories to contain program source and class files.
      val programSourceDir = Files.createDirectories(programSourcePath)
      val programClassDir = Files.createDirectories(programClassPath)

      // Small workaround for Scala not letting us do 'new compiler.Run()'
      val c = compiler
      val run = new c.Run()

      // Set compiler to use specific output directory for specific source directory.
      scalacSettings.outputDirs.add(programSourceDir.toAbsolutePath.toString, programClassDir.toAbsolutePath.toString)

      // Save source file
      val srcFile = programSourceDir.resolve(s"$id.scala")
      Files.write(srcFile, scalaCode.code.getBytes(StandardCharsets.UTF_8))
      val srcPath = srcFile.toAbsolutePath.toString
      if (logger.underlying.isDebugEnabled) {
        logger.debug(s"Compiling source file: $srcPath:\n${scalaCode.code}")
      } else {
        logger.debug("Compiling Scala code")
      }

      // Run scalac compiler
      run.compile(List(srcPath))

      if (compilerReporter.hasErrors) {
        val errorMessage =
          if (compilerReporter.cancelled) {
            logger.warn("Compilation cancelled.")
            "Compilation cancelled"
          } else {
            logger.warn("Compilation failed.")
            // Log each error separately as they can be very large.
            // Concatenating them could failed with "UTF8 String too large".
            compilerReporter.infos.foreach(info => logger.warn(info.toString()))
            "Compilation failed"
          }

        // The reporter keeps the state between runs, so it must be explicitly reset so that errors from previous
        // compilation runs are not falsely reported in the subsequent runs
        compilerReporter.reset()

        throw new Exception(errorMessage)
      } else {
        // Compilation succeeded
        if (compilerReporter.hasWarnings) {
          val compilerWarnings = compilerReporter.infos.mkString("\n")
          logger.warn(s"Compilation completed with warnings. Compiler output:\n$compilerWarnings")
          compilerReporter.reset()
        }

        // Add new output class directory to the classpath
        mutableClassLoader.addURL(programClassDir.toUri.toURL)
      }
    })
  }

  /**
   * Retrieve a unique identifiers not used in any previous compilation.
   *
   * @param prefix Prefix of identifier.
   * @param suffix Suffix of identifier.
   * @return Newly-generated unique identifier.
   */
  def nextUniqueIdn(prefix: String = "", suffix: String = ""): String = {
    next(prefix, suffix)
  }

  def nextClassUniqueIdn(name: String): String = {
    val uuid = UUID.randomUUID().toString.replace("-", "").replace("_", "")
    s"$name$uuid"
  }

  private def withLockedDirectory[T](f: () => T): T = {
    if (!Files.isDirectory(baseDir)) {
      Files.createDirectories(baseDir)
    }
    val lockFile = baseDir.resolve(".lock").toFile
    logger.debug(s"Acquiring compiler lock (on $lockFile)")
    val fileOutputStream = new FileOutputStream(lockFile)
    try {
      val channel = fileOutputStream.getChannel
      try {
        val lock = channel.lock
        try {
          f()
        } finally {
          lock.close()
          logger.debug(s"Releasing compiler lock (on $lockFile)")
        }
      } finally {
        channel.close()
      }
    } finally {
      fileOutputStream.close()
    }
  }

}
