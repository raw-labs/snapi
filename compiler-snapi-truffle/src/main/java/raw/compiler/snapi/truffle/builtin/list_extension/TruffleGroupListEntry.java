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
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.api.Rql2Arg;
import com.rawlabs.compiler.snapi.rql2.builtin.GroupListEntry;
import com.rawlabs.compiler.snapi.rql2.source.*;
import java.util.Arrays;
import java.util.List;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.iterable.list.ListGroupByNode;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashSet;

public class TruffleGroupListEntry extends GroupListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {

    List<TruffleArg> truffleArgs = rql2argsToTruffleArgs(args, emitter);

    Rql2ListType listType = (Rql2ListType) type;
    Rql2RecordType record = (Rql2RecordType) listType.innerType();
    Rql2AttrType[] atts =
        JavaConverters.asJavaCollection(record.atts()).toArray(Rql2AttrType[]::new);

    Rql2TypeWithProperties keyType =
        (Rql2TypeWithProperties)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("key"))
                .findFirst()
                .orElse(Rql2AttrType.apply("key", new Rql2UndefinedType(new HashSet<>())))
                .tipe();

    Rql2ListType groupType =
        (Rql2ListType)
            Arrays.stream(atts)
                .filter(a -> a.idn().equals("group"))
                .findFirst()
                .orElse(Rql2AttrType.apply("key", new Rql2UndefinedType(new HashSet<>())))
                .tipe();

    FrameDescriptor.Builder builder = emitter.getFrameDescriptorBuilder();
    int generatorSlot =
        builder.addSlot(FrameSlotKind.Object, "generator", "a slot to store the generator of osr");
    int keyFuncSlot =
        builder.addSlot(
            FrameSlotKind.Object, "keyFunction", "a slot to store the key function of osr");
    int mapSlot =
        builder.addSlot(FrameSlotKind.Object, "mapSlot", "a slot to store the map of osr");
    int listSlot =
        builder.addSlot(FrameSlotKind.Object, "listSize", "a slot to store the list of osr");

    return new ListGroupByNode(
        truffleArgs.get(0).exprNode(),
        truffleArgs.get(1).exprNode(),
        (Rql2TypeWithProperties) groupType.innerType(),
        keyType,
        generatorSlot,
        keyFuncSlot,
        mapSlot,
        listSlot);
  }
}
