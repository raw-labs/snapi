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
import com.rawlabs.snapi.frontend.rql2.extensions.Rql2Arg;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.FilterCollectionEntry;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionFilterNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;

public class TruffleFilterCollectionEntry extends FilterCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();
    int collectionSlot =
        builder.addSlot(
            FrameSlotKind.Object, "collection", "a slot to store the collection of osr");
    int predicateSlot =
        builder.addSlot(FrameSlotKind.Object, "predicate", "a slot to store the predicate of osr");
    int resultSlot =
        builder.addSlot(FrameSlotKind.Object, "result", "a slot to store the result of osr");

    return new CollectionFilterNode(
        truffleArgs.get(0).exprNode(),
        truffleArgs.get(1).exprNode(),
        collectionSlot,
        predicateSlot,
        resultSlot);
  }
}
