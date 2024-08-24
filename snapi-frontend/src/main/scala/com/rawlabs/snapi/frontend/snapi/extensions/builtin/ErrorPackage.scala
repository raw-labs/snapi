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

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{
  Arg,
  EntryExtension,
  ExpParam,
  PackageExtension,
  Param,
  TypeArg,
  TypeParam
}
import com.rawlabs.snapi.frontend.snapi.source._

object ErrorPackageBuilder {

  object BuildWithType {
    def apply(t: Type, e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Error"), "BuildWithType"), Vector(FunAppArg(TypeExp(t), None), FunAppArg(e, None)))
    }
  }

  object Get {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Error"), "Get"), Vector(FunAppArg(e, None)))
    }
  }

}

class ErrorPackage extends PackageExtension {

  override def name: String = "Error"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the failure cases of error handling."
  )

}

class ErrorBuildEntry extends EntryExtension {

  override def packageName: String = "Error"

  override def entryName: String = "Build"
  override def docs: EntryDoc = EntryDoc(
    "Builds an error value.",
    examples = List(ExampleDoc("""Error.Build("This is an error")""", result = Some("This is an error"))),
    params = List(ParamDoc("message", TypeDoc(List("string")), "The error message."))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(SnapiStringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiUndefinedType(Set(SnapiIsTryableTypeProperty())))
  }

}

class ErrorBuildWithTypeEntry extends EntryExtension {

  override def packageName: String = "Error"

  override def entryName: String = "BuildWithType"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(TypeParam(DoesNotHaveTypeProperties(Set(SnapiIsTryableTypeProperty()))))
    case 1 => Right(ExpParam(SnapiStringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val TypeArg(t) = mandatoryArgs(0)
    Right(addProp(t, SnapiIsTryableTypeProperty()))
  }

}

class ErrorGetEntry extends EntryExtension {

  override def packageName: String = "Error"

  override def entryName: String = "Get"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(IsTryable()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiStringType())
  }

}
