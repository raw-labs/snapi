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

import raw.compiler.base.source.{AnythingType, Type}
import raw.compiler.common.source.{GeneratorType, IterableType}
import raw.compiler.rql2._
import raw.compiler.rql2.source.Rql2IsTryableTypeProperty
import raw.compiler.{EntryDoc, PackageDoc}

class InternalPackage extends PackageExtension {

  override def name: String = "Internal"

  override def docs: PackageDoc = ???

}

class ToCommonInternalEntry extends EntryExtension {

  override def packageName: String = "Internal"

  override def entryName: String = "ToCommon"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    // Type checking is done on return type.
    Right(ExpParam(AnythingType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val arg = mandatoryArgs(0)
    // TryType is handled by 'toL0' in RQL2, so it will never be visible.
    val topType = removeProp(arg.t, Rql2IsTryableTypeProperty())
    val commonType = rql2ToCommonType(topType) match {
      case IterableType(innerType) => GeneratorType(innerType)
      case t => t
    }
    Right(commonType)
  }

}

//class FromCommonInternalEntry extends L0EntryExtension {
//
//  override def docs: EntryDoc = ???
//
//  override def nrMandatoryParams: Int = 1
//
//  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
//    // Type checking is done on return type.
//    Right(ExpParam(AnythingType()))
//  }
//
//  override def returnType(
//      mandatoryArgs: Seq[Arg],
//      optionalArgs: Seq[(String, Arg)],
//      varArgs: Seq[Arg]
//  ): Either[String, Type] = {
//    val arg = mandatoryArgs(0)
//    Right(commonToRql2Type(arg.t))
//  }
//
//  override def toL0(
//      t: Type,
//      args: Seq[L0Arg]
//  ): Either[String, Exp] = {
//    args(0).t match {
//      case GeneratorType(_) => Right(L0.source.GeneratorToIterable(args(0).e))
//      case _ => Right(args(0).e)
//    }
//  }
//
//}
