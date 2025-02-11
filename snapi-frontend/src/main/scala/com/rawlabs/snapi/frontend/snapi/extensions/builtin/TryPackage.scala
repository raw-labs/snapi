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

import com.rawlabs.snapi.frontend.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{Arg, EntryExtension, ExpArg, ExpParam, PackageExtension, Param}

object TryPackageBuilder {

  object Transform {
    def apply(f: Exp, e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Try"), "Transform"), Vector(FunAppArg(f, None), FunAppArg(e, None)))
    }
  }
  object FlatMap {
    def apply(f: Exp, e: Exp): Exp = {
      FunApp(Proj(IdnExp("Try"), "FlatMap"), Vector(FunAppArg(f, None), FunAppArg(e, None)))
    }
  }

  object UnsafeGet {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Try"), "UnsafeGet"), Vector(FunAppArg(e, None)))
    }
  }

}

class TryPackage extends PackageExtension {

  override def name: String = "Try"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of error handling functions."
  )

}

class TryTransformEntry extends EntryExtension {

  override def packageName: String = "Try"

  override def entryName: String = "Transform"
  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(IsTryable()))
    case 1 =>
      val ExpArg(_, t) = prevMandatoryArgs(0)
      val innerType = removeProp(t, SnapiIsTryableTypeProperty())
      Right(
        ExpParam(FunType(Vector(innerType), Vector.empty, DoesNotHaveTypeProperties(Set(SnapiIsTryableTypeProperty()))))
      )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val FunType(_, _, outType, _) = mandatoryArgs(1).t
    val t = addProp(outType, SnapiIsTryableTypeProperty())
    Right(t)
  }

}

class TryIsErrorEntry extends EntryExtension {

  override def packageName: String = "Try"

  override def entryName: String = "IsError"

  override def docs: EntryDoc = EntryDoc(
    "Checks whether a value is an error.",
    examples = List(
      ExampleDoc("""let x: string = "hi" in Try.IsError(x)""", result = Some("false")),
      ExampleDoc("""let x: int = Error.Build("failure!") in Try.IsError(x)""", result = Some("true"))
    ),
    params = List(ParamDoc("value", TypeDoc(List("anything")), "The value to check.")),
    ret = Some(ReturnDoc("True if the value is an error, false otherwise.", retType = Some(TypeDoc(List("bool")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(
      ExpParam(IsTryable())
    )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = mandatoryArgs(0).t match {
    case _: SnapiIterableType => Left("cannot be applied to a collection")
    case _ => Right(SnapiBoolType())
  }

}

class TryIsSuccessEntry extends EntryExtension {

  override def packageName: String = "Try"

  override def entryName: String = "IsSuccess"

  override def docs: EntryDoc = EntryDoc(
    "Checks whether a value is success, i.e. not an error.",
    examples = List(
      ExampleDoc("""let x: string = "hi" in Try.IsSuccess(x) // true""", result = Some("true")),
      ExampleDoc("""let x: int = Error.Build("failure!") in Try.IsSuccess(x) // false""", result = Some("false"))
    ),
    params = List(ParamDoc("value", TypeDoc(List("anything")), "The value to check.")),
    ret = Some(ReturnDoc("True if the value is valid, false if it is an error.", retType = Some(TypeDoc(List("bool")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(
      ExpParam(IsTryable())
    )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = mandatoryArgs(0).t match {
    case _: SnapiIterableType => Left("cannot be applied to a collection")
    case _ => Right(SnapiBoolType())
  }

}

class TryFlatMapEntry extends EntryExtension {

  override def packageName: String = "Try"

  override def entryName: String = "FlatMap"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(AnythingType()))
    case 1 => Right(ExpParam(FunType(Vector(AnythingType()), Vector.empty, AnythingType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val FunType(_, _, outType, _) = mandatoryArgs(1).t
    assert(getProps(outType).contains(SnapiIsTryableTypeProperty()))
    Right(outType)
  }

}

class TryUnsafeGetEntry extends EntryExtension {

  override def packageName: String = "Try"

  override def entryName: String = "UnsafeGet"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(
      ExpParam(IsTryable())
    )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(removeProp(mandatoryArgs(0).t, SnapiIsTryableTypeProperty()))
  }

}
