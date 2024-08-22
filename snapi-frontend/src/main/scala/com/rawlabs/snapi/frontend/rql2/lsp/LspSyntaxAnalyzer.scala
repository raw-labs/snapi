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

package com.rawlabs.snapi.frontend.rql2.lsp

import org.bitbucket.inkytonik.kiama.util.Positions
import com.rawlabs.snapi.frontend.base.source.{BaseProgram, Type}
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2.{FrontendSyntaxAnalyzer, ParsedNamedAttribute, ParsedUnnamedAttribute}
import com.rawlabs.snapi.frontend.rql2.FrontendSyntaxAnalyzerTokens._
import com.rawlabs.snapi.frontend.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}
import com.rawlabs.snapi.frontend.rql2.source._

/**
 * Version of the FrontendSyntaxAnalyzer that accepts "broken code" but still tries to parse it successfully,
 * so that we can still obtain an AST and type it for LSP actions.
 *
 * Whenever an Exp cannot be parsed, it is replaced by ErrorExp
 */
class LspSyntaxAnalyzer(positions: Positions) extends FrontendSyntaxAnalyzer(positions) {

  final override lazy val program: Parser[BaseProgram] = rep(rql2Method) ~ opt(exp) ^^ {
    case ms ~ me => Rql2Program(ms, me)
  }

  final override protected def exp8: PackratParser[Exp] = exp8Attr

  final private lazy val exp8Attr: PackratParser[Exp] = {
    exp8Attr <~ "(" <~ ")" ^^ { f => FunApp(f, Vector.empty) } |
      exp8Attr ~ ("(" ~> rep1sep(funAppArg, ",") <~ ")") ^^ { case f ~ args => FunApp(f, args) } |
      exp8Attr ~ ("." ~> identDef) ^^ { case e ~ idn => Proj(e, idn) } |
      // FunApp closing parenthesis is optional
      exp8Attr ~ ("(" ~> rep1sep(funAppArg, ",")) ^^ { case f ~ args => FunApp(f, args) } |
      // FunApp closing parenthesis is optional
      exp8Attr <~ "(" ^^ { f => FunApp(f, Vector.empty) } |
      /// Dot is optional
      exp8Attr <~ "." ^^ { e => Proj(e, "") } |
      baseExp
  }

  // Always succeed on types: last case is ErrorType() instead of failure

  final override lazy val baseType: Parser[Type] = baseTypeAttr |
    success(ErrorType())

  // Always succeed on expressions: last case is ErrorExp() instead of failure
  final override protected def baseExp: PackratParser[Exp] = baseExpAttr

  final private lazy val baseExpAttr: PackratParser[Exp] = {
    let |
      funAbs |
      typeExp |
      ifThenElse |
      nullConst |
      boolConst |
      // Because of the way we parse strings, we need to try triple quotes first
      tripleQStringConst |
      stringConst |
      numberConst |
      lists |
      records |
      idnExp |
      "(" ~> exp <~ opt(")") | // Closing parenthesis is optional
      success(ErrorExp())
  }

  final override protected def ifThenElse: Parser[IfThenElse] = ifThenElseAttr

  final private lazy val ifThenElseAttr: Parser[IfThenElse] = {
    // Partially written "if-then-else", i.e. "then" or "else" parts missing
    tokIf ~> exp ~ opt(tokThen ~> exp) ~ opt(
      tokElse ~> exp
    ) ^^ { case e1 ~ me2 ~ me3 => IfThenElse(e1, me2.getOrElse(ErrorExp()), me3.getOrElse(ErrorExp())) }
  }

  final override protected def let: Parser[Let] = letAttr

  final private lazy val letAttr: Parser[Let] = {
    // Partially written "let", i.e. "in" part missing
    (tokLet ~> repsep(letDecl, rep1(","))) ~ (opt(",") ~> opt(tokIn) ~> exp) ^^ { case d ~ e => Let(d, e) }
  }

  // Closing bracket is optional
  final override protected lazy val lists: Parser[Exp] = "[" ~> repsep(exp, ",") <~ opt("]") ^^ { atts =>
    ListPackageBuilder.Build(atts: _*)
  }

  // Closing brace is optional
  final override protected lazy val records: Parser[Exp] = "{" ~> repsep(attr, ",") <~ opt("}") ^^ { atts =>
    RecordPackageBuilder.Build(atts.zipWithIndex.map {
      case (attr, idx) => attr match {
          case ParsedNamedAttribute(idn, e) => idn -> e
          case ParsedUnnamedAttribute(e) => s"_${idx + 1}" -> e
        }
    })
  }
}
