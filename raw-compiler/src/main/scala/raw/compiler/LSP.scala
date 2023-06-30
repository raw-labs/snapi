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

import com.fasterxml.jackson.annotation.JsonSubTypes.{Type => JsonType}
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

//This one is repeated on Kiama, ErrorPosition, and at some points in Repose,
//is there any way for every project to reuse the same classes?
final case class Pos(line: Int, column: Int)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[FormatCodeLSPRequest], name = "formatCode"),
    new JsonType(value = classOf[DotAutoCompleteLSPRequest], name = "dotAutoComplete"),
    new JsonType(value = classOf[WordAutoCompleteLSPRequest], name = "wordAutoComplete"),
    new JsonType(value = classOf[HoverLSPRequest], name = "hover"),
    new JsonType(value = classOf[RenameLSPRequest], name = "rename"),
    new JsonType(value = classOf[DefinitionLSPRequest], name = "definition"),
    new JsonType(value = classOf[ValidateLSPRequest], name = "validate"),
    new JsonType(value = classOf[AiValidateLSPRequest], name = "aiValidate")
  )
)
sealed trait LSPRequest {
  val code: String
  val environment: ProgramEnvironment
}
final case class FormatCodeLSPRequest(code: String, environment: ProgramEnvironment) extends LSPRequest
final case class DotAutoCompleteLSPRequest(code: String, environment: ProgramEnvironment, position: Pos)
    extends LSPRequest
final case class WordAutoCompleteLSPRequest(
    code: String,
    environment: ProgramEnvironment,
    prefix: String,
    position: Pos
) extends LSPRequest
final case class HoverLSPRequest(code: String, environment: ProgramEnvironment, position: Pos) extends LSPRequest
final case class RenameLSPRequest(code: String, environment: ProgramEnvironment, position: Pos) extends LSPRequest
final case class ValidateLSPRequest(code: String, environment: ProgramEnvironment) extends LSPRequest
final case class AiValidateLSPRequest(code: String, environment: ProgramEnvironment) extends LSPRequest
final case class DefinitionLSPRequest(code: String, environment: ProgramEnvironment, position: Pos) extends LSPRequest

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[FormatCodeLSPResponse], name = "formatCode"),
    new JsonType(value = classOf[AutoCompleteLSPResponse], name = "autoComplete"),
    new JsonType(value = classOf[HoverLSPResponse], name = "hover"),
    new JsonType(value = classOf[DefinitionLSPResponse], name = "definition"),
    new JsonType(value = classOf[RenameLSPResponse], name = "rename"),
    new JsonType(value = classOf[ErrorLSPResponse], name = "error")
  )
)
sealed trait LSPResponse
final case class FormatCodeLSPResponse(code: String, errors: List[ErrorMessage]) extends LSPResponse
final case class AutoCompleteLSPResponse(entries: Array[LSPAutoCompleteResponse], errors: List[ErrorMessage])
    extends LSPResponse
final case class HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) extends LSPResponse
final case class DefinitionLSPResponse(position: Pos, errors: List[ErrorMessage]) extends LSPResponse
final case class RenameLSPResponse(positions: Array[Pos], errors: List[ErrorMessage]) extends LSPResponse
final case class ErrorLSPResponse(errors: List[ErrorMessage]) extends LSPResponse

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[FieldLSPAutoCompleteResponse], name = "field"),
    new JsonType(value = classOf[LetBindLSPAutoCompleteResponse], name = "bind"),
    new JsonType(value = classOf[LetFunLSPAutoCompleteResponse], name = "function"),
    new JsonType(value = classOf[LetFunRecAutoCompleteResponse], name = "recursiveFunction"),
    new JsonType(value = classOf[FunParamLSPAutoCompleteResponse], name = "functionParameter"),
    new JsonType(value = classOf[PackageLSPAutoCompleteResponse], name = "package"),
    new JsonType(value = classOf[PackageEntryLSPAutoCompleteResponse], name = "packageEntry")
  )
)
sealed trait LSPAutoCompleteResponse
final case class FieldLSPAutoCompleteResponse(name: String, tipe: String) extends LSPAutoCompleteResponse
final case class LetBindLSPAutoCompleteResponse(name: String, tipe: String) extends LSPAutoCompleteResponse
final case class LetFunLSPAutoCompleteResponse(name: String, tipe: String) extends LSPAutoCompleteResponse
final case class LetFunRecAutoCompleteResponse(name: String, tipe: String) extends LSPAutoCompleteResponse
final case class FunParamLSPAutoCompleteResponse(name: String, tipe: String) extends LSPAutoCompleteResponse
final case class PackageLSPAutoCompleteResponse(name: String, doc: PackageDoc) extends LSPAutoCompleteResponse
final case class PackageEntryLSPAutoCompleteResponse(name: String, doc: EntryDoc) extends LSPAutoCompleteResponse

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  Array(
    new JsonType(value = classOf[PackageLSPHoverResponse], name = "package"),
    new JsonType(value = classOf[PackageEntryLSPHoverResponse], name = "entry"),
    new JsonType(value = classOf[TypeHoverResponse], name = "tipe")
  )
)
sealed trait LSPHoverResponse
final case class PackageLSPHoverResponse(name: String, doc: PackageDoc) extends LSPHoverResponse
final case class PackageEntryLSPHoverResponse(name: String, doc: EntryDoc) extends LSPHoverResponse
final case class TypeHoverResponse(name: String, tipe: String) extends LSPHoverResponse
