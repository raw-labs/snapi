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

import com.rawlabs.snapi.frontend.api.{EntryDoc, PackageDoc}
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{Arg, EntryExtension, ExpParam, PackageExtension, Param}
import com.rawlabs.snapi.frontend.snapi.source._

object NullableTryablePackageBuilder {

  object FlatMap {
    def apply(e: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("NullableTryable"), "FlatMap"), Vector(FunAppArg(e, None), FunAppArg(f, None)))
    }
  }
}

class NullableTryablePackage extends PackageExtension {

  override def name: String = "NullableTryable"

  override def docs: PackageDoc = ???

}

// FlatMapNullableTryableEntry looks like a regular flatMap functionality but it is
// more flexible. Mainly, the object in input can be an error, a nullable or both, and the
// output of the function can also be error, nullable or both.
class FlatMapNullableTryableEntry extends EntryExtension with StrictLogging {

  override def packageName: String = "NullableTryable"

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
    Right(outType)
  }

}
