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

import com.rawlabs.compiler.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.compiler.base.source.Type
import raw.compiler.rql2._
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param}
import raw.compiler.rql2.source.{
  Rql2BoolType,
  Rql2IsNullableTypeProperty,
  Rql2IsTryableTypeProperty,
  Rql2ListType,
  Rql2StringType
}

class RegexPackage extends PackageExtension {

  /**
   * Name of the package.
   */
  override def name: String = "Regex"

  /**
   * Package documentation.
   */
  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for regular expressions."
  )

}

class RegexReplaceEntry extends EntryExtension {

  override def packageName: String = "Regex"

  override def entryName: String = "Replace"

  override def docs: EntryDoc = EntryDoc(
    summary = "Replaces matches of a regular expression pattern by a new string.",
    description = Some(
      "For more information about regex patterns see the [Regex patterns documentation](/snapi/regex-templates)."
    ),
    examples =
      List(ExampleDoc("""Regex.Replace("Heelloo John", "[aeiou]+", "_")""", result = Some(""" "H_ll__ J_hn" """))),
    params = List(
      ParamDoc("string", TypeDoc(List("string")), "The string to be analyzed."),
      ParamDoc("pattern", TypeDoc(List("string")), "The regular expression pattern to match."),
      ParamDoc(
        "newSubString",
        TypeDoc(List("string")),
        "The new substring to replace matches with."
      )
    ),
    ret = Some(ReturnDoc("The string with the replaced matches.", retType = Some(TypeDoc(List("string")))))
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2StringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2StringType(Set(Rql2IsTryableTypeProperty())))
  }

}

class RegexMatchesEntry extends EntryExtension {

  override def packageName: String = "Regex"

  override def entryName: String = "Matches"

  override def docs: EntryDoc = EntryDoc(
    summary = "Checks if a string matches a regular expression pattern.",
    description = Some(
      "For more information about regex patterns see the [Regex patterns documentation](/snapi/regex-templates)."
    ),
    examples = List(
      ExampleDoc("""Regex.Matches("1234", "\\d+")""", result = Some("true")),
      ExampleDoc("""Regex.Matches("abc 1234", "\\d+")""", result = Some("false"))
    ),
    params = List(
      ParamDoc("string", TypeDoc(List("string")), "The string to analyzer."),
      ParamDoc("pattern", TypeDoc(List("string")), "The regular expression to match.")
    ),
    ret = Some(ReturnDoc("True if the pattern matches, false otherwise.", Some(TypeDoc(List("bool")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2StringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2BoolType(Set(Rql2IsTryableTypeProperty())))
  }

}

class RegexFirstMatchInEntry extends EntryExtension {

  override def packageName: String = "Regex"

  override def entryName: String = "FirstMatchIn"

  override def docs: EntryDoc = EntryDoc(
    summary = "Finds the first match of a regular expression in a string.",
    description = Some(
      "For more information about regex patterns see the [Regex patterns documentation](/snapi/regex-templates)."
    ),
    examples = List(
      ExampleDoc("""Regex.FirstMatch("1234", "\\d+")""", result = Some(""""1234"""")),
      ExampleDoc("""Regex.FirstMatch("abc 1234", "\\d+")""", result = Some(""""1234"""")),
      ExampleDoc("""Regex.FirstMatch("abc", "\\d+")""", result = Some("""null"""))
    ),
    params = List(
      ParamDoc("string", TypeDoc(List("string")), "The string to analyze."),
      ParamDoc("pattern", TypeDoc(List("string")), "The regular expression to match.")
    ),
    ret = Some(
      ReturnDoc(
        "The first match of the regular expression in the string, or null if no match is found.",
        Some(TypeDoc(List("string")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2StringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2StringType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())))
  }

}

class RegexGroupsEntry extends EntryExtension {

  override def packageName: String = "Regex"

  override def entryName: String = "Groups"

  override def docs: EntryDoc = EntryDoc(
    summary = "Finds the list of capturing groups of a regular expression in a string.",
    description = Some(
      "For more information about regex patterns see the [Regex patterns documentation](/snapi/regex-templates)."
    ),
    examples = List(
      ExampleDoc("""Regex.Groups("23-06-1975", "(\\d+)-(\\d+)-(\\d+)")""", result = Some("""["23", "06", "1975"]""")),
      ExampleDoc("""Regex.Groups("23-06", "(\\d+)-(\\d+)-(\\d+)?")""", result = Some("""["23", "06", null]"""))
    ),
    params = List(
      ParamDoc("string", TypeDoc(List("string")), "The string to analyze."),
      ParamDoc("pattern", TypeDoc(List("string")), "The regular expression to match.")
    ),
    ret = Some(
      ReturnDoc(
        "The list of capturing groups of the regular expression in the string, or null if no match is found.",
        Some(TypeDoc(List("list(string)")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2StringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(
    Rql2ListType(
      Rql2StringType(Set(Rql2IsNullableTypeProperty())),
      Set(Rql2IsTryableTypeProperty())
    )
  )

}
