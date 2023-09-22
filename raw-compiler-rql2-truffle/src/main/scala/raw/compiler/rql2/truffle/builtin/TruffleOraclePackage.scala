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

package raw.compiler.rql2.truffle.builtin

import raw.compiler.base.source.Type
import raw.compiler.rql2.builtin.OracleQueryEntry
import raw.compiler.rql2.source.{Rql2StringType, Rql2TypeWithProperties}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.binary.PlusNode
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode
import raw.runtime.truffle.ast.expressions.literals.StringNode
import raw.runtime.truffle.runtime.exceptions.rdbms.OracleExceptionHandler

class TruffleOracleQueryEntry extends OracleQueryEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val db = args.head.e
    val optionalArgs = args.collect {
      case TruffleArg(e, _, Some(idn)) => idn match {
          case "host" => ("db-host", e)
          case "port" => ("db-port", e)
          case "username" => ("db-username", e)
          case "password" => ("db-password", e)
        }
    }
    val keys = optionalArgs.map(_._1)
    val values = optionalArgs.map(_._2)
    val types = args.tail.collect { case TruffleArg(_, t, Some(_)) => t.asInstanceOf[Rql2TypeWithProperties] }
    val location = new LocationBuildNode(
      new PlusNode(new StringNode("oracle:"), db),
      keys.toArray,
      values.toArray,
      types.toArray
    )
    TruffleJdbc.query(location, args(1).e, t, new OracleExceptionHandler())
  }

}
