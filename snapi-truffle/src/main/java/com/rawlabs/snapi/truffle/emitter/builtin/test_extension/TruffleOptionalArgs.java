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
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.emitter.builtin.WithArgs;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.Rql2Language;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.binary.MultNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.literals.IntNode;
import java.util.List;

public interface TruffleOptionalArgs extends TruffleEntryExtension, WithArgs {
  @Override
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, Rql2Language rawLanguage) {
    ExpressionNode mandatory = mandatoryArgs(args)[0];
    ExpressionNode x = arg(args, "x").orElse(new IntNode("10"));
    ExpressionNode y = arg(args, "y").orElse(new IntNode("10"));
    return MultNodeGen.create(mandatory, MultNodeGen.create(x, y));
  }
}
