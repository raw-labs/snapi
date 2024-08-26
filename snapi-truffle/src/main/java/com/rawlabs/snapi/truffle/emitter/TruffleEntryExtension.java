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

package com.rawlabs.snapi.truffle.emitter;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.SnapiArg;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import java.util.List;
import java.util.stream.Collectors;

public interface TruffleEntryExtension {
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    throw new UnsupportedOperationException("Not implemented");
  }

  default ExpressionNode toTruffle(Type type, List<SnapiArg> args, TruffleEmitter emitter) {
    return toTruffle(type, snapiargsToTruffleArgs(args, emitter), emitter.getLanguage());
  }

  default List<TruffleArg> snapiargsToTruffleArgs(List<SnapiArg> args, TruffleEmitter emitter) {
    return args.stream()
        .map(
            a ->
                new TruffleArg(
                    emitter.recurseExp(a.e()), a.t(), a.idn().isDefined() ? a.idn().get() : null))
        .collect(Collectors.toList());
  }
}
