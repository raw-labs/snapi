/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.compiler.rql2

import org.graalvm.polyglot.{Context, PolyglotAccess, PolyglotException, Source}
import raw.client.api.{CompilerService, ErrorMessage, ErrorPosition, ErrorRange, ProgramEnvironment, RawType, RawValue}
import raw.compiler.rql2.antlr4.ParserErrors

import raw.utils.RawSettings

/**
 * Support for stage compilation.
 * This allows an RQL2/Snapi source to be evaluated in a new context, with a given environment and RawSettings.
 */
trait StagedCompiler {

  /**
   * Evaluate the given source code in a new context.
   * The source code is expected to be a valid RQL2/Snapi program.
   * The result is a RawValue.
   *
   * If the Truffle runtime execution thread is interrupted, an InterruptedException is thrown.
   *
   * @param source      The source code to evaluate.
   * @param tipe        The expected type of the result.
   * @param environment The environment to use for the evaluation.
   * @param settings    The settings to use for the evaluation.
   * @return A StagedCompilerResponse.
   */
  def eval(source: String, tipe: RawType, environment: ProgramEnvironment)(
      implicit settings: RawSettings
  ): StagedCompilerResponse = {
    val (engine, _) = CompilerService.getEngine()

    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("rql")
      .engine(engine)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    environment.options.get("staged-compiler").foreach { stagedCompiler =>
      ctxBuilder.option("rql.staged-compiler", stagedCompiler)
    }
    ctxBuilder.option("rql.settings", settings.renderAsString)

    val ctx = ctxBuilder.build()
    ctx.initialize("rql")
    ctx.enter()
    try {
      val truffleSource = Source
        .newBuilder("rql", source, "unnamed")
        .cached(false) // Disable code caching because of the inferrer.
        .build()
      val polyglotValue = ctx.eval(truffleSource)
      val rawValue = CompilerService.polyglotValueToRawValue(polyglotValue, tipe)
      StagedCompilerSuccess(rawValue)
    } catch {
      case ex: PolyglotException =>
        // (msb): The following are various "hacks" to ensure the inner language InterruptException propagates "out".
        // Unfortunately, I do not find a more reliable alternative; the branch that does seem to work is the one
        // that does startsWith. That said, I believe with Truffle, the expectation is that one is supposed to
        // "cancel the context", but in our case this doesn't quite match the current architecture, where we have
        // other non-Truffle languages and also, we have parts of the pipeline that are running outside of Truffle
        // and which must handle interruption as well.
        if (ex.isInterrupted) {
          throw new InterruptedException()
        } else if (ex.getCause.isInstanceOf[InterruptedException]) {
          throw ex.getCause
        } else if (ex.getMessage.startsWith("java.lang.InterruptedException")) {
          throw new InterruptedException()
        } else if (ex.isGuestException) {
          val err = ex.getGuestObject
          if (err != null && err.hasMembers && err.hasMember("errors")) {
            val errorsValue = err.getMember("errors")
            val errors = (0L until errorsValue.getArraySize).map { i =>
              val errorValue = errorsValue.getArrayElement(i)
              val message = errorValue.asString
              val positions = (0L until errorValue.getArraySize).map { j =>
                val posValue = errorValue.getArrayElement(j)
                val beginValue = posValue.getMember("begin")
                val endValue = posValue.getMember("end")
                val begin = ErrorPosition(beginValue.getMember("line").asInt, beginValue.getMember("column").asInt)
                val end = ErrorPosition(endValue.getMember("line").asInt, endValue.getMember("column").asInt)
                ErrorRange(begin, end)
              }
              ErrorMessage(message, positions.to, ParserErrors.ParserErrorCode)
            }
            StagedCompilerValidationFailure(errors.to)
          } else {
            StagedCompilerRuntimeFailure(ex.getMessage)
          }
        } else {
          // Unexpected error. For now we throw the PolyglotException.
          throw ex
        }
    } finally {
      ctx.leave()
      ctx.close()
    }
  }
}

sealed trait StagedCompilerResponse
final case class StagedCompilerSuccess(v: RawValue) extends StagedCompilerResponse
final case class StagedCompilerValidationFailure(errors: List[ErrorMessage]) extends StagedCompilerResponse
final case class StagedCompilerRuntimeFailure(error: String) extends StagedCompilerResponse
