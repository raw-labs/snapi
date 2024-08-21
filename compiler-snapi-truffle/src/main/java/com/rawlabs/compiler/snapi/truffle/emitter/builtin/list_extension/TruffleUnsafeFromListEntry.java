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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.list_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.api.Rql2Arg;
import com.rawlabs.compiler.snapi.rql2.builtin.UnsafeFromListEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2ListType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2Type;
import java.util.List;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.list.ListFromUnsafe;

public class TruffleUnsafeFromListEntry extends UnsafeFromListEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    Rql2ListType rql2ListType = (Rql2ListType) type;
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();
    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int listSlot =
        builder.addSlot(FrameSlotKind.Object, "filterList", "a slot to store the ArrayList of osr");
    int currentIdxSlot =
        builder.addSlot(
            FrameSlotKind.Int, "currentIdxSlot", "a slot to store the current index of osr");
    int listSizeSlot =
        builder.addSlot(
            FrameSlotKind.Int, "listSize", "a slot to store the size of the list of osr");
    int resultSlot =
        builder.addSlot(FrameSlotKind.Object, "list", "a slot to store the result array of osr");
    return new ListFromUnsafe(
        truffleArgs.get(0).exprNode(),
        (Rql2Type) rql2ListType.innerType(),
        generatorSlot,
        listSlot,
        currentIdxSlot,
        listSizeSlot,
        resultSlot);
  }
}
