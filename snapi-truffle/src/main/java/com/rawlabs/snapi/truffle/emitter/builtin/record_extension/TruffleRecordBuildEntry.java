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

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.RecordBuildEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2AttrType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2RecordType;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.record.RecordBuildNode;
import java.util.List;
import scala.collection.JavaConverters;

public class TruffleRecordBuildEntry extends RecordBuildEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2RecordType recordType = (Rql2RecordType) type;

    String[] fieldNames =
        JavaConverters.asJavaCollection(recordType.atts()).stream()
            .map(a -> (Rql2AttrType) a)
            .map(Rql2AttrType::idn)
            .toList()
            .toArray(new String[0]);

    ExpressionNode[] values =
        args.stream().map(a -> ((TruffleArg) a).exprNode()).toArray(ExpressionNode[]::new);

    return new RecordBuildNode(values, fieldNames);
  }
}
