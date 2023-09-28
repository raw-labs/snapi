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

import org.graalvm.polyglot.Context
import raw.compiler.base.source.Type
import raw.compiler.{LSPRequest, LSPResponse}
import raw.compiler.rql2.Rql2OutputTestContext
import raw.runtime.ParamValue
import raw.runtime.truffle.RawLanguage

import java.nio.file.Path

class TruffleCompilerTestContext extends CompilerTestContext with Rql2OutputTestContext {
  override def language: String = "rql2-truffle"

  override def tryToType(s: String): Either[Seq[String], Type] = {
    withTruffleContext(() => super.tryToType(s))
  }

  override def doLsp(request: LSPRequest): LSPResponse = {
    withTruffleContext(() => super.doLsp(request))
  }

  override def doValidate(
      query: String,
      options: Map[String, String],
      scopes: Set[String]
  ): Either[String, Option[String]] = {
    withTruffleContext(() => super.doValidate(query, options, scopes))
  }

  override def doExecute(
      query: String,
      maybeDecl: Option[String],
      maybeArgs: Option[Seq[(String, ParamValue)]],
      savePath: Option[Path],
      options: Map[String, String],
      scopes: Set[String]
  ): Either[String, Path] = {
    withTruffleContext(() => super.doExecute(query, maybeDecl, maybeArgs, savePath, options, scopes))
  }

  override def callDecl(code: String, decl: String, args: Seq[(String, ParamValue)]): Either[String, Any] = {
    withTruffleContext(() => super.callDecl(code, decl, args))
  }

  override def fastExecute(
      query: String,
      maybeDecl: Option[String],
      savePath: Option[Path],
      options: Map[String, String],
      scopes: Set[String]
  ): Either[String, Path] = {
    withTruffleContext(() => super.fastExecute(query, maybeDecl, savePath, options, scopes))
  }

  private def withTruffleContext[T](f: () => T): T = {
    val ctx: Context = Context.newBuilder(RawLanguage.ID).build()
    ctx.initialize(RawLanguage.ID)
    ctx.enter()
    try {
      f()
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

}
