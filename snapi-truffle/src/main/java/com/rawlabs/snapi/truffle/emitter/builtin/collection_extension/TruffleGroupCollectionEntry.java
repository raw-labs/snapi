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
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.GroupCollectionEntry;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionGroupByNodeGen;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.Arrays;
import java.util.List;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashSet;

public class TruffleGroupCollectionEntry extends GroupCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<SnapiArg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = snapiargsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();

    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int functionSlot =
        builder.addSlot(FrameSlotKind.Object, "function", "a slot to store the function of osr");
    int mapSlot = builder.addSlot(FrameSlotKind.Object, "map", "a slot to store the map of osr");

    SnapiIterableType iterable = (SnapiIterableType) type;
    SnapiRecordType record = (SnapiRecordType) iterable.innerType();
    SnapiAttrType[] atts =
        JavaConverters.asJavaCollection(record.atts()).stream()
            .map(a -> (SnapiAttrType) a)
            .toArray(SnapiAttrType[]::new);

    SnapiTypeWithProperties keyType =
        (SnapiTypeWithProperties)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("key"))
                .findFirst()
                .orElse(SnapiAttrType.apply("key", new SnapiUndefinedType(new HashSet<>())))
                .tipe();

    SnapiIterableType iterableValueType =
        (SnapiIterableType)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("group"))
                .findFirst()
                .orElse(SnapiAttrType.apply("key", new SnapiUndefinedType(new HashSet<>())))
                .tipe();

    SnapiTypeWithProperties valueType = (SnapiTypeWithProperties) iterableValueType.innerType();

    return CollectionGroupByNodeGen.create(
        truffleArgs.get(0).exprNode(),
        truffleArgs.get(1).exprNode(),
        keyType,
        valueType,
        generatorSlot,
        functionSlot,
        mapSlot);
  }
}
