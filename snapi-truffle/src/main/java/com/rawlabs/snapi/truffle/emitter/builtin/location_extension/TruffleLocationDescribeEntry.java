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

package com.rawlabs.snapi.truffle.emitter.builtin.location_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.LocationDescribeEntry;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package.LocationDescribeNodeGen;
import com.rawlabs.snapi.truffle.ast.expressions.literals.IntNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import java.util.List;

public class TruffleLocationDescribeEntry extends LocationDescribeEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, Rql2Language rawLanguage) {
    ExpressionNode sampleSize =
        args.stream()
            .filter(a -> a.identifier() != null && a.identifier().contains("sampleSize"))
            .map(TruffleArg::exprNode)
            .findFirst()
            .orElse(new IntNode(Integer.toString(Integer.MIN_VALUE)));
    return LocationDescribeNodeGen.create(args.get(0).exprNode(), sampleSize);
  }
}
