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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.location_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.LocationDescribeEntry;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.location_package.LocationDescribeNodeGen;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.literals.IntNode;
import java.util.List;

public class TruffleLocationDescribeEntry extends LocationDescribeEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode sampleSize =
        args.stream()
            .filter(a -> a.identifier() != null && a.identifier().contains("sampleSize"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new IntNode(Integer.toString(Integer.MIN_VALUE)));
    return LocationDescribeNodeGen.create(args.get(0).exprNode(), sampleSize);
  }
}
