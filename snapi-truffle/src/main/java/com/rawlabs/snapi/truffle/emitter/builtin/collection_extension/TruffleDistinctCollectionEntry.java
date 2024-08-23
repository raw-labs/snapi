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
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.DistinctCollectionEntry;
import com.rawlabs.snapi.frontend.rql2.source.Rql2IterableType;
import com.rawlabs.snapi.frontend.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionDistinctNodeGen;
import java.util.List;

public class TruffleDistinctCollectionEntry extends DistinctCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();

    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");

    int offHeapDistinctSlot =
        builder.addSlot(
            FrameSlotKind.Object, "offHeapDistinct", "a slot to store the offHeapDistinct of osr");

    return CollectionDistinctNodeGen.create(
        truffleArgs.get(0).exprNode(),
        (Rql2TypeWithProperties) ((Rql2IterableType) type).innerType(),
        generatorSlot,
        offHeapDistinctSlot);
  }
}
