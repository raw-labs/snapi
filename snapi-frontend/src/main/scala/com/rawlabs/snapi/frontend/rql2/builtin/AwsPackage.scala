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

package com.rawlabs.snapi.frontend.rql2.builtin

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.frontend.rql2.extensions.{Arg, EntryExtension, ExpParam, PackageExtension, Param}
import com.rawlabs.snapi.frontend.rql2.source._

class AwsPackage extends PackageExtension {
  override def name: String = "Aws"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for Amazon AWS."
  )

}

class AwsV4SignedRequest extends EntryExtension {

  override def packageName: String = "Aws"

  override def entryName: String = "SignedV4Request"

  override def docs: EntryDoc = EntryDoc(
    summary = "Generates an AWS V4 signed request.",
    params = List(
      ParamDoc("key", TypeDoc(List("string")), "AWS key."),
      ParamDoc("secretKey", TypeDoc(List("string")), "AWS secret key."),
      ParamDoc("service", TypeDoc(List("string")), "AWS service name e.g.: 'ec2'."),
      ParamDoc("region", TypeDoc(List("string")), "AWS region e.g.: 'us-east-1'.", isOptional = true),
      ParamDoc("sessionToken", TypeDoc(List("string")), "AWS session token.", isOptional = true),
      ParamDoc(
        "path",
        TypeDoc(List("string")),
        """Path of the request name e.g.: '/path/to/service'. If not defined defaults to '/'.""".stripMargin,
        isOptional = true
      ),
      ParamDoc("method", TypeDoc(List("string")), "HTTP method, e.g.: 'GET'.", isOptional = true),
      ParamDoc(
        "host",
        TypeDoc(List("string")),
        """AWS host e.g.: 'ec2.amazonaws.com'.
          |If not defined defaults to: `{service}`.`{region}`.amazonaws.com.
          |If region is also not defined defaults to `{service}`.amazonaws.com.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "bodyString",
        TypeDoc(List("string")),
        "data to send in the body of the HTTP request.",
        isOptional = true
      ),
      ParamDoc("args", TypeDoc(List("list")), "url parameters.", isOptional = true),
      ParamDoc("headers", TypeDoc(List("list")), "HTTP headers.", isOptional = true)
    ),
    examples = List(
      ExampleDoc(
        """Xml.Read(
          |   Aws.SignedV4Request(
          |       "my-aws-key",
          |       Environment.Secret("aws-secret-key"),
          |       "ec2",
          |       host = "ec2.amazonaws.com",
          |       region = "eu-west-1",
          |       method = "GET",
          |       bodyString = "",
          |       args = [
          |           {"Action", "DescribeRegion"},
          |           {"Version", "2013-10-15"}
          |       ],
          |       headers = [{"content-type", "application/x-amz-json-1.1"}]
          |   ),
          |   type record(requestId: string, regionInfo: record(item: list(regionName: string, regionEndpoint: string)))
          |)""".stripMargin
      )
    )
  )

  override def nrMandatoryParams: Int = 3

  override def optionalParams: Option[Set[String]] =
    Some(Set("region", "sessionToken", "method", "host", "bodyString", "args", "headers", "path"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "region" | "sessionToken" | "path" | "method" | "host" | "bodyString" => Right(ExpParam(Rql2StringType()))
      case "args" | "headers" => Right(
          ExpParam(
            Rql2ListType(
              Rql2RecordType(Vector(Rql2AttrType("_1", Rql2StringType()), Rql2AttrType("_2", Rql2StringType())))
            )
          )
        )
    }
  }

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2StringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(Rql2LocationType())

}
