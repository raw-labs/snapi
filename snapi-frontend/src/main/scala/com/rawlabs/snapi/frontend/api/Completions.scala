package com.rawlabs.snapi.frontend.api

sealed trait Completion
final case class TypeCompletion(name: String, tipe: String) extends Completion
final case class FieldCompletion(name: String, tipe: String) extends Completion
final case class LetBindCompletion(name: String, tipe: String) extends Completion
final case class LetFunCompletion(name: String, tipe: String) extends Completion
final case class LetFunRecCompletion(name: String, tipe: String) extends Completion
final case class FunParamCompletion(name: String, tipe: String) extends Completion
final case class PackageCompletion(name: String, doc: PackageDoc) extends Completion
final case class PackageEntryCompletion(name: String, doc: EntryDoc) extends Completion

final case class FormatCodeResponse(code: Option[String])
final case class HoverResponse(completion: Option[Completion])
final case class RenameResponse(positions: Array[Pos])
final case class GoToDefinitionResponse(position: Option[Pos])
final case class ValidateResponse(messages: List[Message])
final case class AutoCompleteResponse(completions: Array[Completion])

final case class Pos(line: Int, column: Int)
