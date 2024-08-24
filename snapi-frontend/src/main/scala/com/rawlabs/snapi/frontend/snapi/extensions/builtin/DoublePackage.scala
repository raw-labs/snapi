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

object DoublePackageBuilder {

  object From {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Double"), "From"), Vector(FunAppArg(e, None)))
    }
  }
}

class DoublePackage extends PackageExtension {

  override def name: String = "Double"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the double type."
  )

}

class DoubleFromEntry extends EntryExtension {

  override def packageName: String = "Double"

  override def entryName: String = "From"

  override def docs: EntryDoc = EntryDoc(
    "Builds a double from a number or string.",
    examples = List(
      ExampleDoc("""Double.Build(1) // 1.0""", result = Some("1.0")),
      ExampleDoc("""Double.From("1") // 1.0""", result = Some("1.0")),
      ExampleDoc("""Double.From(1.5) // 1.5""", result = Some("1.5"))
    ),
    params = List(
      ParamDoc("value", TypeDoc(List("string", "number")), "The value to convert to double.")
    ),
    ret = Some(
      ReturnDoc(
        "The double representation of the value.",
        Some(TypeDoc(List("double")))
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
    val props: Set[SnapiTypeProperty] = mandatoryArgs(0).t match {
      case _: SnapiStringType => Set(SnapiIsTryableTypeProperty())
      case _ => Set.empty[SnapiTypeProperty]
    }
    Right(SnapiDoubleType(props))
  }

}
