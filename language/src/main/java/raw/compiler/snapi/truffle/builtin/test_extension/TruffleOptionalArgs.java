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

package raw.compiler.snapi.truffle.builtin.test_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.MultNodeGen;
import raw.runtime.truffle.ast.expressions.literals.IntNode;

public interface TruffleOptionalArgs extends TruffleEntryExtension, WithArgs {
  @Override
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode mandatory = mandatoryArgs(args)[0];
    ExpressionNode x = arg(args, "x").orElse(new IntNode("10"));
    ExpressionNode y = arg(args, "y").orElse(new IntNode("10"));
    return MultNodeGen.create(mandatory, MultNodeGen.create(x, y));
  }
}
