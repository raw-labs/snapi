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

import raw.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.compiler.base.source.Type
import raw.compiler.rql2._
import raw.compiler.rql2.source._

class LongPackage extends PackageExtension {

  /**
   * Name of the package.
   */
  override def name: String = "Long"

  /**
   * Package documentation.
   */
  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the long type."
  )

}

class LongFromEntry extends EntryExtension {

  override def packageName: String = "Long"

  override def entryName: String = "From"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Builds a long from a number or string.",
    examples = List(
      ExampleDoc("""Long.From(1)""", result = Some("1L")),
      ExampleDoc("""Long.From("1")""", result = Some("1L")),
      ExampleDoc("""Long.From(1.5)""", result = Some("1L"))
    ),
    params = List(
      ParamDoc("value", TypeDoc(List("number", "string")), "The value to convert to long.")
    ),
    ret = Some(
      ReturnDoc(
        "The long representation of the value.",
        Some(TypeDoc(List("long")))
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
    val props: Set[Rql2TypeProperty] = mandatoryArgs(0).t match {
      case _: Rql2StringType => Set(Rql2IsTryableTypeProperty())
      case _ => Set.empty[Rql2TypeProperty]
    }
    Right(Rql2LongType(props))
  }

}

class LongRangeEntry extends EntryExtension {

  override def packageName: String = "Long"

  override def entryName: String = "Range"

  override def docs: EntryDoc = EntryDoc(
    summary = "Builds a collection of longs between two specified values.",
    examples = List(ExampleDoc("""Long.Range(0L, 10L, step=2L)""", result = Some("[0L, 2L, 4L, 6L, 8L]"))),
    params = List(
      ParamDoc("start", TypeDoc(List("int")), "The starting value."),
      ParamDoc("end", TypeDoc(List("int")), "The end value (not included)."),
      ParamDoc("step", TypeDoc(List("int")), "The step value (default: 1L).", isOptional = true)
    ),
    ret = Some(
      ReturnDoc(
        "The collection of longs between start and end (not included) with the given step interval.",
        Some(TypeDoc(List("collection(long)")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def optionalParams: Option[Set[String]] = Some(Set("step"))

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2LongType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    assert(idn == "step")
    Right(ExpParam(Rql2LongType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IterableType(Rql2LongType()))
  }

}
