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

import java.util.Arrays;
import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.GroupListEntry;
import raw.compiler.rql2.source.*;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListGroupByNodeGen;
import scala.collection.JavaConverters;
import scala.collection.immutable.HashSet;

public class TruffleGroupListEntry extends GroupListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
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
    return ListGroupByNodeGen.create(
        args.get(0).getExprNode(), args.get(1).getExprNode(), keyType, (Rql2TypeWithProperties)groupType.innerType());
  }
}
