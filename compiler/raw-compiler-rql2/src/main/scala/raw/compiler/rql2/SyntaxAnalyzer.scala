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

  final override protected lazy val tipe: Parser[Type] = commonType | super.tipe

  ///////////////////////////////////////////////////////////////////////////
  // Expressions
  ///////////////////////////////////////////////////////////////////////////

  final override protected lazy val baseExp: PackratParser[Exp] = {
    packageIdnExp |
      eval |
      super.baseExp
  }

  final private lazy val packageIdnExp: Parser[PackageIdnExp] =
    "$$package\\b".r ~> "(" ~> stringLit <~ ")" ^^ PackageIdnExp

}
