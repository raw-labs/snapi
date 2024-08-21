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
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.api.Rql2Arg;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

public interface TruffleEntryExtension {
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    throw new UnsupportedOperationException("Not implemented");
  }

  default ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    return toTruffle(type, rql2argsToTruffleArgs(args, emitter), emitter.getLanguage());
  }

  default List<TruffleArg> rql2argsToTruffleArgs(List<Rql2Arg> args, TruffleEmitter emitter) {
    return args.stream()
        .map(
            a ->
                new TruffleArg(
                    emitter.recurseExp(a.e()), a.t(), a.idn().isDefined() ? a.idn().get() : null))
        .collect(Collectors.toList());
  }
}
