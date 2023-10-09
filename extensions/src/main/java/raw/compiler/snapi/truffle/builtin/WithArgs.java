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

package raw.compiler.snapi.truffle.builtin;

import java.util.List;
import java.util.Optional;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.ExpressionNode;

public interface WithArgs {
  default Optional<ExpressionNode> arg(List<TruffleArg> args, String kw) {
    return args.stream()
        .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains(kw))
        .map(TruffleArg::getExprNode)
        .findFirst();
  }

  default ExpressionNode[] mandatoryArgs(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.getIdentifier() == null)
        .map(TruffleArg::getExprNode)
        .toArray(ExpressionNode[]::new);
  }

  default ExpressionNode[] optionalArgs(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.getIdentifier() != null)
        .map(TruffleArg::getExprNode)
        .toArray(ExpressionNode[]::new);
  }

  default Rql2TypeWithProperties[] optionalArgsTypes(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.getIdentifier() != null)
        .map(a -> (Rql2TypeWithProperties) a.getType())
        .toArray(Rql2TypeWithProperties[]::new);
  }
}
