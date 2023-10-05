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

package raw.compiler.snapi.truffle.builtin.json_extension;

import java.util.List;
import java.util.Optional;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;

public interface WithJsonArgs {
  default Optional<ExpressionNode> arg(List<TruffleArg> args, String kw) {
    return args.stream()
        .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains(kw))
        .map(TruffleArg::getExprNode)
        .findFirst();
  }

  default ExpressionNode[] unnamedArgs(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.getIdentifier() == null)
        .map(TruffleArg::getExprNode)
        .toArray(ExpressionNode[]::new);
  }

  default ExpressionNode encoding(List<TruffleArg> args) {
    return arg(args, "encoding").orElse(new StringNode("utf-8"));
  }

  default ExpressionNode timeFormat(List<TruffleArg> args) {
    return arg(args, "timeFormat").orElse(new StringNode("HH:mm[:ss[.SSS]]"));
  }

  default ExpressionNode dateFormat(List<TruffleArg> args) {
    return arg(args, "dateFormat").orElse(new StringNode("yyyy-M-d"));
  }

  default ExpressionNode timestampFormat(List<TruffleArg> args) {
    return arg(args, "timestampFormat").orElse(new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]"));
  }
}
