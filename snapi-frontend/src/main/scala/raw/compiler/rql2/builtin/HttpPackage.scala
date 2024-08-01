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

package raw.compiler.rql2.builtin

import raw.client.api._
import raw.compiler.base.source.Type
import raw.compiler.common.source._
import raw.compiler.rql2._
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param, ShortEntryExtension}
import raw.compiler.rql2.source._

import scala.collection.immutable.ListMap

class
HttpPackage extends PackageExtension {

  override def name: String = "Http"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of HTTP functions."
  )

}

class HttpReadEntry
    extends ShortEntryExtension(
      "Http",
      "Read",
      Vector(Rql2LocationType()),
      Rql2RecordType(
        Vector(
          Rql2AttrType("status", Rql2IntType()),
          Rql2AttrType("data", Rql2BinaryType()),
          Rql2AttrType(
            "headers",
            Rql2ListType(
              Rql2RecordType(
                Vector(
                  Rql2AttrType("_1", Rql2StringType()),
                  Rql2AttrType("_2", Rql2StringType())
                )
              )
            )
          )
        ),
        Set(Rql2IsTryableTypeProperty())
      ),
      docs = EntryDoc(
        summary = "Makes an HTTP call",
        params = List(
          ParamDoc("location", TypeDoc(List("location")), "The HTTP location."),
          ParamDoc(
            "expectedStatus",
            TypeDoc(List("list")),
            "The list of expected HTTP status codes. If the response status code received is not on the list, the call fails with an error."
          )
        ),
        examples = List(ExampleDoc("""let
          |  request = Http.Read(
          |     Http.Post(
          |      "http://localhost:1234/return-body",
          |      bodyString = "Hello World",
          |      username = "user",
          |      password = "passwd"
          |    )
          |  )
          |in
          |  String.Decode(request.data, "utf-8")"""".stripMargin)),
        ret = Some(
          ReturnDoc(
            "The HTTP response.",
            retType =
              Some(TypeDoc(List("record(status: int, data: binary, headers: list(record(_1: string, _2: string))))")))
          )
        )
      ),
      optionalParamsMap = ListMap(
        "expectedStatus" -> (Rql2ListType(Rql2IntType(), Set(Rql2IsNullableTypeProperty())),
        NullablePackageBuilder.Empty(Rql2ListType(Rql2IntType())))
      )
    )

abstract class HttpCallEntry(method: String) extends EntryExtension {

  override def packageName: String = "Http"

  override val entryName: String = {
    method(0).toUpper + method.substring(1)
  }

  override def docs: EntryDoc = EntryDoc(
    summary = s"Creates an HTTP ${method.toUpperCase} location.",
    examples = List(
      ExampleDoc(
        s"""Http.${method.capitalize}(
          |  "https://www.somewhere.com/something",
          |  headers = [{"Content-Type", "application/json"}],
          |  args = [{"download", "true"}],
          |  bodyString = Json.Print({name: "john", age: 35})
          |)""".stripMargin
      )
    ),
    params = List(
      ParamDoc("url", TypeDoc(List("string")), "The HTTP URL."),
      ParamDoc(
        "bodyString",
        TypeDoc(List("string")),
        "The string data to send as the body of the request. Cannot be used with `bodyBinary`.",
        isOptional = true
      ),
      ParamDoc(
        "bodyBinary",
        TypeDoc(List("binary")),
        "The data to send as the body of the request. Cannot be used with `bodyString`.",
        isOptional = true
      ),
      ParamDoc(
        "token",
        TypeDoc(List("string")),
        "The bearer token to be passed as the Authorization header of the request.",
        isOptional = true
      ),
      ParamDoc(
        "authCredentialName",
        TypeDoc(List("string")),
        "The name of the HTTP credential registered in the credentials storage.",
        isOptional = true
      ),
      ParamDoc(
        "clientId",
        TypeDoc(List("string")),
        "The client ID to use for the client credentials OAuth flow. Requires `clientSecret` and `tokenUrl`.",
        isOptional = true
      ),
      ParamDoc(
        "clientSecret",
        TypeDoc(List("string")),
        "The client secret to use for the client credentials OAuth flow. Requires `clientId` and either `authProvider` or `tokenUrl`.",
        isOptional = true
      ),
      ParamDoc(
        "authProvider",
        TypeDoc(List("string")),
        "The provider for client ID client secret OAuth flow. Requires `clientId` and `clientSecret`.",
        isOptional = true
      ),
      ParamDoc(
        "tokenUrl",
        TypeDoc(List("string")),
        "The URL to be used for the client credentials OAuth flow. Requires `clientId` and `clientSecret`.",
        isOptional = true
      ),
      ParamDoc(
        "useBasicAuth",
        TypeDoc(List("bool")),
        "If true, uses basic auth for the client credentials OAuth flow. Requires `clientId`, `clientSecret` and `tokenUrl`.",
        isOptional = true
      ),
      ParamDoc(
        "username",
        TypeDoc(List("string")),
        "The username to be used for basic authentication. Requires `password`.",
        isOptional = true
      ),
      ParamDoc(
        "password",
        TypeDoc(List("string")),
        "The password to be used for basic authentication. Requires `username`.",
        isOptional = true
      ),
      ParamDoc(
        "args",
        TypeDoc(List("list")),
        """The query parameters arguments for the HTTP request, e.g. `[{"name", "john"}, {"age", "22"}]`. They are URL-encoded automatically.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "headers",
        TypeDoc(List("list")),
        """The HTTP headers to include in the request, e.g. `[{"Authorization", "Bearer 1234"}, {"Accept", "application/json"}]`.""".stripMargin,
        isOptional = true
      ),
      ParamDoc("expectedStatus", TypeDoc(List("list")), "The list of expected statuses.", isOptional = true)
    ),
    ret = Some(ReturnDoc("A location to read from.", retType = Some(TypeDoc(List("location"))))),
    info = Some(
      """Any key/value pair with a null key or value in the `headers` or in the `args` parameters will be omitted and won't be included in the request."""
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(Rql2StringType()))
  }

  // FIXME (msb): This includes settings that are no longer supported!
  override def optionalParams: Option[Set[String]] = Some(
    Set(
      "bodyString",
      "bodyBinary",
      "token",
      "authCredentialName",
      "clientId",
      "clientSecret",
      "authProvider",
      "tokenUrl",
      "useBasicAuth",
      "username",
      "password",
      "args",
      "headers",
      "expectedStatus"
    )
  )
  // FIXME (msb): This isn't actually validating anything! We must do:
  //  idn match {
  //    case "bodyString" => Right(Rql2StringType())
  //    ...
  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(
      ExpParam(
        OneOfType(
          Rql2IntType(),
          Rql2StringType(),
          Rql2BinaryType(),
          Rql2BoolType(),
          Rql2IntervalType(),
          Rql2ListType(
            Rql2RecordType(
              Vector(
                Rql2AttrType("_1", Rql2StringType(Set(Rql2IsNullableTypeProperty()))),
                Rql2AttrType("_2", Rql2StringType(Set(Rql2IsNullableTypeProperty())))
              )
            )
          ),
          Rql2ListType(Rql2IntType())
        )
      )
    )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2LocationType())
  }

}

class HttpUrlEncodeEntry
    extends ShortEntryExtension(
      "Http",
      "UrlEncode",
      Vector(Rql2StringType()),
      Rql2StringType(),
      EntryDoc(
        "Encodes a string as a URL.",
        params = List(ParamDoc("value", TypeDoc(List("string")), "The string to encode.")),
        ret = Some(ReturnDoc("The encoded string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class HttpUrlDecodeEntry
    extends ShortEntryExtension(
      "Http",
      "UrlDecode",
      Vector(Rql2StringType()),
      Rql2StringType(),
      EntryDoc(
        "Decodes a URL-encoded string.",
        params = List(ParamDoc("value", TypeDoc(List("string")), "The string to decode.")),
        ret = Some(ReturnDoc("The decoded string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class HttpPutEntry extends HttpCallEntry("put")
class HttpDeleteEntry extends HttpCallEntry("delete")
class HttpGetEntry extends HttpCallEntry("get")
class HttpHeadEntry extends HttpCallEntry("head")
class HttpOptionsEntry extends HttpCallEntry("options")
class HttpPatchEntry extends HttpCallEntry("patch")
class HttpPostEntry extends HttpCallEntry("post")
