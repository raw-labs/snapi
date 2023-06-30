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

import raw.compiler.base.errors.UnexpectedType
import raw.compiler.base.source.BaseNode
import raw.compiler.{common, rql2}
import raw.compiler.rql2.source.{ExpectedProjType, PackageType}

trait ErrorsPrettyPrinter extends common.errors.ErrorsPrettyPrinter with rql2.source.SourcePrettyPrinter {

  override def toDoc(n: BaseNode): Doc = n match {
    case _: KeyNotComparable => KeyNotComparable.message
    case _: ItemsNotComparable => ItemsNotComparable.message
    case _: CannotDetermineTypeOfParameter => CannotDetermineTypeOfParameter.message
    case _: OutputTypeRequiredForRecursiveFunction => OutputTypeRequiredForRecursiveFunction.message
    case InvalidOrderSpec(_, spec) => InvalidOrderSpec.message <> colon <+> spec
    case _: PackageNotFound => PackageNotFound.message
    case _: NamedParameterAfterOptionalParameter => NamedParameterAfterOptionalParameter.message
    case _: InvalidType => InvalidType.message
    case _: MandatoryArgumentAfterOptionalArgument => MandatoryArgumentAfterOptionalArgument.message
    case _: UnexpectedOptionalArgument => UnexpectedOptionalArgument.message
    case FunctionOrMethodExpected(_, t) => t match {
        case _: PackageType => FunctionOrMethodExpected.message <+> "but got package"
        case _ => FunctionOrMethodExpected.message <+> "but got" <+> t
      }
    case RepeatedFieldNames(_, name) => RepeatedFieldNames.message <> colon <+> name
    case _: NoOptionalArgumentsExpected => NoOptionalArgumentsExpected.message
    case _: ExpectedTypeButGotExpression => ExpectedTypeButGotExpression.message
    case _: UnexpectedArguments => UnexpectedArguments.message
    case _: RepeatedOptionalArguments => RepeatedOptionalArguments.message
    case FailedToEvaluate(_, optionalMessage) => optionalMessage match {
        case Some(message) => FailedToEvaluate.message <> colon <+> message
        case None => FailedToEvaluate.message
      }
    case MandatoryArgumentsMissing(_, argsMissing) =>
      handleHintsAndSuggestions(MandatoryArgumentsMissing.message, None, argsMissing)
    case UnexpectedType(_, PackageType(pkgName), ExpectedProjType(ent), hints, suggestions) =>
      val message =
        if (pkgName.contains("$")) {
          // Hide package names of dynamic packages
          ent <+> "is not declared in package"
        } else {
          ent <+> "is not declared in package" <+> pkgName
        }
      handleHintsAndSuggestions(message, hints, suggestions)
    case UnexpectedType(_, PackageType(pkgName), ExpectedProjType(ent), hints, suggestions) =>
      val message =
        if (pkgName.contains("$")) {
          // Hide package names of dynamic packages
          ent <+> "is not declared in package"
        } else {
          ent <+> "is not declared in package" <+> pkgName
        }
      handleHintsAndSuggestions(message, hints, suggestions)
    case _ => super.toDoc(n)
  }

}

object ErrorsPrettyPrinter extends ErrorsPrettyPrinter
