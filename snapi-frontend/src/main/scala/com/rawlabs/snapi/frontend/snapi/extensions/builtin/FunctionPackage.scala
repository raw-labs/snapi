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

import com.rawlabs.snapi.frontend.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.snapi.source.FunType
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{Arg, EntryExtension, ExpParam, PackageExtension, Param}

class FunctionPackage extends PackageExtension {

  override def name: String = "Function"

  override def docs: PackageDoc = PackageDoc("Library of functions for the function type.")

}

class FunctionInvokeAfterEntry extends EntryExtension {

  override def packageName: String = "Function"

  override def entryName: String = "InvokeAfter"
  override def docs: EntryDoc = EntryDoc(
    "Invokes function after a delay.",
    params = List(
      ParamDoc(
        "f",
        TypeDoc(List("function")),
        "A function without arguments with the code to invoke after the delay expired."
      ),
      ParamDoc("sleep", TypeDoc(List("long")), "Delay in milliseconds before invoking the function.")
    ),
    examples = List(ExampleDoc("""Function.InvokeAfter(() -> 1+1, 100)""", result = Some("2")))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {

    idx match {
      case 0 => Right(ExpParam(FunType(Vector.empty, Vector.empty, AnythingType())))
      case 1 => Right(ExpParam(long))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val FunType(_, _, returnType, _) = mandatoryArgs(0).t
    Right(returnType)
  }

}
