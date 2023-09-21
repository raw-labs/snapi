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

import raw.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.compiler.base.source.Type
import raw.compiler.rql2._
import raw.compiler.rql2.source._

class BytePackage extends PackageExtension {

  /**
   * Name of the package.
   */
  override def name: String = "Byte"

  /**
   * Package documentation.
   */
  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the byte type."
  )

}

class ByteFromEntry extends EntryExtension {

  override def packageName: String = "Byte"

  override def entryName: String = "From"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Builds a byte from a number or string.",
    examples = List(
      ExampleDoc("""Byte.From("123")""", result = Some("123")),
      ExampleDoc("""Byte.From(1.5)""", result = Some("1"))
    ),
    params = List(
      ParamDoc("value", TypeDoc(List("number", "string")), "The value to convert to byte.")
    ),
    ret = Some(
      ReturnDoc(
        "The byte representation of the value.",
        Some(TypeDoc(List("byte")))
      )
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(numberOrString))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    // Type as Try when passed a string in case the content doesn't parse.
    val props: Set[Rql2TypeProperty] = mandatoryArgs(0).t match {
      case _: Rql2StringType => Set(Rql2IsTryableTypeProperty())
      case _ => Set.empty[Rql2TypeProperty]
    }
    Right(Rql2ByteType(props))
  }

}
