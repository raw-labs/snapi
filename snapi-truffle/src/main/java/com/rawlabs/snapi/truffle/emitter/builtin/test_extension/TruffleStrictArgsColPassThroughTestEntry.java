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
import com.rawlabs.snapi.frontend.rql2.extensions.Rql2Arg;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.StrictArgsColPassThroughTestEntry;
import com.rawlabs.snapi.truffle.emitter.TruffleEmitter;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.binary.MultNodeGen;
import com.rawlabs.snapi.truffle.ast.expressions.iterable.collection.CollectionTransformNodeGen;
import com.rawlabs.snapi.truffle.ast.expressions.literals.IntNode;
import com.rawlabs.snapi.truffle.ast.local.ReadParamNode;
import java.util.List;

public class TruffleStrictArgsColPassThroughTestEntry extends StrictArgsColPassThroughTestEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    return CollectionTransformNodeGen.create(
        emitter.recurseExp(args.get(0).e()),
        emitter.recurseLambda(() -> MultNodeGen.create(new ReadParamNode(0), new IntNode("10"))));
  }
}
