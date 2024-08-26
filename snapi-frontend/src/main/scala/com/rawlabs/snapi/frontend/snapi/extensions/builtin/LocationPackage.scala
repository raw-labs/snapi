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

package com.rawlabs.snapi.frontend.snapi.extensions.builtin

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{Arg, EntryExtension, ExpParam, PackageExtension, Param}
import com.rawlabs.snapi.frontend.snapi.source._

object LocationPackageBuilder {

  object FromString {
    def apply(url: Exp): Exp = {
      FunApp(
        Proj(PackageIdnExp("Location"), "FromString"),
        Vector(FunAppArg(url, None))
      )
    }
  }

}

class LocationPackage extends PackageExtension {

  override def name: String = "Location"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions to obtain information on remote locations."
  )

}

class LocationFromStringEntry extends EntryExtension {

  override def packageName: String = "Location"

  override def entryName: String = "FromString"

  // FIXME (msb): Make this a user-visible node and take advantage to document the exact format of the URLs allowed.
  override def docs: EntryDoc = EntryDoc(
    summary = """Transforms an url to a location.""".stripMargin,
    description = Some("""List of supported url protocols:
      |  - s3 e.g. 's3://my-bucket/folder/file'.
      |  - http/https e.g 'https://example.com/service'.
      |  - dropbox e.g. 'dropbox://credential/my-folder/file'.""".stripMargin),
    params = List(
      ParamDoc("url", TypeDoc(List("string")), "The url to resolve to a location.")
    ),
    examples = List(
      ExampleDoc("""
        |Location.FromString("s3://my-bucket/folder/file")
        |""".stripMargin)
    ),
    ret = Some(
      ReturnDoc(
        "The location object.",
        retType = Some(TypeDoc(List("location")))
      )
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(SnapiStringType()))
  }

  override def optionalParams: Option[Set[String]] = None

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiLocationType())
  }

}

class LocationDescribeEntry extends EntryExtension {

  override def packageName: String = "Location"

  override def entryName: String = "Describe"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    summary = "Describes a location.",
    params = List(
      ParamDoc("location", TypeDoc(List("location")), "The location to describe."),
      ParamDoc(
        "sampleSize",
        TypeDoc(List("int")),
        "The number of data units to sample from the location for the inference. If set to negative value, the whole file will be read.",
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc("""
        |Location.Describe("http://.../dataset.json")
        |""".stripMargin),
      ExampleDoc("""
        |Location.Describe("http://.../dataset.json", sampleSize = 1000)
        |""".stripMargin)
    ),
    ret = Some(
      ReturnDoc(
        "A record describing the location.",
        retType = Some(
          TypeDoc(
            List(
              "record(format: string, comment: string, type: string, properties: list(record(name: string, value: string)), is_collection: bool, columns: list(record(col_name: string, col_type: string, nullable: bool)), sampled: bool)"
            )
          )
        )
      )
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(SnapiLocationType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(
    SnapiRecordType(
      Vector(
        SnapiAttrType("format", SnapiStringType()),
        SnapiAttrType("comment", SnapiStringType()),
        SnapiAttrType("type", SnapiStringType()),
        SnapiAttrType(
          "properties",
          SnapiListType(
            SnapiRecordType(
              Vector(
                SnapiAttrType("name", SnapiStringType()),
                SnapiAttrType("value", SnapiStringType(Set(SnapiIsNullableTypeProperty())))
              )
            )
          )
        ),
        SnapiAttrType("is_collection", SnapiBoolType()),
        SnapiAttrType(
          "columns",
          SnapiListType(
            SnapiRecordType(
              Vector(
                SnapiAttrType("col_name", SnapiStringType(Set(SnapiIsNullableTypeProperty()))),
                SnapiAttrType("col_type", SnapiStringType()),
                SnapiAttrType("nullable", SnapiBoolType())
              )
            )
          )
        ),
        SnapiAttrType("sampled", SnapiBoolType())
      ),
      Set(SnapiIsTryableTypeProperty())
    )
  )

  override def optionalParams: Option[Set[String]] = Some(Set("sampleSize"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(ExpParam(SnapiIntType(Set(SnapiIsNullableTypeProperty()))))
  }

}

class LocationLsEntry extends EntryExtension {

  override def packageName: String = "Location"

  override def entryName: String = "Ls"

  override def docs = EntryDoc(
    summary = "Lists a location. The location must be a file system or an S3 bucket.",
    params = List(ParamDoc("value", TypeDoc(List("location")), "The location to list.")),
    examples = List(ExampleDoc("""Location.Ls("s3://my-bucket/folder/")""", None)),
    ret = Some(ReturnDoc("The list of files in the location.", retType = Some(TypeDoc(List("list(string)"))))),
    description = Some(
      "Urls with wildcards are also supported. For information about the use of wildcards see the [Locations with wildcards documentation](/snapi/wildcards)."
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(SnapiLocationType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] =
    Right(SnapiListType(SnapiStringType(), Set(SnapiIsTryableTypeProperty())))

}

class LocationLlEntry() extends EntryExtension {

  override def packageName: String = "Location"

  override def entryName: String = "Ll"

  override def docs = EntryDoc(
    summary =
      "Lists a location but returns also metadata information per entry. The location must be a file system or an S3 bucket.",
    params = List(ParamDoc("location", TypeDoc(List("location")), "The location to list.")),
    examples = List(ExampleDoc("""Location.Ll("s3://my-bucket/folder/")""")),
    ret = Some(
      ReturnDoc(
        "The list of files in the location with metadata information.",
        retType = Some(
          TypeDoc(
            List(
              "list(record(url: string, metadata: record(modified: timestamp, size: int, blocks: list(record(hosts: list(string), offset: long, length: long)))"
            )
          )
        )
      )
    ),
    description = Some(
      "URLs with wildcards are also supported. For information about the use of wildcards see the [Locations with wildcards documentation](/snapi/wildcards)."
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(SnapiLocationType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(
    SnapiListType(
      SnapiRecordType(
        Vector(
          SnapiAttrType("url", SnapiStringType()),
          SnapiAttrType(
            "metadata",
            SnapiRecordType(
              Vector(
                SnapiAttrType("modified", SnapiTimestampType(Set(SnapiIsNullableTypeProperty()))),
                SnapiAttrType("size", SnapiLongType(Set(SnapiIsNullableTypeProperty()))),
                SnapiAttrType(
                  "blocks",
                  SnapiListType(
                    SnapiRecordType(
                      Vector(
                        SnapiAttrType("hosts", SnapiListType(SnapiStringType())),
                        SnapiAttrType("offset", SnapiLongType()),
                        SnapiAttrType("length", SnapiLongType())
                      )
                    )
                  )
                )
              )
            )
          )
        )
      ),
      Set(SnapiIsTryableTypeProperty())
    )
  )

}
