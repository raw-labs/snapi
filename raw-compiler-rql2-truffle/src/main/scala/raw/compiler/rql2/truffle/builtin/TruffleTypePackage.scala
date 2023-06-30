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
import raw.compiler.common.source.OrType
import raw.compiler.rql2.{ProgramContext, TypesMerger}
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.builtin.type_package.{
  TypeMatchNode,
  TypeProtectCastOptionNode,
  TypeProtectCastTryableNode
}
import raw.runtime.truffle.ast.expressions.literals.ZeroedConstNode

class TruffleTypeCastEntry extends TypeCastEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    args(1).e
  }

}

// Internal node that performs `cast` but catches unexpected nulls/errors hit while processing
// the nested objects. In case of error: replaces the expression by a null/error.
class TruffleTypeProtectCastEntry extends TypeProtectCastEntry with TruffleEntryExtension {

  // cloned from rql2.SemanticAnalyzer
  // TODO (bgaidioz) share the code somewhere?
  private def extraProps(target: Type, source: Type) = {

    def recurse(target: Type, source: Type): Set[Rql2TypeProperty] = (target, source) match {
      case (Rql2ListType(inner1, props1), Rql2ListType(inner2, props2)) => recurse(inner1, inner2) ++ (props2 &~ props1)
      case (Rql2IterableType(_, props1), Rql2IterableType(_, props2)) =>
        // inner types aren't checked because iterables aren't consumed in the moment they're passed to
        // the function. No exception will be raised under ProtectCast regarding an iterable's items.
        props2 &~ props1
      case (Rql2RecordType(atts1, props1), Rql2RecordType(atts2, props2)) =>
        val tipes1 = atts1.map(_.tipe)
        val tipes2 = atts2.map(_.tipe)
        assert(tipes1.size == tipes2.size)
        tipes1.zip(tipes2).flatMap { case (t1, t2) => recurse(t1, t2) }.toSet ++ (props2 &~ props1)
      case (t1: Rql2TypeWithProperties, t2: Rql2TypeWithProperties) => t2.props &~ t1.props
    }
    recurse(target, source)
  }

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val ExpType(sourceType) = args(0).t
    val ExpType(targetType) = args(1).t

    val e = args(2).e

    val eProps = extraProps(targetType, sourceType)
    if (eProps == Set(Rql2IsTryableTypeProperty())) new TypeProtectCastTryableNode(e)
    else if (eProps == Set(Rql2IsNullableTypeProperty())) new TypeProtectCastOptionNode(e)
    else new TypeProtectCastTryableNode(new TypeProtectCastOptionNode(e))
  }

}

class TruffleTypeEmptyEntry extends TypeEmptyEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    new ZeroedConstNode(args(0).t.asInstanceOf[Rql2Type])
  }

}

class TruffleTypeMatchEntry extends TypeMatchEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val Rql2OrType(options, _) = args.head.t.asInstanceOf[Rql2OrType]
    val typesMerger = new TypesMerger
    val handlers = args.tail // or-type pattern matching functions
      .map { arg =>
        val paramType = arg.t.asInstanceOf[FunType].ms.head // first (and only) parameter type
        val idx = options.indexWhere(typesMerger.propertyCompatible(_, paramType)) // where is that type in the or-type?
        (idx, arg)
      }
      .sortBy(_._1) // reorder items by index
      .map(_._2.e) // extract 'e' (the function)
      .toArray
    new TypeMatchNode(args.head.e, handlers)
  }

}
