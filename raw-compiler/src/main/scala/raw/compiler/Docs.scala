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

package raw.compiler

/**
 * Package documentation.
 *
 * @param description Description of package.
 * @param info Text prefixed with Info sign.
 * @param warning Text prefixed with Warning sign.
 * @param danger Text prefixed with Danger sign.
 */
final case class PackageDoc(
    description: String,
    info: Option[String] = None,
    warning: Option[String] = None,
    danger: Option[String] = None
) {
  Doc.grammarCheck(description)
  Doc.grammarCheck(info)
  Doc.grammarCheck(warning)
  Doc.grammarCheck(danger)
}

/**
 * Package entry documentation.
 *
 * @param summary Short-form documentation for the entry.
 * @param description Long-form documentation for the entry.
 * @param example Usage example.
 * @param params Documentation of parameters.
 * @param ret Documentation of return value.
 * @param info Text prefixed with Info sign.
 * @param warning Text prefixed with Warning sign.
 * @param danger Text prefixed with Danger sign.
 */
final case class EntryDoc(
    summary: String,
    description: Option[String] = None,
    examples: List[ExampleDoc] = List.empty,
    params: List[ParamDoc] = List.empty,
    ret: Option[ReturnDoc] = None,
    info: Option[String] = None,
    warning: Option[String] = None,
    danger: Option[String] = None
) {

  Doc.grammarCheck(description)
  Doc.grammarCheck(info)
  Doc.grammarCheck(warning)
  Doc.grammarCheck(danger)

  assert(!summary.contains('\n'), "Summary must not contain newlines.")

  def getSyntax = s"(${params.map(par => par.syntax).mkString(", ")})"
}

final case class ExampleDoc(
    example: String,
    result: Option[String] = None
) {
  if (example.contains("```")) throw new AssertionError(
    "Example code contains ``` and this isn't supported. Must only contain RQL2 code."
  )
//  if (
//    !example.contains("s3://") && !example.contains("https://") && !example
//      .contains("http://") && example.contains("//")
//  ) throw new AssertionError(
//    "Example result contains // and this isn't supported. Use the result field instead."
//  )
  result.foreach { r =>
    if (r.contains("```")) throw new AssertionError(
      "Return contains ``` and this isn't supported. Must contain only data."
    )
    if (r.contains("//")) throw new AssertionError(
      "Return contains // and this isn't supported. Must contain only data."
    )
  }
}

/**
 * Documentation for a parameter.
 *
 * @param name Name of the parameter.
 * @param typeDoc Type documentation.
 * @param description Description of the parameter.
 * @param isOptional True/False.
 * @param isVarArg True/False.
 * @param info Text prefixed with Info sign.
 * @param warning Text prefixed with Warning sign.
 * @param danger Text prefixed with Danger sign.
 */
final case class ParamDoc(
    name: String,
    typeDoc: TypeDoc,
    description: String,
    isOptional: Boolean = false,
    isVarArg: Boolean = false,
    info: Option[String] = None,
    warning: Option[String] = None,
    danger: Option[String] = None,
    customSyntax: Option[String] = None
) {

  Doc.grammarCheck(description)
  Doc.grammarCheck(info)
  Doc.grammarCheck(warning)
  Doc.grammarCheck(danger)

  def syntax: String = {
    customSyntax match {
      case Some(syntax) => syntax
      case None =>
        if (isVarArg) s"$name: ${typeDoc.possibleTypes.mkString(" or ")} ..."
        else s"$name: ${if (isOptional) "optional " else ""}${typeDoc.possibleTypes.mkString(" or ")}"
    }
  }
}

/**
 * Return documentation.
 *
 * @param description Description of the return value.
 * @param retType Return type.
 */
final case class ReturnDoc(description: String, retType: Option[TypeDoc]) {
  Doc.grammarCheck(description)
}

/**
 * Type documentation.
 *
 * @param possibleTypes List of types allowed. It's a list because can be more than one type accepted.
 */
final case class TypeDoc(possibleTypes: List[String]) {

  // Assert that list of possible types is within the accepted list.
  possibleTypes.foreach(t =>
    assert(TypeDoc.ACCEPTED_TYPES.contains(t), s"Type documentation contains unknown type: '$t'")
  )

}

object TypeDoc {
  val ACCEPTED_TYPES = Seq(
    "any",
    // Numbers
    "byte",
    "short",
    "int",
    "long",
    "float",
    "double",
    "decimal",
    "number",
    // Binary
    "binary",
    // Boolean
    "bool",
    // String
    "string",
    // Regular expression
    "regex",
    // Collections
    "collection",
    "list",
    // Function
    "function",
    // Temporals
    "date",
    "time",
    "timestamp",
    "interval",
    "temporal",
    // Location
    "location",
    // Type
    "type",
    // Record
    "record",
    // TODO (msb): To reconsider or maybe even drop!!!
    "anything",
    // Composed types
    "list(string)",
    "collection(string)",
    "record(status: int, data: binary, headers: list(record(_1: string, _2: string))))",
    "collection(int)",
    "list(int)",
    "record(format: string, comment: string, type: string, properties: list(record(name: string, value: string)), is_collection: bool, columns: list(record(col_name: string, col_type: string, nullable: bool)), sampled: bool)",
    "list(record(url: string, metadata: record(modified: timestamp, size: int, blocks: list(record(hosts: list(string), offset: long, length: long)))",
    "list(long)",
    "collection(long)",
    "list(timestamp)",
    "collection(timestamp)"
  )
}

object Doc {

  def grammarCheck(sentence: String): Unit = {
    if (sentence.isEmpty) {
      throw new AssertionError("Empty doc")
    } else if (sentence.last != '.') {
      throw new AssertionError(s"Sentence does not terminate in a dot or semi-colon: '$sentence'.")
    }
  }

  def grammarCheck(maybeSentence: Option[String]): Unit = {
    maybeSentence.map(grammarCheck)
  }

}
