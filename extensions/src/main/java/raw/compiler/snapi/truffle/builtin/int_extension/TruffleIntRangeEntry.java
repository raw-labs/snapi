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

package raw.compiler.snapi.truffle.builtin.int_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.IntRangeEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.numeric.int_package.IntRangeNodeGen;
import raw.runtime.truffle.ast.expressions.literals.IntNode;

public class TruffleIntRangeEntry extends IntRangeEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {

    ExpressionNode start = args.get(0).getExprNode();
    ExpressionNode end = args.get(1).getExprNode();
    ExpressionNode step =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().equals("step"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode("1"));

    return IntRangeNodeGen.create(start, end, step);
  }
}
