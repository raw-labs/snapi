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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.collection_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.api.Rql2Arg;
import com.rawlabs.compiler.snapi.rql2.builtin.FilterCollectionEntry;
import java.util.List;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.collection.CollectionFilterNode;

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
