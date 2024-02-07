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

package raw.compiler.rql2.errors

import raw.compiler.base.errors.ErrorCompilationMessage
import raw.compiler.base.source.{BaseNode, Type}

object KeyNotComparable {
  val message: String = "key is not comparable"
}
final case class KeyNotComparable(node: BaseNode) extends ErrorCompilationMessage

object ItemsNotComparable {
  val message: String = "items are not comparable"
}
final case class ItemsNotComparable(node: BaseNode) extends ErrorCompilationMessage

object CannotDetermineTypeOfParameter {
  val message: String = "cannot determine type of parameter"
}
final case class CannotDetermineTypeOfParameter(node: BaseNode) extends ErrorCompilationMessage

object OutputTypeRequiredForRecursiveFunction {
  val message: String = "output type required for recursive function"
}
final case class OutputTypeRequiredForRecursiveFunction(node: BaseNode) extends ErrorCompilationMessage

object RepeatedOptionalArguments {
  val message: String = "repeated optional arguments"
}
final case class RepeatedOptionalArguments(node: BaseNode) extends ErrorCompilationMessage

object OrderSpecMustFollowOrderingFunction {
  val message: String = "order specification must follow each ordering key function"
}
final case class OrderSpecMustFollowOrderingFunction(node: BaseNode) extends ErrorCompilationMessage

object InvalidOrderSpec {
  val message: String = "invalid order specification"
}
final case class InvalidOrderSpec(node: BaseNode, spec: String) extends ErrorCompilationMessage

object PackageNotFound {
  val message: String = "package not found"
}
final case class PackageNotFound(node: BaseNode) extends ErrorCompilationMessage

object NamedParameterAfterOptionalParameter {
  val message: String = "mandatory parameters must be before optional parameters"
}
final case class NamedParameterAfterOptionalParameter(node: BaseNode) extends ErrorCompilationMessage

object MandatoryArgumentAfterOptionalArgument {
  val message: String = "mandatory arguments must be before optional arguments"
}
final case class MandatoryArgumentAfterOptionalArgument(node: BaseNode) extends ErrorCompilationMessage

object InvalidType {
  val message: String = "invalid type"
}
final case class InvalidType(node: BaseNode) extends ErrorCompilationMessage

object RepeatedFieldNames {
  val message: String = "record has more than one field with the same name"
}
final case class RepeatedFieldNames(node: BaseNode, field: String) extends ErrorCompilationMessage

object FunctionOrMethodExpected {
  val message: String = "function or method expected"
}
final case class FunctionOrMethodExpected(node: BaseNode, t: Type) extends ErrorCompilationMessage

object UnexpectedOptionalArgument {
  val message: String = "found unknown optional argument"
}
final case class UnexpectedOptionalArgument(node: BaseNode) extends ErrorCompilationMessage

object NoOptionalArgumentsExpected {
  val message: String = "no optional arguments expected"
}
final case class NoOptionalArgumentsExpected(node: BaseNode) extends ErrorCompilationMessage

object ExpectedTypeButGotExpression {
  val message: String = "expected type but got expression"
}
final case class ExpectedTypeButGotExpression(node: BaseNode) extends ErrorCompilationMessage

object UnexpectedArguments {
  val message: String = "too many arguments found"
}
final case class UnexpectedArguments(node: BaseNode) extends ErrorCompilationMessage

object FailedToEvaluate {
  val message: String = "failed to evaluate"
}
final case class FailedToEvaluate(node: BaseNode, optionalMessage: Option[String] = None) extends ErrorCompilationMessage

object MandatoryArgumentsMissing {
  val message: String = "missing mandatory arguments"
}
final case class MandatoryArgumentsMissing(node: BaseNode, argsMissing: Seq[String]) extends ErrorCompilationMessage
