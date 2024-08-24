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

package com.rawlabs.snapi.truffle.emitter.builtin.list_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.SnapiArg;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.TransformListEntry;
import com.rawlabs.snapi.frontend.snapi.source.FunType;
import com.rawlabs.snapi.frontend.snapi.source.SnapiType;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.list.ListTransformNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;

public class TruffleTransformListEntry extends TransformListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<SnapiArg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = snapiargsToTruffleArgs(args, emitter);
    FunType funType = (FunType) truffleArgs.get(1).type();
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();
    int listSlot = builder.addSlot(FrameSlotKind.Object, "list", "a slot to store the list of osr");
    int functionSlot =
        builder.addSlot(FrameSlotKind.Object, "function", "a slot to store the function of osr");
    int currentIdxSlot =
        builder.addSlot(
            FrameSlotKind.Int, "currentIdxSlot", "a slot to store the current index of osr");
    int listSizeSlot =
        builder.addSlot(
            FrameSlotKind.Int, "listSize", "a slot to store the size of the list of osr");
    int resultSlot =
        builder.addSlot(
            FrameSlotKind.Object, "list", "a slot to store the result list internal array of osr");

    return new ListTransformNode(
        truffleArgs.get(0).exprNode(),
        truffleArgs.get(1).exprNode(),
        (SnapiType) funType.r(),
        listSlot,
        functionSlot,
        currentIdxSlot,
        listSizeSlot,
        resultSlot);
  }
}
