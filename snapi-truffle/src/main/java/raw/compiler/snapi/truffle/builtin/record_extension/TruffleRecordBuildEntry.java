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

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.RecordBuildEntry;
import raw.compiler.rql2.source.Rql2RecordType;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.record.RecordBuildNode;

public class TruffleRecordBuildEntry extends RecordBuildEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2RecordType recordType = (Rql2RecordType) type;
    ExpressionNode[] keyTypePairs = new ExpressionNode[recordType.atts().size() * 2];

    for (int i = 0; i < recordType.atts().size(); i++) {
      keyTypePairs[i * 2] = new StringNode(recordType.atts().apply(i).idn());
      keyTypePairs[i * 2 + 1] = args.get(i).exprNode();
    }

    return new RecordBuildNode(keyTypePairs);
  }
}
