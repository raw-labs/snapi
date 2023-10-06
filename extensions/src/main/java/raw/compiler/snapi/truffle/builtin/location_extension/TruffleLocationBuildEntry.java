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

package raw.compiler.snapi.truffle.builtin.location_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LocationBuildEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

public class TruffleLocationBuildEntry extends LocationBuildEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    String[] keys =
        args.stream()
            .filter(a -> a.getIdentifier() != null)
            .map(a -> a.getIdentifier().replace('_', '-'))
            .toArray(String[]::new);

    String[] values =
        args.stream()
            .filter(a -> a.getIdentifier() != null)
            .map(a -> a.getIdentifier().replace('_', '-'))
            .toArray(String[]::new);

    return TruffleEntryExtension.super.toTruffle(type, args, rawLanguage);
  }
}
