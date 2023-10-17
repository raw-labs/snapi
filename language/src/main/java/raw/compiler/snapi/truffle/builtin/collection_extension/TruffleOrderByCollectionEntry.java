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
import java.util.concurrent.atomic.AtomicInteger;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.OrderByCollectionEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionOrderByNode;

public class TruffleOrderByCollectionEntry extends OrderByCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    AtomicInteger index = new AtomicInteger();

    index.set(0);
    ExpressionNode[] keyFunctions =
        args.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 0)
            .map(a -> args.get(a + 1).exprNode())
            .toArray(ExpressionNode[]::new);

    index.set(0);
    Rql2TypeWithProperties[] keyTypes =
        args.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 0)
            .map(a -> (Rql2TypeWithProperties) ((FunType) args.get(a + 1).type()).r())
            .toArray(Rql2TypeWithProperties[]::new);

    index.set(0);
    ExpressionNode[] orderings =
        args.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 1)
            .map(a -> args.get(a + 1).exprNode())
            .toArray(ExpressionNode[]::new);
    Rql2TypeWithProperties valueType =
        (Rql2TypeWithProperties) ((Rql2IterableType) args.get(0).type()).innerType();

    return new CollectionOrderByNode(
        args.get(0).exprNode(), keyFunctions, orderings, keyTypes, valueType);
  }
}
