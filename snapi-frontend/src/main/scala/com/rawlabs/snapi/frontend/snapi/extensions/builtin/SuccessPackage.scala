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

import com.rawlabs.compiler.{EntryDoc, PackageDoc}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.snapi.source.{
  Exp,
  FunApp,
  FunAppArg,
  PackageIdnExp,
  Proj,
  SnapiIsTryableTypeProperty,
  SnapiTypeWithProperties
}
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{Arg, EntryExtension, ExpParam, PackageExtension, Param}

object SuccessPackageBuilder {

  object Build {
    def apply(e: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Success"), "Build"), Vector(FunAppArg(e, None)))
    }
  }
}

class SuccessPackage extends PackageExtension {

  override def name: String = "Success"

  override def docs: PackageDoc = ???

}

class SuccessBuildEntry extends EntryExtension {

  override def packageName: String = "Success"

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
    mandatoryArgs(0).t match {
      case x: SnapiTypeWithProperties if x.props.contains(SnapiIsTryableTypeProperty()) =>
        return Left("argument is tryable")
      case _ =>
    }
    val t = addProp(mandatoryArgs(0).t, SnapiIsTryableTypeProperty())
    Right(t)
  }

}
