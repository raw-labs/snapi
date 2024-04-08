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
import raw.compiler.rql2._
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param}
import raw.compiler.rql2.source._

class FloatPackage extends PackageExtension {

  override def name: String = "Float"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the float type."
  )

}

class FloatFromEntry extends EntryExtension {

  override def packageName: String = "Float"

  override def entryName: String = "From"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Builds a float from a number or string.",
    examples = List(
      ExampleDoc("""Float.From(1)""", result = Some("1.0f")),
      ExampleDoc("""Float.From("1")""", result = Some("1.0f")),
      ExampleDoc("""Float.From(1.5)""", result = Some("1.5f"))
    ),
    params = List(
      ParamDoc("value", TypeDoc(List("number", "string")), "The value to convert to float.")
    ),
    ret = Some(
      ReturnDoc(
        "The float representation of the value.",
        Some(TypeDoc(List("float")))
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
    Right(Rql2FloatType(props))
  }

}
