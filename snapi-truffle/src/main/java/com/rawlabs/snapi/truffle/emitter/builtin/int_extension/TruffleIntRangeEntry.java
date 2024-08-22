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

package com.rawlabs.snapi.truffle.emitter.builtin.int_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.builtin.IntRangeEntry;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.numeric.int_package.IntRangeNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.literals.IntNode;
import java.util.List;

public class TruffleIntRangeEntry extends IntRangeEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {

    ExpressionNode start = args.get(0).exprNode();
    ExpressionNode end = args.get(1).exprNode();
    ExpressionNode step =
        args.stream()
            .filter(a -> a.identifier() != null && a.identifier().equals("step"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new IntNode("1"));

    return IntRangeNodeGen.create(start, end, step);
  }
}
