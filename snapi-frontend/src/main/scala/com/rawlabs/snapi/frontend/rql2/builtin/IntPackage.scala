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

package com.rawlabs.snapi.frontend.rql2.builtin

import com.rawlabs.compiler.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.frontend.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param}
import com.rawlabs.snapi.frontend.rql2.source._

class IntPackage extends PackageExtension {

  /**
   * Name of the package.
   */
  override def name: String = "Int"

  /**
   * Package documentation.
   */
  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the int type."
  )

}

class IntFromEntry extends EntryExtension {

  override def packageName: String = "Int"

  override def entryName: String = "From"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Builds an int from a number or string.",
    examples = List(
      ExampleDoc("""Int.From("123")""", result = Some("123")),
      ExampleDoc("""Int.From(1.5)""", result = Some("1"))
    ),
    params = List(
      ParamDoc("value", TypeDoc(List("number", "string")), "The value to convert to int.")
    ),
    ret = Some(
      ReturnDoc(
        "The int representation of the value.",
        Some(TypeDoc(List("int")))
      )
    )
  )

  override def nrMandatoryParams: Int = 1

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    // Type as Try when passed a string in case the content doesn't parse.
    val props: Set[Rql2TypeProperty] = mandatoryArgs(0).t match {
      case _: Rql2StringType => Set(Rql2IsTryableTypeProperty())
      case _ => Set.empty[Rql2TypeProperty]
    }
    Right(Rql2IntType(props))
  }

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(numberOrString))
  }

}

class IntRangeEntry extends EntryExtension {

  override def packageName: String = "Int"

  override def entryName: String = "Range"

  override def docs: EntryDoc = EntryDoc(
    summary = "Builds a collection of integers between two specified values.",
    examples = List(ExampleDoc("""Int.Range(0, 10, step=2)""", result = Some("[0, 2, 4, 6, 8]"))),
    params = List(
      ParamDoc("start", TypeDoc(List("int")), "The starting value."),
      ParamDoc("end", TypeDoc(List("int")), "The end value (not included)."),
      ParamDoc("step", TypeDoc(List("int")), "The step value (default: 1).", isOptional = true)
    ),
    ret = Some(
      ReturnDoc(
        "The collection of integers between start and end (not included) with the given step interval.",
        Some(TypeDoc(List("collection(int)")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def optionalParams: Option[Set[String]] = Some(Set("step"))

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2IntType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    assert(idn == "step")
    Right(ExpParam(Rql2IntType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IterableType(Rql2IntType()))
  }

}
