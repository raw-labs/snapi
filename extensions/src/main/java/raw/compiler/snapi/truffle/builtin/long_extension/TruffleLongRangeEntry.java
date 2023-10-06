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

package raw.compiler.snapi.truffle.builtin.long_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LocationLlEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.numeric.long_package.LongRangeNodeGen;
import raw.runtime.truffle.ast.expressions.literals.LongNode;

public class TruffleLongRangeEntry extends LocationLlEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode start = args.get(0).getExprNode();
    ExpressionNode end = args.get(1).getExprNode();
    ExpressionNode step =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("step"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new LongNode("1"));
    return LongRangeNodeGen.create(start, end, step);
  }
}
