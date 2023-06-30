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

package raw.compiler.common

import raw.api.{AuthenticatedUser, Service}
import raw.compiler.base.ProgramContext
import raw.compiler.jvm.{RawDelegatingURLClassLoader, RawMutableURLClassLoader}
import raw.compiler.scala2.{Scala2CompilerContext, Scala2JvmCompiler}
import raw.compiler.{ProgramEnvironment, ProgramSettings}
import raw.config.RawSettings
import raw.creds.CredentialsServiceProvider
import raw.inferrer.InferrerServiceProvider
import raw.runtime._
import raw.sources.SourceContext
import raw.sources.bytestream.ByteStreamCache
import raw.utils.RawConcurrentHashMap

import scala.util.control.NonFatal

class CompilerService(implicit settings: RawSettings) extends Service {

  private val credentials = CredentialsServiceProvider()
  private val byteStreamCache = ByteStreamCache(settings)

  private val mutableClassLoader = new RawMutableURLClassLoader(getClass.getClassLoader)
  private val rawClassLoader = new RawDelegatingURLClassLoader(mutableClassLoader)

  private val scala2JvmCompiler = new Scala2JvmCompiler(mutableClassLoader, rawClassLoader)

  // Map of users to compilers.
  private val compilerCaches = new RawConcurrentHashMap[(AuthenticatedUser, String), Compiler]

  def getCompiler(user: AuthenticatedUser, maybeLanguage: Option[String]): Compiler = {
    val language = maybeLanguage.getOrElse("rql2")
    compilerCaches.getOrElseUpdate((user, language), createCompiler(user, language))
  }

  private def createCompiler(user: AuthenticatedUser, language: String): Compiler = {
    // Initialize source context
    implicit val sourceContext = new SourceContext(user, credentials, byteStreamCache, settings)

    // Initialize inferrer
    val inferrer = InferrerServiceProvider()

    // Initialize compiler context
    val compilerContext = new Scala2CompilerContext(language, user, sourceContext, inferrer, scala2JvmCompiler)
    try {
      // Initialize compiler. Default language, if not specified is 'rql2'.
      CommonCompilerProvider(language)(compilerContext)
    } catch {
      case NonFatal(ex) =>
        // To not leave hanging inferrer services.
        // This would make tests fail in the afterAll when checking for running services
        inferrer.stop()
        throw ex
    }
  }

  def getProgramContext(
      compiler: Compiler,
      id: String,
      code: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment
  ): ProgramContext = {
    val programSettings = compiler.getProgramSettings(code, environment)
    val runtimeContext =
      getRuntimeContext(compiler, id, maybeArguments, environment, NullExecutionLogger, programSettings)
    compiler.getProgramContext(programSettings, runtimeContext)
  }

  private def getRuntimeContext(
      compiler: Compiler,
      id: String,
      maybeArguments: Option[Array[(String, ParamValue)]],
      environment: ProgramEnvironment,
      executionLogger: ExecutionLogger,
      programSettings: ProgramSettings
  ): RuntimeContext = {
    val sourceContext = compiler.compilerContext.sourceContext
    new RuntimeContext(
      id,
      sourceContext,
      programSettings,
      executionLogger,
      maybeArguments,
      environment.scopes
    )
  }

  override def doStop(): Unit = {
    compilerCaches.values.foreach(compiler => compiler.compilerContext.inferrer.stop())
    credentials.stop()
    byteStreamCache.stop()
  }

}
