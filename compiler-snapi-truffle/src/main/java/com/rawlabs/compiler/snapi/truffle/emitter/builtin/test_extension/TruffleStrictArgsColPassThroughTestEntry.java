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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.test_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.api.Rql2Arg;
import com.rawlabs.compiler.snapi.rql2.builtin.StrictArgsColPassThroughTestEntry;
import java.util.List;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.binary.MultNodeGen;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.iterable.collection.CollectionTransformNodeGen;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.literals.IntNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.local.ReadParamNode;

public class TruffleStrictArgsColPassThroughTestEntry extends StrictArgsColPassThroughTestEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    return CollectionTransformNodeGen.create(
        emitter.recurseExp(args.get(0).e()),
        emitter.recurseLambda(() -> MultNodeGen.create(new ReadParamNode(0), new IntNode("10"))));
  }
}
