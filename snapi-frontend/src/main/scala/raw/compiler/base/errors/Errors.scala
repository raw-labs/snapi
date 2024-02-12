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

package raw.compiler.base.errors

import raw.compiler.base.source._
import raw.client.api.{ErrorMessage, ErrorRange, HintMessage, InfoMessage, Message, WarningMessage}
import raw.compiler.rql2.errors.ErrorsPrettyPrinter

object CompilationMessageMapper {

  /**
   * *
   *
   * @param compilerMessage internal compiler message to be converted into a message
   * @param range range of the compiler message
   * @param format function to format the message
   * @return a message that is propagated to the user
   */
  def toMessage(compilerMessage: CompilerMessage, range: List[ErrorRange], format: BaseNode => String): Message = {
    compilerMessage match {
      case e: ErrorCompilerMessage => ErrorMessage(ErrorsPrettyPrinter.format(e), range, e.code)
      case w: WarningCompilerMessage => WarningMessage(ErrorsPrettyPrinter.format(w), range, w.code)
      case i: InfoCompilerMessage => InfoMessage(ErrorsPrettyPrinter.format(i), range, i.code)
      case h: HintCompilerMessage => HintMessage(ErrorsPrettyPrinter.format(h), range, h.code)
      case _ => throw new AssertionError("Unknown message type")
    }
  }
}

trait CompilerMessage extends BaseNode {
  def node: BaseNode
  val code: String
}

trait WarningCompilerMessage extends CompilerMessage

final case class MissingSecretWarning(
    node: BaseNode,
    reason: String = MissingSecretWarning.message
) extends WarningCompilerMessage {
  val code: String = MissingSecretWarning.code
}
object MissingSecretWarning {
  val code: String = "missingSecret"
  val message: String = "secret is not defined"
}

trait InfoCompilerMessage extends CompilerMessage
trait HintCompilerMessage extends CompilerMessage
trait ErrorCompilerMessage extends CompilerMessage

final case class InvalidSemantic(
    node: BaseNode,
    reason: String,
    hint: Option[String] = None,
    suggestions: Seq[String] = Seq.empty
) extends ErrorCompilerMessage {
  val code: String = InvalidSemantic.code
}
object InvalidSemantic {
  val code: String = "invalidSemantic"
  val message: String = "invalid semantic"
}

final case class UnexpectedType(
    node: BaseNode,
    actual: Type,
    expected: Type,
    hint: Option[String] = None,
    suggestions: Seq[String] = Seq.empty
) extends ErrorCompilerMessage {
  val code: String = UnexpectedType.code
}
object UnexpectedType {
  val code: String = "unexpectedType"
  val message: String = "unexpected type"
}

/**
 * Unexpected value
 * Expected <expected> but got <actual>.
 * Similar to UnexpectedType, but the 'actual' is a free-from message.
 * @param node Node with unsupported value.
 * @param expected The expected type (possibly a constraint).
 * @param actual The actual value.
 */
final case class UnexpectedValue(node: BaseNode, expected: Type, actual: String) extends ErrorCompilerMessage {
  val code: String = UnexpectedValue.code
}
object UnexpectedValue {
  val code: String = "unexpectedValue"
  val message: String = "unexpected value"
}

final case class UnknownDecl(node: BaseIdnNode, hint: Option[String] = None, suggestions: Seq[String] = Seq.empty)
    extends ErrorCompilerMessage {
  val code: String = code
}
object UnknownDecl {
  val code: String = "UnknownDecl"
  val message: String = "unknown declaration"
}

final case class MultipleDecl(node: BaseIdnNode) extends ErrorCompilerMessage {
  val code: String = MultipleDecl.code
}
object MultipleDecl {
  val code: String = "multipleDecl"
  val message: String = "multiple declarations"
}

/**
 * Unsupported type.
 *
 * `t` is the unsupported type.
 *
 * @param node Node with unsupported type.
 * @param t The type that is unsupported.
 * @param parent The top-level type. (e.g. if collection(int). Set to None if t == parent.
 */
final case class UnsupportedType(node: BaseNode, t: Type, parent: Option[Type]) extends ErrorCompilerMessage {
  val code: String = code
}
object UnsupportedType {
  val code: String = "unsupportedType"
  val message: String = "unsupported type"
}

final case class ExternalError(node: BaseNode, language: String, errors: Seq[ErrorMessage])
    extends ErrorCompilerMessage {
  val code: String = code
}
object ExternalError {
  val code: String = "externalError"
  val message: String = "external error"
}
