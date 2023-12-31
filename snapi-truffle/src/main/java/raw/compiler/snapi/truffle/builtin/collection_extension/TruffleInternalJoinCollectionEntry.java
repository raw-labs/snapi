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
import raw.compiler.rql2.builtin.InternalJoinCollectionEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionJoinNodeGen;

public class TruffleInternalJoinCollectionEntry extends InternalJoinCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    TruffleArg left = args.get(0);
    TruffleArg right = args.get(1);
    TruffleArg reshape = args.get(2);
    TruffleArg predicate = args.get(3);
    Rql2IterableType rql2IterableType = (Rql2IterableType) right.type();
    Rql2TypeWithProperties rightType = (Rql2TypeWithProperties) rql2IterableType.innerType();
    boolean reshapeBeforePredicate = ((FunType) predicate.type()).ms().size() == 1;
    return CollectionJoinNodeGen.create(
        left.exprNode(),
        right.exprNode(),
        reshape.exprNode(),
        predicate.exprNode(),
        rightType,
        reshapeBeforePredicate);
  }
}
