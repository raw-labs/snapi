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
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.{TruffleArg, TruffleEntryExtension}
import raw.runtime.truffle.ExpressionNode
import raw.runtime.truffle.ast.expressions.iterable.list._

class TruffleEmptyListEntry extends EmptyListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    new ListBuildNode(t, Array.empty);
  }

}

class TruffleBuildListEntry extends BuildListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    new ListBuildNode(t, args.map(_.e).toArray);
  }

}

class TruffleGetListEntry extends GetListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    ListGetNodeGen.create(args(0).e, args(1).e)
  }

}

class TruffleFilterListEntry extends FilterListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val Rql2ListType(innerType, _) = t
    val FunType(_, _, rType, _) = args(1).t
    ListFilterNodeGen.create(
      args(0).e,
      args(1).e,
      innerType.asInstanceOf[Rql2Type],
      rType.asInstanceOf[Rql2Type]
    )
  }

}

class TruffleTransformListEntry extends TransformListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val FunType(_, _, rType, _) = args(1).t
    ListTransformNodeGen.create(args(0).e, args(1).e, rType.asInstanceOf[Rql2Type]);
  }

}

class TruffleTakeListEntry extends TakeListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val Rql2ListType(innerType, _) = t
    ListTakeNodeGen.create(args(0).e, args(1).e, innerType.asInstanceOf[Rql2Type])
  }
}

class TruffleSumListEntry extends SumListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    ListSumNodeGen.create(args(0).e)
  }

}

class TruffleMaxListEntry extends MaxListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    ListMaxNodeGen.create(args(0).e)
  }

}

class TruffleMinListEntry extends MinListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    ListMinNodeGen.create(args(0).e)
  }

}

class TruffleFirstListEntry extends FirstListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    ListFirstNodeGen.create(args(0).e, t.asInstanceOf[Rql2Type])
  }

}

class TruffleLastListEntry extends LastListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    ListLastNodeGen.create(args(0).e, t.asInstanceOf[Rql2Type])
  }

}

class TruffleCountListEntry extends CountListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    ListCountNodeGen.create(args(0).e)
  }

}

class TruffleFromListEntry extends FromListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val Rql2ListType(innerType, _) = t
    ListFromNodeGen.create(args(0).e, innerType.asInstanceOf[Rql2Type]);
  }
}

class TruffleUnsafeFromListEntry extends UnsafeFromListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val Rql2ListType(innerType, _) = t
    ListFromUnsafeNodeGen.create(args(0).e, innerType.asInstanceOf[Rql2Type]);
  }
}

class TruffleGroupListEntry extends GroupListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val Rql2ListType(
      Rql2RecordType(
        Vector(
          Rql2AttrType("key", keyType: Rql2TypeWithProperties),
          Rql2AttrType("group", Rql2ListType(valueType: Rql2TypeWithProperties, _))
        ),
        _
      ),
      _
    ) = t
    ListGroupByNodeGen.create(args(0).e, args(1).e, keyType, valueType)
  }

}

class TruffleExistsListEntry extends ExistsListEntry with TruffleEntryExtension {

  override def toTruffle(t: Type, args: Seq[TruffleArg]): ExpressionNode = {
    val FunType(_, _, rType, _) = args(1).t
    ListExistsNodeGen.create(
      args(0).e,
      args(1).e,
      rType.asInstanceOf[Rql2Type]
    )
  }

}
