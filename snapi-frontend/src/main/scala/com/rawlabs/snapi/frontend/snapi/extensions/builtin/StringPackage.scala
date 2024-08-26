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

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{
  Arg,
  EntryExtension,
  ExpParam,
  PackageExtension,
  Param,
  ShortEntryExtension
}
import com.rawlabs.snapi.frontend.snapi.source._

class StringPackage extends PackageExtension {

  override def name: String = "String"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the string type."
  )

}

object StringPackage extends StringPackage {

  def outputWriteSupport(dataType: Type): Boolean = {
    dataType.isInstanceOf[SnapiStringType] // nullable/tryable or not. All are supported
  }
}

class StringFromEntry extends EntryExtension {

  override def packageName: String = "String"

  override def entryName: String = "From"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Builds a string from a number, bool, temporal or location.",
    params = List(
      ParamDoc("value", TypeDoc(List("number", "bool", "temporal")), "The value to convert to string.")
    ),
    examples = List(
      ExampleDoc("""String.From(123)""", result = Some(""""123"""")),
      ExampleDoc("""String.From(true)""", result = Some(""""true"""")),
      ExampleDoc("""String.From(Date.Build(1975, 6, 23))""", result = Some(""""1975-06-23""""))
    ),
    ret = Some(ReturnDoc("The string representation of the value.", retType = Some(TypeDoc(List("string")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(
      ExpParam(
        OneOfType(
          SnapiByteType(),
          SnapiShortType(),
          SnapiIntType(),
          SnapiLongType(),
          SnapiFloatType(),
          SnapiDoubleType(),
          SnapiDecimalType(),
          SnapiBoolType(),
          SnapiDateType(),
          SnapiTimeType(),
          SnapiTimestampType(),
          SnapiIntervalType(),
          SnapiLocationType()
        )
      )
    )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(SnapiStringType())

}

class StringReadEntry extends EntryExtension {

  override def packageName: String = "String"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads the contents of a location as a string.",
    params = List(ParamDoc(name = "location", typeDoc = TypeDoc(List("location")), "The location to read.")),
    examples = List(
      ExampleDoc("""String.Read("file:///tmp/test.txt")""")
    ),
    ret = Some(ReturnDoc("The contents of the location as a string.", retType = Some(TypeDoc(List("string")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(SnapiLocationType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("encoding"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(ExpParam(SnapiStringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiStringType(Set(SnapiIsTryableTypeProperty())))
  }

}

class StringContainsEntry
    extends ShortEntryExtension(
      "String",
      "Contains",
      Vector(SnapiStringType(), SnapiStringType()),
      SnapiBoolType(),
      EntryDoc(
        summary = "Returns true if a given string contains another given string.",
        params = List(
          ParamDoc("s1", TypeDoc(List("string")), "The input string to search within."),
          ParamDoc("s2", TypeDoc(List("string")), "The substring to search for within s1.")
        ),
        examples = List(
          ExampleDoc("""String.Contains("Snapi", "api")""", result = Some("""true""")),
          ExampleDoc("""String.Contains("Snapi", "API")""", result = Some("""false"""))
        ),
        ret = Some(ReturnDoc("True if s1 contains s2, false otherwise.", retType = Some(TypeDoc(List("bool")))))
      )
    )

class StringTrimEntry
    extends ShortEntryExtension(
      "String",
      "Trim",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Removes white space characters from the beginning and end of a string.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to be trimmed.")),
        examples = List(
          ExampleDoc("""String.Trim("  abc ")""", result = Some(""""abc"""")),
          ExampleDoc("""String.Trim("abc ")""", result = Some(""""abc""""))
        ),
        ret = Some(ReturnDoc("The trimmed string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringLTrimEntry
    extends ShortEntryExtension(
      "String",
      "LTrim",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Removes white space characters from the beginning of a string.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to be trimmed.")),
        examples = List(
          ExampleDoc("""String.LTrim("  abc ")""", result = Some(""""abc"""")),
          ExampleDoc("""String.LTrim("abc ")""", result = Some(""""abc """")),
          ExampleDoc("""String.LTrim(null)""", result = Some("""null"""))
        ),
        ret = Some(ReturnDoc("The trimmed string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringRTrimEntry
    extends ShortEntryExtension(
      "String",
      "RTrim",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Removes white space characters from the end of a string.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to be trimmed.")),
        examples = List(
          ExampleDoc("""String.RTrim("  abc ")""", result = Some(""""  abc"""")),
          ExampleDoc("""String.RTrim("  abc")""", result = Some(""""  abc""""))
        ),
        ret = Some(ReturnDoc("The trimmed string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringReplaceEntry extends EntryExtension {

  override def packageName: String = "String"

  override def entryName: String = "Replace"

  override def docs: EntryDoc = EntryDoc(
    summary = "Replace all matches of substring by a new string.",
    params = List(
      ParamDoc("string", TypeDoc(List("string")), "The string to be analyzed."),
      ParamDoc("pattern", TypeDoc(List("string")), "The substring to match."),
      ParamDoc(
        "newSubString",
        TypeDoc(List("string")),
        "The new substring to replace matches with."
      )
    ),
    examples = List(
      ExampleDoc("""String.Replace("Hello John", "John", "Jane")""", result = Some(""""Hello Jane"""")),
      ExampleDoc("""String.Replace("Hello John", "o", "+")""", result = Some(""""Hell+ J+hn!""""))
    ),
    ret = Some(ReturnDoc("The string with all matches replaced.", retType = Some(TypeDoc(List("string")))))
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
    idx match {
      case 0 => Right(ExpParam(SnapiStringType()))
      case 1 => Right(ExpParam(SnapiStringType()))
      case 2 => Right(ExpParam(SnapiStringType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiStringType())
  }

}

class StringReverseEntry
    extends ShortEntryExtension(
      "String",
      "Reverse",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Reverses a string.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to reverse.")),
        examples = List(ExampleDoc("""String.Reverse("1234")""", result = Some(""""4321""""))),
        ret = Some(ReturnDoc("The reversed string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringReplicateEntry
    extends ShortEntryExtension(
      "String",
      "Replicate",
      Vector(SnapiStringType(), SnapiIntType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Replicates a string by coping the substring for the specific number of repetitions.",
        params = List(
          ParamDoc("string", TypeDoc(List("string")), "The string to replicate."),
          ParamDoc("repetitions", TypeDoc(List("int")), "The number of repetitions.")
        ),
        examples = List(
          ExampleDoc("""String.Replicate("x", 4)""", result = Some(""""xxxx"""")),
          ExampleDoc("""String.Replicate("abc,", 2)""", result = Some(""""abc,abc,""""))
        ),
        ret = Some(ReturnDoc("The replicated string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringUpperEntry
    extends ShortEntryExtension(
      "String",
      "Upper",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Convert a string to uppercase.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to convert to uppercase.")),
        examples = List(
          ExampleDoc("""String.Upper("abC") // "ABC"""", result = Some(""""ABC"""")),
          ExampleDoc("""String.Upper(null) // null""", result = Some("null"))
        ),
        ret = Some(ReturnDoc("The converted string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringLowerEntry
    extends ShortEntryExtension(
      "String",
      "Lower",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Convert a string to lowercase.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to convert to lowercase.")),
        examples = List(
          ExampleDoc("""lower("abC")""", result = Some(""""abc"""")),
          ExampleDoc("""lower(null)""", result = Some("null"))
        ),
        ret = Some(ReturnDoc("The converted string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringSplitEntry
    extends ShortEntryExtension(
      "String",
      "Split",
      Vector(SnapiStringType(), SnapiStringType()),
      SnapiListType(SnapiStringType()),
      EntryDoc(
        summary = "Split a string into a list of strings given a separator.",
        params = List(
          ParamDoc("string", TypeDoc(List("string")), "The string to split."),
          ParamDoc("separator", TypeDoc(List("string")), "The  separator by which the string is split.")
        ),
        examples = List(
          ExampleDoc(
            """String.Split("Value1||Value2", "||")""",
            result = Some("""["Value1","Value2"]""")
          ),
          ExampleDoc(
            """String.Split("Value1|Value2", "|")""",
            result = Some("""["Value1","Value2"]""")
          )
        ),
        ret = Some(ReturnDoc("The list of strings.", retType = Some(TypeDoc(List("list(string)")))))
      )
    )

class StringLengthEntry
    extends ShortEntryExtension(
      "String",
      "Length",
      Vector(SnapiStringType()),
      SnapiIntType(),
      EntryDoc(
        summary = "Compute the size of a string.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to compute the size.")),
        examples = List(ExampleDoc("""String.Length("Hello John")""", result = Some("10")))
      )
    )

class StringSubStringEntry
    extends ShortEntryExtension(
      "String",
      "SubString",
      Vector(SnapiStringType(), SnapiIntType(), SnapiIntType()),
      SnapiStringType(),
      EntryDoc(
        summary = """Extract a substring from a string, given the start index and length of extraction.""",
        info = Some("""The index starts at 1. A negative number as the length means the remainder of the string."""),
        params = List(
          ParamDoc("string", TypeDoc(List("string")), "The string of which a substring is extracted."),
          ParamDoc("index", TypeDoc(List("int")), "The start position to extract from."),
          ParamDoc("size", TypeDoc(List("int")), "The size of the substring to extract.")
        ),
        examples = List(
          ExampleDoc("""String.SubString("Hello John", 4, 2)""", result = Some(""""lo"""")),
          ExampleDoc("""String.SubString("Hello John", 7, -1)""", result = Some(""""John""""))
        ),
        ret = Some(ReturnDoc("The extracted substring.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringCountSubStringEntry
    extends ShortEntryExtension(
      "String",
      "CountSubString",
      Vector(SnapiStringType(), SnapiStringType()),
      SnapiIntType(),
      EntryDoc(
        summary = "Count the number of occurrences of a substring in a string.",
        params = List(
          ParamDoc("string", TypeDoc(List("string")), "The string on which the occurrences are counted."),
          ParamDoc("substring", TypeDoc(List("string")), "The substring which is counted.")
        ),
        examples = List(
          ExampleDoc("""String.CountSubString("aXbX", "X")""", result = Some("2")),
          ExampleDoc("""String.CountSubString("aXbX", "y")""", result = Some("0")),
          ExampleDoc("""String.CountSubString("aXbX", "aX")""", result = Some("1"))
        ),
        ret = Some(ReturnDoc("The number of occurrences.", retType = Some(TypeDoc(List("int")))))
      )
    )

class StringStartsWithEntry
    extends ShortEntryExtension(
      "String",
      "StartsWith",
      Vector(SnapiStringType(), SnapiStringType()),
      SnapiBoolType(),
      EntryDoc(
        summary = """Check if a string starts with a given prefix.""",
        params = List(
          ParamDoc("string", TypeDoc(List("string")), "The string to find the prefix."),
          ParamDoc("prefix", TypeDoc(List("string")), "The prefix to match.")
        ),
        examples = List(
          ExampleDoc("""String.StartsWith("Hello world", "Hello")""", result = Some("true")),
          ExampleDoc("""String.StartsWith("Hello world", "world")""", result = Some("false"))
        ),
        ret = Some(
          ReturnDoc(
            "True if the beginning of a string matches the prefix, false otherwise.",
            retType = Some(TypeDoc(List("bool")))
          )
        )
      )
    )

class StringEmptyEntry
    extends ShortEntryExtension(
      "String",
      "Empty",
      Vector(SnapiStringType()),
      SnapiBoolType(),
      EntryDoc(
        summary = """Returns true the string is empty.""",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to check if empty.")),
        examples = List(
          ExampleDoc("""String.Empty("")""", result = Some("true")),
          ExampleDoc("""String.Empty("Hello!")""", result = Some("false"))
        ),
        ret = Some(ReturnDoc("True if the string is empty, false otherwise.", retType = Some(TypeDoc(List("bool")))))
      )
    )

class Base64EntryExtension
    extends ShortEntryExtension(
      "String",
      "Base64",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Returns the base64 encoding of a string",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to encode as base64.")),
        examples = List(ExampleDoc("""String.Base64("Hello World!")""", result = Some(""""SGVsbG8gV29ybGQh""""))),
        ret = Some(ReturnDoc("The base64 encoded string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringEncodeEntry
    extends ShortEntryExtension(
      "String",
      "Encode",
      Vector(SnapiStringType(), SnapiStringType()),
      SnapiBinaryType(Set(SnapiIsTryableTypeProperty())),
      EntryDoc(
        summary = "Converts a string to a binary, given an encoding.",
        params = List(
          ParamDoc("string", TypeDoc(List("string")), "The string to encode."),
          ParamDoc("encoding", TypeDoc(List("string")), "The encoding to use.")
        ),
        examples = List(ExampleDoc("""String.EncodeString("Hello world", "utf-8"))""")),
        ret = Some(ReturnDoc("The encoded binary.", retType = Some(TypeDoc(List("binary")))))
      )
    )

class StringDecodeEntry
    extends ShortEntryExtension(
      "String",
      "Decode",
      Vector(SnapiBinaryType(), SnapiStringType()),
      SnapiStringType(Set(SnapiIsTryableTypeProperty())),
      EntryDoc(
        summary = "Builds a string from a binary, given an encoding.",
        params = List(
          ParamDoc("string", TypeDoc(List("string")), "The string to decode."),
          ParamDoc("encoding", TypeDoc(List("string")), "The encoding that the binary value uses.")
        ),
        examples = List(
          ExampleDoc(
            """let
              |  b = String.Encode("Hello world", "utf-8")
              |in
              |  String.Decode(b, "utf-8")""".stripMargin,
            result = Some(""""Hello world"""")
          )
        ),
        ret = Some(ReturnDoc("The decoded string.", retType = Some(TypeDoc(List("string")))))
      )
    )

class StringLevenshteinDistanceEntry
    extends ShortEntryExtension(
      "String",
      "LevenshteinDistance",
      Vector(SnapiStringType(), SnapiStringType()),
      SnapiIntType(),
      EntryDoc(
        summary = """Calculates the Levenshtein distance between two strings.""",
        info = Some(
          """The Levenshtein distance between two words is the minimum number of single-character edits (insertions, deletions or substitutions) required to change one word into the other."""
        ),
        params = List(
          ParamDoc(
            "string1",
            TypeDoc(List("string")),
            "The string from which the levenshtein distance is calculated."
          ),
          ParamDoc("string2", TypeDoc(List("string")), "The string to which the levenshtein distance is calculated.")
        ),
        examples = List(
          ExampleDoc("""String.LevenshteinDistance("Hello world", "Hello warld")""", result = Some("1")),
          ExampleDoc("""String.LevenshteinDistance("Hello world", "Hello John")""", result = Some("4"))
        ),
        ret = Some(ReturnDoc("The levenshtein distance between the two strings.", retType = Some(TypeDoc(List("int")))))
      )
    )

class StringReadLinesEntry extends EntryExtension {

  override def packageName: String = "String"

  override def entryName: String = "ReadLines"

  override def docs: EntryDoc = EntryDoc(
    "Reads the contents of a location as lines of text",
    params = List(
      ParamDoc(
        name = "location",
        typeDoc = TypeDoc(List("location")),
        description = "The location to read lines from."
      ),
      ParamDoc(
        "encoding",
        typeDoc = TypeDoc(List("string")),
        description = """Specifies the encoding of the data;
          |if not specified defaults to utf-8.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""String.ReadLines("http://somewhere/data.log")"""")),
    ret = Some(ReturnDoc("The lines of text in the location.", retType = Some(TypeDoc(List("collection(string)")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(SnapiLocationType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("encoding"))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiIterableType(SnapiStringType()))
  }
}

class StringCapitalizeEntry
    extends ShortEntryExtension(
      "String",
      "Capitalize",
      Vector(SnapiStringType()),
      SnapiStringType(),
      EntryDoc(
        summary = "Makes the first character of a string be uppercase and the rest lowercase.",
        params = List(ParamDoc("string", TypeDoc(List("string")), "The string to be capitalized.")),
        examples = List(
          ExampleDoc("""String.Capitalize("SNAPI")""", result = Some(""""Snapi"""")),
          ExampleDoc("""String.Capitalize("snapi")""", result = Some(""""Snapi"""")),
          ExampleDoc("""String.Capitalize("snAPI")""", result = Some(""""Snapi"""")),
          ExampleDoc("""String.Capitalize("Snapi")""", result = Some(""""Snapi""""))
        ),
        ret = Some(ReturnDoc("The capitalized string.", retType = Some(TypeDoc(List("string")))))
      )
    )
