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

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.builtin.OrderByCollectionEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionOrderByNode;

public class TruffleOrderByCollectionEntry extends OrderByCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();

    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int collectionSlot =
        builder.addSlot(
            FrameSlotKind.Object, "collection", "a slot to store the collection of osr");
    int offHeapGroupByKeysSlot =
        builder.addSlot(
            FrameSlotKind.Object,
            "offHeapGroupByKeys",
            "a slot to store the offHeapGroupByKeys of osr");

    AtomicInteger index = new AtomicInteger();

    index.set(0);
    ExpressionNode[] keyFunctions =
        truffleArgs.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 0)
            .map(a -> truffleArgs.get(a + 1).exprNode())
            .toArray(ExpressionNode[]::new);

    index.set(0);
    Rql2TypeWithProperties[] keyTypes =
        truffleArgs.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 0)
            .map(a -> (Rql2TypeWithProperties) ((FunType) truffleArgs.get(a + 1).type()).r())
            .toArray(Rql2TypeWithProperties[]::new);

    index.set(0);
    ExpressionNode[] orderings =
        truffleArgs.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 1)
            .map(a -> truffleArgs.get(a + 1).exprNode())
            .toArray(ExpressionNode[]::new);
    Rql2TypeWithProperties valueType =
        (Rql2TypeWithProperties) ((Rql2IterableType) truffleArgs.get(0).type()).innerType();

    return new CollectionOrderByNode(
        truffleArgs.get(0).exprNode(),
        keyFunctions,
        orderings,
        keyTypes,
        valueType,
        generatorSlot,
        collectionSlot,
        offHeapGroupByKeysSlot);
  }
}