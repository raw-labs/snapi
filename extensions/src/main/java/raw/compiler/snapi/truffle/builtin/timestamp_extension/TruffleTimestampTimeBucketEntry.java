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

package raw.compiler.snapi.truffle.builtin.timestamp_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.TimestampTimeBucketEntry;
import raw.compiler.rql2.source.Rql2IntervalType;
import raw.compiler.rql2.source.Rql2StringType;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ExpressionNode;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketIntervalNodeGen;
import raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketStringNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

import java.util.List;

public class TruffleTimestampTimeBucketEntry extends TimestampTimeBucketEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {

    return switch (args.get(0).getType()) {
      case Rql2StringType ignored ->
          TimestampTimeBucketStringNodeGen.create(args.get(0).getExprNode(), args.get(1).getExprNode());
      case Rql2IntervalType ignored ->
          TimestampTimeBucketIntervalNodeGen.create(args.get(0).getExprNode(), args.get(1).getExprNode());
      default -> throw new RawTruffleInternalErrorException();
    };
  }
}
