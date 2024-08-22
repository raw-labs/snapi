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

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.rql2.source.{Rql2LocationType, Rql2StringType}
import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.frontend.rql2.extensions.{Arg, EntryExtension, ExpParam, PackageExtension, Param}

class S3Package extends PackageExtension {

  override def name: String = "S3"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for accessing data on S3."
  )

}

class S3BuildEntry extends EntryExtension {

  override def packageName: String = "S3"

  override def entryName: String = "Build"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Builds a S3 location from an url.",
    params = List(
      ParamDoc("url", TypeDoc(List("string")), "The url to the s3 location."),
      ParamDoc("region", TypeDoc(List("string")), "The region of the bucket, e.g. 'eu-west-1'.", isOptional = true),
      ParamDoc("accessKey", TypeDoc(List("string")), "The AWS access key.", isOptional = true),
      ParamDoc("secretKey", TypeDoc(List("string")), "The AWS secret key.", isOptional = true)
    ),
    info = Some(
      "If the S3 bucket is not registered in the credentials storage, then the region, accessKey and secretKey must be provided as arguments."
    ),
    examples = List(ExampleDoc("""S3.Build("S3://my-bucket/folder/sub-folder/file")""")),
    ret = Some(ReturnDoc("The S3 location.", retType = Some(TypeDoc(List("location")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("region", "accessKey", "secretKey"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = idn match {
    case "region" => Right(ExpParam(Rql2StringType()))
    case "accessKey" => Right(ExpParam(Rql2StringType()))
    case "secretKey" => Right(ExpParam(Rql2StringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2LocationType())
  }

}
