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
import com.rawlabs.snapi.frontend.rql2.builtin.GroupCollectionEntry;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.collection.CollectionGroupByNodeGen;
import java.util.Arrays;
import java.util.List;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashSet;

public class TruffleGroupCollectionEntry extends GroupCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);
    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();

    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int functionSlot =
        builder.addSlot(FrameSlotKind.Object, "function", "a slot to store the function of osr");
    int mapSlot = builder.addSlot(FrameSlotKind.Object, "map", "a slot to store the map of osr");

    Rql2IterableType iterable = (Rql2IterableType) type;
    Rql2RecordType record = (Rql2RecordType) iterable.innerType();
    Rql2AttrType[] atts =
        JavaConverters.asJavaCollection(record.atts()).stream()
            .map(a -> (Rql2AttrType) a)
            .toArray(Rql2AttrType[]::new);

    Rql2TypeWithProperties keyType =
        (Rql2TypeWithProperties)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("key"))
                .findFirst()
                .orElse(Rql2AttrType.apply("key", new Rql2UndefinedType(new HashSet<>())))
                .tipe();

    Rql2IterableType iterableValueType =
        (Rql2IterableType)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("group"))
                .findFirst()
                .orElse(Rql2AttrType.apply("key", new Rql2UndefinedType(new HashSet<>())))
                .tipe();

    Rql2TypeWithProperties valueType = (Rql2TypeWithProperties) iterableValueType.innerType();

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
