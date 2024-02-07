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
import raw.client.api.ErrorMessage

trait CompilationMessage extends BaseNode {
  def node: BaseNode
}

trait WarningCompilationMessage extends CompilationMessage
// It is named MissingSecretMessage and not MissingSecret do resolve ambiguity with the MissingSecret class in the raw.client.api package.
final case class MissingSecretWarning(
    node: BaseNode,
    reason: String
) extends WarningCompilationMessage

trait InfoCompilationMessage extends CompilationMessage
trait HintCompilationMessage extends CompilationMessage
trait ErrorCompilationMessage extends CompilationMessage

final case class InvalidSemantic(
    node: BaseNode,
    reason: String,
    hint: Option[String] = None,
    suggestions: Seq[String] = Seq.empty
) extends ErrorCompilationMessage

final case class UnexpectedType(
    node: BaseNode,
    actual: Type,
    expected: Type,
    hint: Option[String] = None,
    suggestions: Seq[String] = Seq.empty
) extends ErrorCompilationMessage

/**
 * Unexpected value
 * Expected <expected> but got <actual>.
 * Similar to UnexpectedType, but the 'actual' is a free-from message.
 * @param node Node with unsupported value.
 * @param expected The expected type (possibly a constraint).
 * @param actual The actual value.
 */
final case class UnexpectedValue(node: BaseNode, expected: Type, actual: String) extends ErrorCompilationMessage

final case class UnknownDecl(node: BaseIdnNode, hint: Option[String] = None, suggestions: Seq[String] = Seq.empty)
    extends ErrorCompilationMessage

final case class MultipleDecl(node: BaseIdnNode) extends ErrorCompilationMessage

/**
 * Unsupported type.
 *
 * `t` is the unsupported type.
 *
 * @param node Node with unsupported type.
 * @param t The type that is unsupported.
 * @param parent The top-level type. (e.g. if collection(int). Set to None if t == parent.
 */
final case class UnsupportedType(node: BaseNode, t: Type, parent: Option[Type]) extends ErrorCompilationMessage

final case class ExternalError(node: BaseNode, language: String, errors: Seq[ErrorMessage])
    extends ErrorCompilationMessage
