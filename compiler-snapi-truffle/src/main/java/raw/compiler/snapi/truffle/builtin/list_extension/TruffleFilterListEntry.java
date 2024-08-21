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

package raw.compiler.snapi.truffle.builtin.list_extension;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import java.util.List;
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.api.Rql2Arg;
import com.rawlabs.compiler.snapi.rql2.builtin.FilterListEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2ListType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.list.ListFilterNode;

public class TruffleFilterListEntry extends FilterListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    Rql2ListType listType = (Rql2ListType) type;
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();
    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int functionSlot =
        builder.addSlot(FrameSlotKind.Object, "function", "a slot to store the function of osr");
    int listSlot =
        builder.addSlot(FrameSlotKind.Object, "filterList", "a slot to store the ArrayList of osr");
    int currentIdxSlot =
        builder.addSlot(
            FrameSlotKind.Int, "currentIdxSlot", "a slot to store the current index of osr");
    int listSizeSlot =
        builder.addSlot(
            FrameSlotKind.Int, "listSize", "a slot to store the size of the list for osr");
    int resultSlot =
        builder.addSlot(
            FrameSlotKind.Object, "result", "a slot to store the result internal array for osr");
    return new ListFilterNode(
        truffleArgs.get(0).exprNode(),
        truffleArgs.get(1).exprNode(),
        (Rql2Type) listType.innerType(),
        generatorSlot,
        functionSlot,
        listSlot,
        currentIdxSlot,
        listSizeSlot,
        resultSlot);
  }
}
