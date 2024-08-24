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
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.RecordBuildEntry;
import com.rawlabs.snapi.frontend.snapi.source.SnapiAttrType;
import com.rawlabs.snapi.frontend.snapi.source.SnapiRecordType;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.record.RecordBuildNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;
import scala.collection.JavaConverters;

public class TruffleRecordBuildEntry extends RecordBuildEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    SnapiRecordType recordType = (SnapiRecordType) type;

    String[] fieldNames =
        JavaConverters.asJavaCollection(recordType.atts()).stream()
            .map(a -> (SnapiAttrType) a)
            .map(SnapiAttrType::idn)
            .toList()
            .toArray(new String[0]);

    ExpressionNode[] values =
        args.stream().map(a -> ((TruffleArg) a).exprNode()).toArray(ExpressionNode[]::new);

    return new RecordBuildNode(values, fieldNames);
  }
}
