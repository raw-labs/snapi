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

package com.rawlabs.snapi.truffle.emitter.builtin.record_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.RecordRemoveFieldEntry;
import com.rawlabs.snapi.frontend.rql2.source.SnapiAttrType;
import com.rawlabs.snapi.frontend.rql2.source.SnapiRecordType;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.literals.StringNode;
import com.rawlabs.snapi.truffle.ast.expressions.record.RecordRemoveFieldNodeGen;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.Arrays;
import java.util.List;
import scala.collection.JavaConverters;

public class TruffleRecordRemoveFieldEntry extends RecordRemoveFieldEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    SnapiRecordType recordType = (SnapiRecordType) type;
    List<String> finalFieldNames =
        JavaConverters.asJavaCollection(recordType.atts()).stream()
            .map(a -> (SnapiAttrType) a)
            .map(SnapiAttrType::idn)
            .distinct()
            .toList();
    SnapiRecordType original = (SnapiRecordType) args.get(0).type();
    String[] originalFieldNames =
        JavaConverters.asJavaCollection(original.atts()).stream()
            .map(a -> (SnapiAttrType) a)
            .map(SnapiAttrType::idn)
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
