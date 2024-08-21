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
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.api.Rql2Arg;
import com.rawlabs.compiler.snapi.rql2.builtin.InternalEquiJoinCollectionEntry;
import com.rawlabs.compiler.snapi.rql2.source.FunType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2IterableType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import java.util.List;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionEquiJoinNode;

public class TruffleInternalEquiJoinCollectionEntry extends InternalEquiJoinCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
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
    Rql2IterableType leftValue = (Rql2IterableType) left.type();
    Rql2IterableType rightValue = (Rql2IterableType) right.type();

    return new CollectionEquiJoinNode(
        left.exprNode(),
        right.exprNode(),
        leftK.exprNode(),
        rightK.exprNode(),
        (Rql2TypeWithProperties) funType.r(),
        (Rql2TypeWithProperties) leftValue.innerType(),
        (Rql2TypeWithProperties) rightValue.innerType(),
        remap.exprNode(),
        computeNextSlot,
        shouldContinueSlot,
        generatorSlot,
        keyFunctionSlot,
        mapSlot);
  }
}
