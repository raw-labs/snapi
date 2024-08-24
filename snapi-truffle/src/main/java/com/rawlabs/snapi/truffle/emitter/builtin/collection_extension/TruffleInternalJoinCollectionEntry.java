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
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.InternalJoinCollectionEntry;
import com.rawlabs.snapi.frontend.rql2.source.FunType;
import com.rawlabs.snapi.frontend.rql2.source.SnapiIterableType;
import com.rawlabs.snapi.frontend.rql2.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionJoinNodeGen;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;

public class TruffleInternalJoinCollectionEntry extends InternalJoinCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<SnapiArg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();

    int computeNextSlot =
        builder.addSlot(
            FrameSlotKind.Object, "computeNext", "a slot to store the computeNext of osr");
    int shouldContinueSlot =
        builder.addSlot(
            FrameSlotKind.Boolean, "shouldContinue", "a slot to store the shouldContinue of osr");
    int resultSlot =
        builder.addSlot(FrameSlotKind.Object, "result", "a slot to store the result of osr");
    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int outputBufferSlot =
        builder.addSlot(
            FrameSlotKind.Object, "outputBuffer", "a slot to store the outputBuffer of osr");

    TruffleArg left = truffleArgs.get(0);
    TruffleArg right = truffleArgs.get(1);
    TruffleArg reshape = truffleArgs.get(2);
    TruffleArg predicate = truffleArgs.get(3);
    SnapiIterableType rql2IterableType = (SnapiIterableType) right.type();
    SnapiTypeWithProperties rightType = (SnapiTypeWithProperties) rql2IterableType.innerType();
    boolean reshapeBeforePredicate = ((FunType) predicate.type()).ms().size() == 1;
    return CollectionJoinNodeGen.create(
        left.exprNode(),
        right.exprNode(),
        reshape.exprNode(),
        predicate.exprNode(),
        rightType,
        reshapeBeforePredicate,
        computeNextSlot,
        shouldContinueSlot,
        resultSlot,
        generatorSlot,
        outputBufferSlot);
  }
}
