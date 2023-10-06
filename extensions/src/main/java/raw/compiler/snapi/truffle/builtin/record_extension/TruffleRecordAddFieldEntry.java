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
import raw.compiler.rql2.builtin.RecordAddFieldEntry;
import raw.compiler.rql2.source.Rql2RecordType;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.record.RecordAddFieldNodeGen;

public class TruffleRecordAddFieldEntry extends RecordAddFieldEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2RecordType recordType = (Rql2RecordType) type;
    String f = recordType.atts().last().idn();
    StringNode fieldName = new StringNode(f);
    ExpressionNode value = args.get(1).getExprNode();
    return RecordAddFieldNodeGen.create(args.get(0).getExprNode(), fieldName, value);
  }
}
