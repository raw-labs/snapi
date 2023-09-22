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

package raw.compiler.rql2.tests

import com.oracle.truffle.api.Truffle
import org.graalvm.polyglot.Context
import raw.compiler.rql2.Rql2OutputTestContext
import raw.runtime.ParamValue
import raw.runtime.truffle.RawLanguage

import java.nio.file.Path

class TruffleCompilerTestContext extends CompilerTestContext with Rql2OutputTestContext {
  override def language: String = "rql2-truffle"

  override def doExecute(
      query: String,
      maybeDecl: Option[String],
      maybeArgs: Option[Seq[(String, ParamValue)]],
      savePath: Option[Path],
      options: Map[String, String],
      scopes: Set[String]
  ): Either[String, Path] = {
    val ctx: Context = Context.newBuilder(RawLanguage.ID).build()
    ctx.initialize(RawLanguage.ID)
    ctx.enter()
    try {
      super.doExecute(query, maybeDecl, maybeArgs, savePath, options, scopes)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

}
