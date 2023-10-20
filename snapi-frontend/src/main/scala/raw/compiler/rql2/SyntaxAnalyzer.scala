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

package raw.compiler.rql2

import org.bitbucket.inkytonik.kiama.util.Positions
import raw.compiler.base.source.Type
import raw.compiler.common.source.Exp
import raw.compiler.rql2.FrontendSyntaxAnalyzerTokens.tokOr
import raw.compiler.rql2.source._

/**
 * Internal SyntaxAnalyzer.
 *
 * Unlike FrontendSyntaxAnalyzer, it can be slow and must contain internal nodes & types.
 * This parser is optional and only used if "training wheels" is on, and we pretty print/reparse trees.
 */
class SyntaxAnalyzer(positions: Positions) extends FrontendSyntaxAnalyzer(positions) {

  ///////////////////////////////////////////////////////////////////////////
  // Types
  ///////////////////////////////////////////////////////////////////////////

  override protected lazy val typeProps: Parser[Set[Rql2TypeProperty]] = {
    ("@try" ~ "@null" ^^^ Set[Rql2TypeProperty](Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())) |
      ("@null" ~ "@try" ^^^ Set[Rql2TypeProperty](Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())) |
      ("@try" ^^^ Set[Rql2TypeProperty](Rql2IsTryableTypeProperty())) |
      ("@null" ^^^ Set[Rql2TypeProperty](Rql2IsNullableTypeProperty())) |
      success(Set.empty[Rql2TypeProperty])
  }

  ///////////////////////////////////////////////////////////////////////////
  // Expressions
  ///////////////////////////////////////////////////////////////////////////

  final override protected lazy val baseExp: PackratParser[Exp] = {
    packageIdnExp |
      eval |
      super.baseExp
  }

  final private lazy val packageIdnExp: Parser[PackageIdnExp] =
    "\\$package\\b".r ~> "(" ~> stringLit <~ ")" ^^ PackageIdnExp

  final override lazy val tipe1: PackratParser[Type] = tipe1 ~ ("->" ~> tipe2) ~ ("(" ~> typeProps <~ ")") ^^ {
    case t ~ r ~ props => FunType(Vector(t), Vector.empty, r, props)
  } | tipe1 ~ ("->" ~> tipe2) ^^ { case t ~ r => FunType(Vector(t), Vector.empty, r, Set.empty) } |
    tipe2

  final override lazy val tipe2: PackratParser[Type] = tipe2 ~ (tokOr ~> baseType) ~ ("(" ~> typeProps <~ ")") ^^ {
    case t1 ~ t2 ~ props => Rql2OrType(t1, t2, props)
  } | tipe2 ~ (tokOr ~> baseType) ^^ { case t1 ~ t2 => Rql2OrType(t1, t2, Set.empty) } |
    baseType

}
