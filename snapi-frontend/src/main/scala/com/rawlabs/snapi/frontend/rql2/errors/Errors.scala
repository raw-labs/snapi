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

package com.rawlabs.snapi.frontend.rql2.errors

import com.rawlabs.snapi.frontend.base.errors.ErrorCompilerMessage
import com.rawlabs.snapi.frontend.base.source.{BaseNode, Type}

object KeyNotComparable {
  val message: String = "key is not comparable"
  val code = "keyNotComparable"
}
final case class KeyNotComparable(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = KeyNotComparable.code
}

object ItemsNotComparable {
  val message: String = "items are not comparable"
  val code = "itemsNotComparable"
}
final case class ItemsNotComparable(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = ItemsNotComparable.code
}

object CannotDetermineTypeOfParameter {
  val message: String = "cannot determine type of parameter"
  val code = "cannotDetermineTypeOfParameter"
}
final case class CannotDetermineTypeOfParameter(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = CannotDetermineTypeOfParameter.code
}

object OutputTypeRequiredForRecursiveFunction {
  val message: String = "output type required for recursive function"
  val code = "outputTypeRequiredForRecursiveFunction"
}
final case class OutputTypeRequiredForRecursiveFunction(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = OutputTypeRequiredForRecursiveFunction.code
}

object RepeatedOptionalArguments {
  val message: String = "repeated optional arguments"
  val code = "repeatedOptionalArguments"
}
final case class RepeatedOptionalArguments(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = RepeatedOptionalArguments.code
}

object OrderSpecMustFollowOrderingFunction {
  val message: String = "order specification must follow each ordering key function"
  val code = "orderSpecMustFollowOrderingFunction"
}
final case class OrderSpecMustFollowOrderingFunction(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = OrderSpecMustFollowOrderingFunction.code
}

object InvalidOrderSpec {
  val message: String = "invalid order specification"
  val code = "invalidOrderSpec"
}
final case class InvalidOrderSpec(node: BaseNode, spec: String) extends ErrorCompilerMessage {
  val code: String = InvalidOrderSpec.code
}

object PackageNotFound {
  val message: String = "package not found"
  val code = "packageNotFound"
}
final case class PackageNotFound(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = PackageNotFound.code
}

object NamedParameterAfterOptionalParameter {
  val message: String = "mandatory parameters must be before optional parameters"
  val code = "namedParameterAfterOptionalParameter"
}
final case class NamedParameterAfterOptionalParameter(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = NamedParameterAfterOptionalParameter.code
}

object MandatoryArgumentAfterOptionalArgument {
  val message: String = "mandatory arguments must be before optional arguments"
  val code = "mandatoryArgumentAfterOptionalArgument"
}
final case class MandatoryArgumentAfterOptionalArgument(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = MandatoryArgumentAfterOptionalArgument.code
}

object InvalidType {
  val message: String = "invalid type"
  val code = "invalidType"
}
final case class InvalidType(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = InvalidType.code
}

object RepeatedFieldNames {
  val message: String = "record has more than one field with the same name"
  val code = "repeatedFieldNames"
}
final case class RepeatedFieldNames(node: BaseNode, field: String) extends ErrorCompilerMessage {
  val code: String = RepeatedFieldNames.code
}

object FunctionOrMethodExpected {
  val message: String = "function or method expected"
  val code = "functionOrMethodExpected"
}
final case class FunctionOrMethodExpected(node: BaseNode, t: Type) extends ErrorCompilerMessage {
  val code: String = FunctionOrMethodExpected.code
}

object UnexpectedOptionalArgument {
  val message: String = "found unknown optional argument"
  val code = "unexpectedOptionalArgument"
}
final case class UnexpectedOptionalArgument(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = UnexpectedOptionalArgument.code
}

object NoOptionalArgumentsExpected {
  val message: String = "no optional arguments expected"
  val code = "noOptionalArgumentsExpected"
}
final case class NoOptionalArgumentsExpected(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = NoOptionalArgumentsExpected.code
}

object ExpectedTypeButGotExpression {
  val message: String = "expected type but got expression"
  val code = "expectedTypeButGotExpression"
}
final case class ExpectedTypeButGotExpression(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = ExpectedTypeButGotExpression.code
}

object UnexpectedArguments {
  val message: String = "too many arguments found"
  val code = "unexpectedArguments"
}
final case class UnexpectedArguments(node: BaseNode) extends ErrorCompilerMessage {
  val code: String = UnexpectedArguments.code
}

object FailedToEvaluate {
  val message: String = "failed to evaluate"
  val code = "failedToEvaluate"
}
final case class FailedToEvaluate(node: BaseNode, optionalMessage: Option[String] = None) extends ErrorCompilerMessage {
  val code: String = FailedToEvaluate.code
}

object MandatoryArgumentsMissing {
  val message: String = "missing mandatory arguments"
  val code = "mandatoryArgumentsMissing"
}
final case class MandatoryArgumentsMissing(node: BaseNode, argsMissing: Seq[String]) extends ErrorCompilerMessage {
  val code: String = MandatoryArgumentsMissing.code
}
