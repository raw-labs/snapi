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

package raw.compiler.truffle

import com.oracle.truffle.api.Truffle
import com.typesafe.scalalogging.StrictLogging
import raw.compiler.base.ProgramContext
import raw.compiler.base.source.BaseNode
import raw.compiler.snapi.truffle.compiler.TruffleEntrypoint
import raw.compiler.{CompilerExecutionException, ProgramOutputWriter}
import raw.runtime.Entrypoint
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException

import java.io.OutputStream

trait TruffleCompiler[N <: BaseNode, P <: N, E <: N] { this: raw.compiler.Compiler[N, P, E] =>

  override def execute(entrypoint: Entrypoint)(implicit programContext: ProgramContext): ProgramOutputWriter = {
    new TruffleProgramOutputWriter(entrypoint.asInstanceOf[TruffleEntrypoint])
  }

}

class TruffleProgramOutputWriter(entrypoint: TruffleEntrypoint)(
    implicit programContext: ProgramContext
) extends ProgramOutputWriter
    with StrictLogging {

  override def writeTo(outputStream: OutputStream): Unit = {
    try {
      try {
        val target = Truffle.getRuntime.createDirectCallNode(entrypoint.target().getCallTarget)
        target.call()
      } catch {
        case ex: RawTruffleRuntimeException =>
          // Instead of passing the cause, we pass null, because otherwise when running Scala2 tests it tries to
          // the AbstractTruffleException which is not exported in JVM (not GraalVM), so it fails.
          throw new CompilerExecutionException(
            ex.getMessage,
            null
          )
      }
    } finally {
      // We explicitly created and then entered the context during code emission.
      // Now we explicitly leave and close the context.
//      entrypoint.context.leave()
//      entrypoint.context.close()
    }
  }
}
