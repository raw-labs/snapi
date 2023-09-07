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
import raw.compiler.rql2._
import raw.compiler.rql2.builtin.FlatMapNullableTryableEntry
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleEmitter, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.function.InvokeNode
import raw.runtime.truffle.ast.expressions.option._
import raw.runtime.truffle.ast.local.ReadParamNode
import raw.runtime.truffle.ast.expressions.tryable.{TryableFlatMapNodeGen, TryableSuccessNodeGen}
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
      // Case #1. The value is a try+nullable, but only the try needs to be checked (nullable is expected).
      // And the function returns a try+null (likely it is try because of the extra try in the argument).
      // That's kind of a tryable flatMap
      case (
            eTypeWithProps: Rql2TypeWithProperties,
            inTypeWithProps: Rql2TypeWithProperties,
            outTypeWithProps: Rql2TypeWithProperties
          )
          if eTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) &&
            inTypeWithProps.props == Set(Rql2IsNullableTypeProperty()) &&
            outTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) =>
        TryableFlatMapNodeGen.create(
          emitter.recurseExp(args(0).e),
          emitter.recurseLambda(() =>
            new InvokeNode(emitter.recurseExp(args(1).e), Array(null), Array(new ReadParamNode(0)))
          )
        )

      // Case #2. The value is try+nullable, and both properties need to be checked before applying the function.
      // And the function returns a try+nullable. That's kind of a regular flatMap on a tryable+nullable.
      case (eTypeWithProps: Rql2TypeWithProperties, inType, outTypeWithProps: Rql2TypeWithProperties)
          if eTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) &&
            outTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) =>
        TryableFlatMapNodeGen.create(
          emitter.recurseExp(args(0).e),
          emitter.recurseLambda(() =>
            OptionGetOrElseNodeGen.create(
              OptionMapNodeGen.create(
                new ReadParamNode(0),
                //                wait; this does not evaluate to a function..
                emitter.recurseLambda(() =>
                  new InvokeNode(emitter.recurseExp(args(1).e), Array(null), Array(new ReadParamNode(0)))
                )
              ),
              TryableSuccessNodeGen.create(new OptionNoneNode(outType))
            )
          )
        )

      // Case #3. No coverage for that one?
      case (
            eTypeWithProps: Rql2TypeWithProperties,
            inTypeWithProps: Rql2TypeWithProperties,
            outTypeWithProps: Rql2TypeWithProperties
          )
          if eTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) &&
            inTypeWithProps.props == Set(Rql2IsNullableTypeProperty()) &&
            outTypeWithProps.props.contains(Rql2IsNullableTypeProperty()) =>
        // Not implemented yet!
        ???

      // Case #4. The value is a nullable, it's like an option.flatMap BUT the output is a try+nullable.
      // If division is applied to a nullable, that would do that
      case (eTypeWithProps: Rql2TypeWithProperties, inType, outTypeWithProps: Rql2TypeWithProperties)
          if eTypeWithProps.props == Set(Rql2IsNullableTypeProperty()) &&
            outTypeWithProps.props == Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty()) =>
        OptionGetOrElseNodeGen.create(
          OptionMapNodeGen.create(
            emitter.recurseExp(args(0).e),
            emitter.recurseLambda(() =>
              new InvokeNode(emitter.recurseExp(args(1).e), Array(null), Array(new ReadParamNode(0)))
            )
          ),
          TryableSuccessNodeGen.create(new OptionNoneNode(outType))
        )

      // Case #5 Pure tryable
      case (eTypeWithProps: Rql2TypeWithProperties, inType, outTypeWithProps: Rql2TypeWithProperties)
          if eTypeWithProps.props.contains(Rql2IsTryableTypeProperty()) &&
            outTypeWithProps.props.contains(Rql2IsTryableTypeProperty()) =>
        TryableFlatMapNodeGen.create(
          emitter.recurseExp(args(0).e),
          emitter.recurseExp(args(1).e)
        )

      // Case #6 pure option
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
