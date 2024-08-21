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

import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import java.util.List;
import java.util.Optional;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.ExpressionNode;

public interface WithArgs {
  default Optional<ExpressionNode> arg(List<TruffleArg> args, String kw) {
    return args.stream()
        .filter(a -> a.identifier() != null && a.identifier().contains(kw))
        .map(TruffleArg::exprNode)
        .findFirst();
  }

  default ExpressionNode[] mandatoryArgs(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.identifier() == null)
        .map(TruffleArg::exprNode)
        .toArray(ExpressionNode[]::new);
  }

  default ExpressionNode[] optionalArgs(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.identifier() != null)
        .map(TruffleArg::exprNode)
        .toArray(ExpressionNode[]::new);
  }

  default Rql2TypeWithProperties[] optionalArgsTypes(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.identifier() != null)
        .map(a -> (Rql2TypeWithProperties) a.type())
        .toArray(Rql2TypeWithProperties[]::new);
  }
}
