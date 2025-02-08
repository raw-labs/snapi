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
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions._
import com.rawlabs.snapi.frontend.snapi.source._

class KryoPackage extends PackageExtension {

  override def name: String = "Kryo"

  override def docs: PackageDoc = ???
}

class KryoEncodeEntry extends EntryExtension {

  override def docs: EntryDoc = ???

  override def packageName: String = "Kryo"

  override def entryName: String = "Encode"

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(AnythingType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(SnapiBinaryType())

}

class KryoDecodeEntry extends EntryExtension {

  override def docs: EntryDoc = ???

  override def packageName: String = "Kryo"

  override def entryName: String = "Decode"

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    if (idx == 0) Right(ExpParam(SnapiBinaryType()))
    else Right(TypeParam(AnythingType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(mandatoryArgs(1).t)
  }

}
