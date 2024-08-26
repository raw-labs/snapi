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

package com.rawlabs.snapi.truffle.emitter.builtin;

import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import java.util.List;
import java.util.Optional;

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

  default SnapiTypeWithProperties[] optionalArgsTypes(List<TruffleArg> args) {
    return args.stream()
        .filter(a -> a.identifier() != null)
        .map(a -> (SnapiTypeWithProperties) a.type())
        .toArray(SnapiTypeWithProperties[]::new);
  }
}
