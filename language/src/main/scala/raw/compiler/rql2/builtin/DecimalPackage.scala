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
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param, ShortEntryExtension}
import raw.compiler.rql2.source._

class DecimalPackage extends PackageExtension {

  override def name: String = "Decimal"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the decimal type."
  )

}

class DecimalFromEntry extends EntryExtension {

  override def packageName: String = "Decimal"

  override def entryName: String = "From"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Builds a decimal from a number or string.",
    examples = List(
      ExampleDoc("""Decimal.From(1)""", result = Some("1.0")),
      ExampleDoc("""Decimal.From("1")""", result = Some("1.0")),
      ExampleDoc("""Decimal.From(1.5)""", result = Some("1.5"))
    ),
    params = List(
      ParamDoc("value", TypeDoc(List("number", "string")), "The value to convert to decimal.")
    ),
    ret = Some(
      ReturnDoc(
        "The decimal representation of the value.",
        Some(TypeDoc(List("decimal")))
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
    Right(Rql2DecimalType(props))
  }

}

class DecimalRoundEntry
    extends ShortEntryExtension(
      "Decimal",
      "Round",
      Vector(Rql2DecimalType(), Rql2IntType()),
      Rql2DecimalType(),
      EntryDoc(
        summary = "Rounds a decimal to the specified number of decimal places.",
        examples = List(
          ExampleDoc("""Round(2.1234, 2)""", result = Some("2.12")),
          ExampleDoc("""Round(2.1234, 3)""", result = Some("2.123"))
        ),
        params = List(
          ParamDoc("value", TypeDoc(List("decimal")), "The value to be round."),
          ParamDoc("decimal cases", TypeDoc(List("int")), "Number of decimal places to round to.")
        ),
        ret = Some(
          ReturnDoc(
            "The rounded decimal.",
            Some(TypeDoc(List("decimal")))
          )
        )
      )
    )
