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

package raw.compiler.rql2.truffle

import org.bitbucket.inkytonik.kiama.util.Positions
import raw.compiler.CompilerParserException
import raw.compiler.base.CompilerContext
import raw.compiler.base.source.{BaseNode, BaseProgram}
import raw.compiler.common.CommonCompilerBuilder
import raw.compiler.rql2.FrontendSyntaxAnalyzer
import raw.compiler.rql2.source.SourcePrettyPrinter

import scala.collection.mutable

class Rql2TruffleCompilerBuilder extends CommonCompilerBuilder {

  override def names: Seq[String] = Seq("rql2-truffle")

  private val compilerCache = mutable.HashMap[CompilerContext, Rql2TruffleCompiler]()
  private val compilerCacheLock = new Object

  override def getCompiler(compilerContext: CompilerContext): Rql2TruffleCompiler = {
    compilerCacheLock.synchronized {
      compilerCache.getOrElseUpdate(compilerContext, new Rql2TruffleCompiler()(compilerContext))
    }
  }

  override def parse(code: String): (BaseProgram, Positions) = {
    val positions = new Positions()
    val syntaxAnalyzer = new FrontendSyntaxAnalyzer(positions)
    syntaxAnalyzer.parse(code) match {
      case Right(p) => (p, positions)
      case Left((err, pos)) => throw new CompilerParserException(err, pos)
    }
  }

  override def prettyPrint(node: BaseNode): String = {
    SourcePrettyPrinter.format(node)
  }

}
