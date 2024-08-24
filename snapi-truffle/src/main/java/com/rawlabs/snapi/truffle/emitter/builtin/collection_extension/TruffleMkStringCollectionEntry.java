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
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.MkStringCollectionEntry;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionMkStringNode;
import com.rawlabs.snapi.truffle.ast.expressions.literals.StringNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;

public class TruffleMkStringCollectionEntry extends MkStringCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<SnapiArg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();
    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int sepSlot =
        builder.addSlot(FrameSlotKind.Object, "separator", "a slot to store the separator of osr");
    int resultSlot =
        builder.addSlot(FrameSlotKind.Boolean, "result", "a slot to store the result of osr");

    ExpressionNode start =
        truffleArgs.stream()
            .filter(a -> a.identifier() != null && a.identifier().contains("start"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new StringNode(""));

    ExpressionNode sep =
        truffleArgs.stream()
            .filter(a -> a.identifier() != null && a.identifier().contains("sep"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new StringNode(""));

    ExpressionNode end =
        truffleArgs.stream()
            .filter(a -> a.identifier() != null && a.identifier().contains("end"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new StringNode(""));

    return new CollectionMkStringNode(
        truffleArgs.get(0).exprNode(), start, sep, end, generatorSlot, sepSlot, resultSlot);
  }
}
