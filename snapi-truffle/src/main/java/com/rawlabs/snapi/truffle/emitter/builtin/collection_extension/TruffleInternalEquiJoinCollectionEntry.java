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
import com.rawlabs.snapi.frontend.snapi.extensions.SnapiArg;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.InternalEquiJoinCollectionEntry;
import com.rawlabs.snapi.frontend.snapi.source.FunType;
import com.rawlabs.snapi.frontend.snapi.source.SnapiIterableType;
import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionEquiJoinNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;

public class TruffleInternalEquiJoinCollectionEntry extends InternalEquiJoinCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<SnapiArg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = snapiargsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();
    int computeNextSlot =
        builder.addSlot(
            FrameSlotKind.Object, "computeNext", "a slot to store the computeNext of osr");
    int shouldContinueSlot =
        builder.addSlot(
            FrameSlotKind.Boolean, "shouldContinue", "a slot to store the shouldContinue of osr");
    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int keyFunctionSlot =
        builder.addSlot(
            FrameSlotKind.Object, "keyFunction", "a slot to store the keyFunction of osr");
    int mapSlot = builder.addSlot(FrameSlotKind.Object, "map", "a slot to store the map of osr");

    TruffleArg left = truffleArgs.get(0);
    TruffleArg right = truffleArgs.get(1);
    TruffleArg leftK = truffleArgs.get(2);
    TruffleArg rightK = truffleArgs.get(3);
    TruffleArg remap = truffleArgs.get(4);

    FunType funType = (FunType) leftK.type();
    SnapiIterableType leftValue = (SnapiIterableType) left.type();
    SnapiIterableType rightValue = (SnapiIterableType) right.type();

    return new CollectionEquiJoinNode(
        left.exprNode(),
        right.exprNode(),
        leftK.exprNode(),
        rightK.exprNode(),
        (SnapiTypeWithProperties) funType.r(),
        (SnapiTypeWithProperties) leftValue.innerType(),
        (SnapiTypeWithProperties) rightValue.innerType(),
        remap.exprNode(),
        computeNextSlot,
        shouldContinueSlot,
        generatorSlot,
        keyFunctionSlot,
        mapSlot);
  }
}
