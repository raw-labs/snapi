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

package raw.compiler.snapi.truffle.builtin.timestamp_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.TimestampRangeEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.temporals.interval_package.IntervalBuildNodeGen;
import raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package.TimestampRangeNodeGen;
import raw.runtime.truffle.ast.expressions.literals.IntNode;

public class TruffleTimestampRangeEntry extends TimestampRangeEntry
    implements TruffleEntryExtension {

  private final ExpressionNode defaultStep =
      IntervalBuildNodeGen.create(
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("1"),
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("0"),
          new IntNode("0"));

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode start = args.get(0).exprNode();
    ExpressionNode end = args.get(1).exprNode();
    ExpressionNode step =
        args.stream()
            .filter(arg -> arg.identifier() != null && arg.identifier().contains("step"))
            .findFirst()
            .map(TruffleArg::exprNode)
            .orElseGet(() -> defaultStep);
    return TimestampRangeNodeGen.create(start, end, step);
  }
}
