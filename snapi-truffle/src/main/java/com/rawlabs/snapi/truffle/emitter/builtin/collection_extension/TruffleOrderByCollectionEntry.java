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

package com.rawlabs.snapi.truffle.emitter.builtin.collection_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.SnapiArg;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.OrderByCollectionEntry;
import com.rawlabs.snapi.frontend.rql2.source.FunType;
import com.rawlabs.snapi.frontend.rql2.source.SnapiIterableType;
import com.rawlabs.snapi.frontend.rql2.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionOrderByNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TruffleOrderByCollectionEntry extends OrderByCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<SnapiArg> args, TruffleEmitter emitter) {
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
    SnapiTypeWithProperties[] keyTypes =
        truffleArgs.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 0)
            .map(a -> (SnapiTypeWithProperties) ((FunType) truffleArgs.get(a + 1).type()).r())
            .toArray(SnapiTypeWithProperties[]::new);

    index.set(0);
    ExpressionNode[] orderings =
        truffleArgs.stream()
            .skip(1)
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 1)
            .map(a -> truffleArgs.get(a + 1).exprNode())
            .toArray(ExpressionNode[]::new);
    SnapiTypeWithProperties valueType =
        (SnapiTypeWithProperties) ((SnapiIterableType) truffleArgs.get(0).type()).innerType();

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
