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

package com.rawlabs.snapi.truffle.emitter.builtin.test_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.StrictArgsTestEntry;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.binary.PlusNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.numeric.float_package.FloatFromNodeGen;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.list.ListCountNodeGen;
import com.rawlabs.snapi.truffle.ast.expressions.literals.FloatNode;
import com.rawlabs.snapi.truffle.ast.expressions.literals.LongNode;
import com.rawlabs.snapi.truffle.ast.expressions.literals.StringNode;
import com.rawlabs.snapi.truffle.ast.expressions.record.RecordBuildNode;
import com.rawlabs.snapi.truffle.ast.expressions.record.RecordProjNodeGen;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.emitter.builtin.WithArgs;
import java.util.List;

public class TruffleStrictArgsTestEntry extends StrictArgsTestEntry
    implements TruffleEntryExtension, WithArgs {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    ExpressionNode listArg = args.get(0).exprNode();
    ExpressionNode[] optionalArgs = optionalArgs(args);
    ExpressionNode recordArg =
        arg(args, "r")
            .orElse(
                new RecordBuildNode(
                    new ExpressionNode[] {new LongNode("0"), new FloatNode("0")},
                    new String[] {"a", "b"}));
    return new PlusNode(
        FloatFromNodeGen.create(
            new PlusNode(
                ListCountNodeGen.create(listArg),
                RecordProjNodeGen.create(recordArg, new StringNode("a")))),
        RecordProjNodeGen.create(recordArg, new StringNode("b")));
  }
}
