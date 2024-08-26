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

object BytePackageBuilder {

  object From {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Byte"), "Build"), Vector(FunAppArg(e, None)))
    }
  }
}

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
    val props: Set[SnapiTypeProperty] = mandatoryArgs(0).t match {
      case _: SnapiStringType => Set(SnapiIsTryableTypeProperty())
      case _ => Set.empty[SnapiTypeProperty]
    }
    Right(SnapiByteType(props))
  }

}
