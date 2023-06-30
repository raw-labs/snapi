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
import raw.compiler.rql2.builtin.{HttpCallEntry, HttpReadEntry, HttpUrlDecode, HttpUrlEncode}
import raw.compiler.rql2.source.{Rql2IntType, Rql2PrimitiveType, Rql2StringType, Rql2TypeWithProperties}
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension, TruffleShortEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.http_package.{
  HttpReadNodeGen,
  HttpUrlDecodeNodeGen,
  HttpUrlEncodeNodeGen
}
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode
import raw.runtime.truffle.ast.expressions.literals.StringNode
import raw.sources.CacheStrategy

class TruffleHttpReadEntry extends HttpReadEntry with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = HttpReadNodeGen.create(args.head, args(1))
}

abstract class TruffleHttpCallEntry(method: String) extends HttpCallEntry(method) with TruffleEntryExtension {
  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {

    def replaceKey(idn: String) = {
      idn match {
        case "method" => "http-method"
        case "bodyString" => "http-body-string"
        case "bodyBinary" => "http-body"
        case "token" => "http-token"
        case "authCredentialName" => "http-auth-cred-name"
        case "clientId" => "http-client-id"
        case "clientSecret" => "http-client-secret"
        case "authProvider" => "http-auth-provider"
        case "tokenUrl" => "http-token-url"
        case "useBasicAuth" => "http-use-basic-auth"
        case "username" => "http-user-name"
        case "password" => "http-password"
        case "args" => "http-args"
        case "headers" => "http-headers"
        case "expectedStatus" => "http-expected-status"
      }
    }

    val url = args.head.e
    val keys = args.tail.collect { case TruffleArg(_, _, Some(idn)) => replaceKey(idn) } :+ "http-method"
    val values = args.tail.collect { case TruffleArg(e, _, _) => e } :+ new StringNode(method)
    val types = args.tail.collect {
      case TruffleArg(_, t, Some(_)) => t.asInstanceOf[Rql2TypeWithProperties]
    } :+ Rql2StringType().asInstanceOf[Rql2TypeWithProperties]

    new LocationBuildNode(
      url,
      keys.toArray,
      values.toArray,
      types.toArray,
      CacheStrategy.NoCache
    )
  }

}

class TruffleHttpGetEntry extends TruffleHttpCallEntry("get")

class TruffleHttpPostEntry extends TruffleHttpCallEntry("post")

class TruffleHttpPutEntry extends TruffleHttpCallEntry("put")

class TruffleHttpDeleteEntry extends TruffleHttpCallEntry("delete")

class TruffleHttpHeadEntry extends TruffleHttpCallEntry("head")

class TruffleHttpPatchEntry extends TruffleHttpCallEntry("patch")

class TruffleHttpOptionsEntry extends TruffleHttpCallEntry("options")

class TruffleHttpUrlEncode extends HttpUrlEncode with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = HttpUrlEncodeNodeGen.create(args.head)
}

class TruffleHttpUrlDecode extends HttpUrlDecode with TruffleShortEntryExtension {
  override def toTruffle(args: Seq[ExpressionNode]): ExpressionNode = HttpUrlDecodeNodeGen.create(args.head)
}
