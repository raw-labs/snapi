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

package raw.compiler.snapi.truffle.builtin.collection_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.InternalEquiJoinCollectionEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionEquiJoinNode;

public class TruffleInternalEquiJoinCollectionEntry extends InternalEquiJoinCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    TruffleArg left = args.get(0);
    TruffleArg right = args.get(1);
    TruffleArg leftK = args.get(2);
    TruffleArg rightK = args.get(3);
    TruffleArg remap = args.get(4);

    FunType funType = (FunType) leftK.getType();
    Rql2IterableType leftValue = (Rql2IterableType) left.getType();
    Rql2IterableType rightValue = (Rql2IterableType) right.getType();

    return new CollectionEquiJoinNode(
        left.getExprNode(),
        right.getExprNode(),
        leftK.getExprNode(),
        rightK.getExprNode(),
        (Rql2TypeWithProperties) funType.r(),
        (Rql2TypeWithProperties) leftValue.innerType(),
        (Rql2TypeWithProperties) rightValue.innerType(),
        remap.getExprNode());
  }
}
