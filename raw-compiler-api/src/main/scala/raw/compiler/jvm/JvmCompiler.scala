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

package raw.compiler.jvm

import com.typesafe.scalalogging.StrictLogging
import raw.config.RawSettings
import raw.utils.RawUtils

import java.util.concurrent._

trait JvmCode {
  def code: String
}

object JvmCompiler {

  private val COMPILATION_TIMEOUT = "raw.compiler.jvm.compilation-timeout"

  // Compilation thread pool.
  private val compilerThreadPool = Executors.newSingleThreadScheduledExecutor(
    RawUtils.newThreadFactory("jvm-compiler")
  )

}

abstract class JvmCompiler(implicit settings: RawSettings) extends StrictLogging {

  import JvmCompiler._

  private val compilationTimeoutMillis = settings.getDuration(COMPILATION_TIMEOUT, TimeUnit.MILLISECONDS)

  protected val compiledClasses = new ConcurrentHashMap[String, Unit]()

  /**
   * Perform the compilation.
   *
   * @param id Unique identifier.
   * @param jvmCode The code to compile.
   * @return JAR path.
   */
  protected def doCompile(id: String, jvmCode: JvmCode): Unit

  /**
   * Compile and load the code.
   *
   * @param id Unique identifier.
   * @param code The code to compile.
   */
  def compile(id: String, code: => JvmCode): Unit = {
    // Check if class already compiled and loaded.
    if (isClassCompiled(id)) {
      logger.debug(s"Class $id already compiled and loaded. Reusing.")
      return
    }

    // If not, create new compilation task and wait its termination.
    val compileTask = new CompileTask(id, code)
    val future = compilerThreadPool.submit(compileTask)
    try {
      future.get(compilationTimeoutMillis, TimeUnit.MILLISECONDS)
    } catch {
      case ex: CancellationException =>
        logger.warn("Compilation cancelled", ex)
        throw ex
      case ex: InterruptedException =>
        // Scala code compilation interrupted. Killing compilation task.
        future.cancel(true)
        throw ex
      case ex: ExecutionException => throw if (ex.getCause == null) ex else ex.getCause
      case te: TimeoutException =>
        logger.warn(s"Query is taking too long to compile (timeout is $compilationTimeoutMillis ms). Aborting.")
        future.cancel(true)
        throw te
    }
  }

  private class CompileTask(id: String, jvmCode: JvmCode) extends Callable[Unit] {
    override def call(): Unit = {
      // Compile code and obtain jar file.
      doCompile(id, jvmCode)

      // Now that the jar has been loaded successfully, the class is available for others to use.
      addClassCompiled(id)
    }
  }

  protected def isClassCompiled(id: String): Boolean = {
    compiledClasses.containsKey(id)
  }

  protected def addClassCompiled(id: String): Unit = {
    compiledClasses.put(id, Unit)
  }

}
