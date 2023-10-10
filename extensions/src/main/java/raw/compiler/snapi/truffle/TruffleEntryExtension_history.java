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
package raw.compiler.snapi.truffle;

import java.util.List;
import java.util.stream.Collectors;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.truffle.TruffleEmitter;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

public interface TruffleEntryExtension_history {
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    throw new UnsupportedOperationException("Not implemented");
  }

  default ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    return toTruffle(
        type,
        args.stream()
            .map(
                a ->
                    new TruffleArg(
                        emitter.recurseExp(a.e()),
                        a.t(),
                        a.idn().isDefined() ? a.idn().get() : null))
            .collect(Collectors.toList()),
        emitter.rawLanguage());
  }
}
