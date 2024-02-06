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
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param}
import raw.compiler.rql2.source._

class LocationPackage extends PackageExtension {

  override def name: String = "Location"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions to obtain information on remote locations."
  )

}

class LocationBuildEntry extends EntryExtension {

  override def packageName: String = "Location"

  override def entryName: String = "Build"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set.empty)

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
    Right(ExpParam(Rql2LocationType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(
    Rql2RecordType(
      Vector(
        Rql2AttrType("format", Rql2StringType()),
        Rql2AttrType("comment", Rql2StringType()),
        Rql2AttrType("type", Rql2StringType()),
        Rql2AttrType(
          "properties",
          Rql2ListType(
            Rql2RecordType(
              Vector(
                Rql2AttrType("name", Rql2StringType()),
                Rql2AttrType("value", Rql2StringType(Set(Rql2IsNullableTypeProperty())))
              )
            )
          )
        ),
        Rql2AttrType("is_collection", Rql2BoolType()),
        Rql2AttrType(
          "columns",
          Rql2ListType(
            Rql2RecordType(
              Vector(
                Rql2AttrType("col_name", Rql2StringType(Set(Rql2IsNullableTypeProperty()))),
                Rql2AttrType("col_type", Rql2StringType()),
                Rql2AttrType("nullable", Rql2BoolType())
              )
            )
          )
        ),
        Rql2AttrType("sampled", Rql2BoolType())
      ),
      Set(Rql2IsTryableTypeProperty())
    )
  )

  override def optionalParams: Option[Set[String]] = Some(Set("sampleSize"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(ExpParam(Rql2IntType(Set(Rql2IsNullableTypeProperty()))))
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
    Right(ExpParam(Rql2LocationType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] =
    Right(Rql2ListType(Rql2StringType(), Set(Rql2IsTryableTypeProperty())))

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
    Right(ExpParam(Rql2LocationType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(
    Rql2ListType(
      Rql2RecordType(
        Vector(
          Rql2AttrType("url", Rql2StringType()),
          Rql2AttrType(
            "metadata",
            Rql2RecordType(
              Vector(
                Rql2AttrType("modified", Rql2TimestampType(Set(Rql2IsNullableTypeProperty()))),
                Rql2AttrType("size", Rql2LongType(Set(Rql2IsNullableTypeProperty()))),
                Rql2AttrType(
                  "blocks",
                  Rql2ListType(
                    Rql2RecordType(
                      Vector(
                        Rql2AttrType("hosts", Rql2ListType(Rql2StringType())),
                        Rql2AttrType("offset", Rql2LongType()),
                        Rql2AttrType("length", Rql2LongType())
                      )
                    )
                  )
                )
              )
            )
          )
        )
      ),
      Set(Rql2IsTryableTypeProperty())
    )
  )

}
