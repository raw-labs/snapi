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

package raw.compiler.rql2.truffle.builtin

import raw.compiler.base.source.Type
import raw.compiler.rql2.api._
import raw.compiler.rql2.builtin.FlatMapNullableTryableEntry
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleEmitter, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.option._
import raw.runtime.truffle.ast.expressions.tryable.{
  TryableFlatMapNodeGen,
  TryableNullableFlatMapNodeGen,
  TryableSuccessNodeGen
}
import raw.runtime.truffle.ast.expressions.option.{OptionGetOrElseNodeGen, OptionMapNodeGen}

// FlatMapNullableTryableEntry looks like a regular flatMap functionality but it is
// more flexible. Mainly, the object in input can be an error, a nullable or both, and the
// output of the function can also be error, nullable or both.
class TruffleFlatMapNullableTryableEntry extends FlatMapNullableTryableEntry with TruffleEntryExtension {

// nullableTryable.FlatMap(nt => function(nt)) eType -> type of nullableTryable, inType -> type of nt, outType -> type of function(nt)
  override def toTruffle(t: Type, args: Seq[Rql2Arg], emitter: TruffleEmitter): ExpressionNode = {
    val eType = args(0).t
    val FunType(Vector(inType), _, outType, _) = args(1).t
    (eType, inType, outType) match {
      // The value is try+nullable, and both properties need to be checked before applying the function.
      // And the function returns a try+nullable. That's kind of a regular flatMap on a tryable+nullable.
      case (eTypeWithProps: Rql2TypeWithProperties, inType, outTypeWithProps: Rql2TypeWithProperties)
          if eTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) &&
            getProps(inType).isEmpty &&
            outTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) =>
        TryableNullableFlatMapNodeGen.create(
          emitter.recurseExp(args(0).e),
          emitter.recurseExp(args(1).e)
        )

      // The value is a nullable, it's like an option.flatMap BUT the output is a try+nullable.
      // If division is applied to a nullable, that would do that
      case (eTypeWithProps: Rql2TypeWithProperties, inType, outTypeWithProps: Rql2TypeWithProperties)
          if eTypeWithProps.props == Set(Rql2IsNullableTypeProperty()) &&
            outTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) =>
        OptionGetOrElseNodeGen.create(
          OptionMapNodeGen.create(
            emitter.recurseExp(args(0).e),
            emitter.recurseExp(args(1).e)
          ),
          TryableSuccessNodeGen.create(new OptionNoneNode(outType))
        )

      // Pure tryable
      case (eTypeWithProps: Rql2TypeWithProperties, inType, outTypeWithProps: Rql2TypeWithProperties)
          if eTypeWithProps.props.contains(Rql2IsTryableTypeProperty()) &&
            outTypeWithProps.props.contains(Rql2IsTryableTypeProperty()) =>
        TryableFlatMapNodeGen.create(
          emitter.recurseExp(args(0).e),
          emitter.recurseExp(args(1).e)
        )

      // Pure option
      case (eTypeWithProps: Rql2TypeWithProperties, inType, outTypeWithProps: Rql2TypeWithProperties)
          if eTypeWithProps.props == Set(Rql2IsNullableTypeProperty()) &&
            outTypeWithProps.props.contains(Rql2IsNullableTypeProperty()) =>
        OptionFlatMapNodeGen.create(emitter.recurseExp(args(0).e), emitter.recurseExp(args(1).e))
    }
  }

}

//L0.source.OptionGetOrElse(
//    L0.source
//        .OptionMap(args(0).e, L0.source.Param(optIdn, innerType), L0.source.FunApp(args(1).e, IdnExp(optIdn))),
//    L0.source.TryBuildSuccess(L0.source.NoneConst(t))
//)
