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
import raw.compiler.rql2.source._
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.{ExpressionNode, RawLanguage}
import raw.runtime.truffle.ast.expressions.iterable.collection.{
  CollectionBuildNode,
  CollectionCountNodeGen,
  CollectionDistinctNodeGen,
  CollectionEquiJoinNode,
  CollectionExistsNodeGen,
  CollectionFilterNodeGen,
  CollectionFirstNodeGen,
  CollectionFromNodeGen,
  CollectionGroupByNodeGen,
  CollectionJoinNodeGen,
  CollectionLastNodeGen,
  CollectionMaxNodeGen,
  CollectionMinNodeGen,
  CollectionMkStringNodeGen,
  CollectionOrderByNode,
  CollectionSumNodeGen,
  CollectionTakeNodeGen,
  CollectionTransformNodeGen,
  CollectionTupleAvgNodeGen,
  CollectionUnionNode,
  CollectionUnnestNodeGen,
  CollectionZipNodeGen
}
import raw.runtime.truffle.ast.expressions.literals.StringNode

class TruffleEmptyCollectionEntry extends EmptyCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    new CollectionBuildNode(Array.empty);
  }

}

class TruffleBuildCollectionEntry extends BuildCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    new CollectionBuildNode(args.map(_.e).toArray)
  }

}

class TruffleFilterCollectionEntry extends FilterCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionFilterNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleOrderByCollectionEntry extends OrderByCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val keyFunctions = args.tail.zipWithIndex.collect { case (arg, i) if i % 2 == 0 => arg.e }
    val keyTypes = args.tail.zipWithIndex.collect {
      case (arg, i) if i % 2 == 0 => arg.t.asInstanceOf[FunType].r.asInstanceOf[Rql2TypeWithProperties]
    }
    val orderings = args.tail.zipWithIndex.collect { case (arg, i) if i % 2 == 1 => arg.e }
    val Rql2IterableType(valueType: Rql2TypeWithProperties, _) = args.head.t
    new CollectionOrderByNode(args(0).e, keyFunctions.toArray, orderings.toArray, keyTypes.toArray, valueType)
  }

}

class TruffleTransformCollectionEntry extends TransformCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionTransformNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleDistinctCollectionEntry extends DistinctCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val Rql2IterableType(itemType: Rql2TypeWithProperties, _) = t
    CollectionDistinctNodeGen.create(args(0).e, itemType)
  }
}

class TruffleCountCollectionEntry extends CountCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionCountNodeGen.create(args(0).e)
  }

}

class TruffleTupleAvgCollectionEntry extends TupleAvgCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionTupleAvgNodeGen.create(args(0).e)
  }

}

class TruffleMinCollectionEntry extends MinCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionMinNodeGen.create(args(0).e)
  }

}

class TruffleMaxCollectionEntry extends MaxCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionMaxNodeGen.create(args(0).e)
  }

}

class TruffleSumCollectionEntry extends SumCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionSumNodeGen.create(args(0).e)
  }

}

class TruffleFirstCollectionEntry extends FirstCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionFirstNodeGen.create(args(0).e)
  }

}

class TruffleLastCollectionEntry extends LastCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionLastNodeGen.create(args(0).e)
  }
}

class TruffleTakeCollectionEntry extends TakeCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionTakeNodeGen.create(args(0).e, args(1).e)
  }
}

class TruffleUnnestCollectionEntry extends UnnestCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionUnnestNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleFromCollectionEntry extends FromCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionFromNodeGen.create(args(0).e)
  }
}

class TruffleGroupCollectionEntry extends GroupCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val Rql2IterableType(
      Rql2RecordType(
        Vector(
          Rql2AttrType("key", keyType: Rql2TypeWithProperties),
          Rql2AttrType("group", Rql2IterableType(valueType: Rql2TypeWithProperties, _))
        ),
        _
      ),
      _
    ) = t
    CollectionGroupByNodeGen.create(args(0).e, args(1).e, keyType, valueType)
  }

}

class TruffleInternalJoinCollectionEntry extends InternalJoinCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val left = args(0)
    val right = args(1)
    val reshape = args(2)
    val predicate = args(3)
    val Rql2IterableType(rightType: Rql2TypeWithProperties, _) = right.t
    val reshapeBeforePredicate = {
      val FunType(predicateParams, _, _, _) = predicate.t
      predicateParams.size == 1
    }
    CollectionJoinNodeGen.create(left.e, right.e, reshape.e, predicate.e, rightType, reshapeBeforePredicate)
  }

}

class TruffleInternalEquiJoinCollectionEntry extends InternalEquiJoinCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val left = args(0).e
    val right = args(1).e
    val leftK = args(2).e
    val rightK = args(3).e
    val remap = args(4).e
    val FunType(_, _, kType: Rql2TypeWithProperties, _) = args(2).t
    val Rql2IterableType(leftValueType: Rql2TypeWithProperties, _) = args(0).t
    val Rql2IterableType(rightValueType: Rql2TypeWithProperties, _) = args(1).t
    new CollectionEquiJoinNode(left, right, leftK, rightK, kType, leftValueType, rightValueType, remap)
  }

}

class TruffleUnionCollectionEntry extends UnionCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode =
    new CollectionUnionNode(args.map(_.e).toArray);
}

class TruffleExistsCollectionEntry extends ExistsCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val FunType(_, _, rType, _) = args(1).t
    CollectionExistsNodeGen.create(
      args(0).e,
      args(1).e,
      rType.asInstanceOf[Rql2TypeWithProperties]
    )
  }

}

class TruffleZipCollectionEntry extends ZipCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    CollectionZipNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleMkStringCollectionEntry extends MkStringCollectionEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg], rawLanguage: RawLanguage): ExpressionNode = {
    val start = args.collectFirst { case arg if arg.idn.contains("start") => arg.e }.getOrElse(new StringNode(""))
    val sep = args.collectFirst { case arg if arg.idn.contains("sep") => arg.e }.getOrElse(new StringNode(""))
    val end = args.collectFirst { case arg if arg.idn.contains("end") => arg.e }.getOrElse(new StringNode(""))
    CollectionMkStringNodeGen.create(args(0).e, start, sep, end)
  }

}
