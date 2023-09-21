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
import raw.compiler.rql2.builtin.AwsV4SignedRequest
import raw.compiler.rql2.source.{Rql2ListType, Rql2RecordType}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.binary.PlusNode
import raw.runtime.truffle.ast.expressions.builtin.aws_package.AwsV4SignedRequestNodeGen
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode
import raw.runtime.truffle.ast.expressions.literals.StringNode

class AwsV4SignedRequestEntry extends AwsV4SignedRequest with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {

    val key = args.head.e
    val secretKey = args(1).e
    val service = args(2).e

    val maybeRegion = args.collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "region" => e }
    val maybeSessionToken = args.collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "sessionToken" => e }
    val method =
      args.collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "method" => e }.getOrElse(new StringNode("GET"))

    val host = args
      .collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "host" => e }
      .getOrElse(
        if (maybeRegion.isDefined) new PlusNode(
          new PlusNode(new PlusNode(service, new StringNode(".")), maybeRegion.get),
          new StringNode(".amazonaws.com")
        )
        else new PlusNode(service, new StringNode(".amazonaws.com"))
      )

    val path =
      args.collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "path" => e }.getOrElse(new StringNode("/"))

    val body =
      args.collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "bodyString" => e }.getOrElse(new StringNode(""))

    val urlParams = args
      .collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "args" => e }
      .getOrElse(new ListBuildNode(Rql2ListType(Rql2RecordType(Vector.empty)), Array()))

    val headers = args
      .collectFirst { case TruffleArg(e, _, Some(idn)) if idn == "headers" => e }
      .getOrElse(new ListBuildNode(Rql2ListType(Rql2RecordType(Vector())), Array()))

    val sessionToken = maybeSessionToken
      .getOrElse(new StringNode(""))

    val region = maybeRegion.getOrElse(new StringNode("us-east-1"))

    AwsV4SignedRequestNodeGen.create(
      key,
      secretKey,
      service,
      region,
      sessionToken,
      path,
      method,
      host,
      body,
      urlParams,
      headers
    )
  }

}
