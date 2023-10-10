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

package raw.compiler.snapi.truffle.builtin.record_extension;

import java.util.Arrays;
import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.RecordRemoveFieldEntry;
import raw.compiler.rql2.source.Rql2AttrType;
import raw.compiler.rql2.source.Rql2RecordType;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.record.RecordRemoveFieldNodeGen;
import scala.collection.JavaConverters;

public class TruffleRecordRemoveFieldEntry extends RecordRemoveFieldEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2RecordType recordType = (Rql2RecordType) type;
    List<String> finalFieldNames =
        JavaConverters.asJavaCollection(recordType.atts()).stream()
            .map(a -> (Rql2AttrType) a)
            .map(Rql2AttrType::idn)
            .distinct()
            .toList();
    Rql2RecordType original = (Rql2RecordType) args.get(0).type();
    String[] originalFieldNames =
        JavaConverters.asJavaCollection(original.atts()).stream()
            .map(a -> (Rql2AttrType) a)
            .map(Rql2AttrType::idn)
            .distinct()
            .toArray(String[]::new);
    String f =
        Arrays.stream(originalFieldNames)
            .filter(a -> !finalFieldNames.contains(a))
            .findFirst()
            .orElseThrow();
    return RecordRemoveFieldNodeGen.create(args.get(0).exprNode(), new StringNode(f));
  }
}
