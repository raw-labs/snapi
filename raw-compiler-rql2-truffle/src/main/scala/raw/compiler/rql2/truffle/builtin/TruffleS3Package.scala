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
import raw.compiler.rql2.builtin.S3BuildEntry
import raw.compiler.rql2.source.Rql2TypeWithProperties
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode

class TruffleS3BuildEntry extends S3BuildEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {

    def replaceKey(idn: String) = idn match {
      case "region" => "s3-region"
      case "accessKey" => "s3-access-key"
      case "secretKey" => "s3-secret-key"
    }

    val url = args.head.e
    val keys = args.tail.collect { case TruffleArg(_, _, Some(idn)) => replaceKey(idn) }
    val values = args.tail.collect { case TruffleArg(e, _, _) => e }
    val types = args.tail.collect { case TruffleArg(_, t, Some(_)) => t.asInstanceOf[Rql2TypeWithProperties] }

    new LocationBuildNode(
      url,
      keys.toArray,
      values.toArray,
      types.toArray
    )
  }

}
