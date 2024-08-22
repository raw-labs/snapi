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

package com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.TimestampRangeEntry;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.interval_package.IntervalBuildNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package.TimestampRangeNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.literals.IntNode;
import java.util.List;

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
