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

package com.rawlabs.snapi.frontend.rql2.extensions.builtin

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.frontend.rql2.extensions.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtension,
  Param,
  TypeArg,
  TypeParam
}

object NullablePackageBuilder {

  object Empty {
    def apply(t: Type): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "Empty"), Vector(FunAppArg(TypeExp(t), None)))
    }
  }

  object UnsafeGet {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "UnsafeGet"), Vector(FunAppArg(e, None)))
    }
  }

  object Build {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "Build"), Vector(FunAppArg(e, None)))
    }
  }

  object Transform {
    def apply(f: Exp, e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Nullable"), "Transform"), Vector(FunAppArg(f, None), FunAppArg(e, None)))
    }
  }
}

class NullablePackage extends PackageExtension {

  override def name: String = "Nullable"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for handling nulls."
  )

}

class NullableEmptyEntry extends EntryExtension {

  override def packageName: String = "Nullable"

  override def entryName: String = "Empty"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def hasVarArgs: Boolean = false

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(TypeParam(DoesNotHaveTypeProperties(Set(SnapiIsNullableTypeProperty()))))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val TypeArg(t) = mandatoryArgs(0)
    Right(addProp(t, SnapiIsNullableTypeProperty()))
  }

}

class NullableBuildEntry extends EntryExtension {

  override def packageName: String = "Nullable"

  override def entryName: String = "Build"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(AnythingType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val t = addProp(mandatoryArgs(0).t, SnapiIsNullableTypeProperty())
    Right(t)
  }

}

class NullableIsNullEntry extends EntryExtension {

  override def packageName: String = "Nullable"

  override def entryName: String = "IsNull"

  override def docs: EntryDoc = EntryDoc(
    "Checks whether a value is null.",
    examples = List(
      ExampleDoc(
        """let x: int = null
          |in Nullable.IsNull(x)""".stripMargin,
        result = Some("true")
      ),
      ExampleDoc(
        """let y: int = 1
          |in Nullable.IsNull(y)""".stripMargin,
        result = Some("false")
      )
    ),
    params = List(ParamDoc("value", TypeDoc(List("anything")), "Value to check whether is null.")),
    ret = Some(ReturnDoc("Whether the value is null.", retType = Some(TypeDoc(List("bool")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(HasTypeProperties(Set(SnapiIsNullableTypeProperty()))))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiBoolType())
  }

}

class NullableUnsafeGetEntry extends EntryExtension {

  override def packageName: String = "Nullable"

  override def entryName: String = "UnsafeGet"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(HasTypeProperties(Set(SnapiIsNullableTypeProperty()))))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, t) = mandatoryArgs(0)
    Right(removeProp(t, SnapiIsNullableTypeProperty()))
  }

}

class NullableTransformEntry extends EntryExtension {

  override def packageName: String = "Nullable"

  override def entryName: String = "Transform"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(HasTypeProperties(Set(SnapiIsNullableTypeProperty()))))
    case 1 =>
      val ExpArg(_, t) = prevMandatoryArgs(0)
      val innerType = removeProp(t, SnapiIsNullableTypeProperty())
      Right(
        ExpParam(FunType(Vector(innerType), Vector.empty, DoesNotHaveTypeProperties(Set(SnapiIsNullableTypeProperty()))))
      )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val FunType(_, _, outType, _) = mandatoryArgs(1).t
    val t = addProp(outType, SnapiIsNullableTypeProperty())
    Right(t)
  }

}
