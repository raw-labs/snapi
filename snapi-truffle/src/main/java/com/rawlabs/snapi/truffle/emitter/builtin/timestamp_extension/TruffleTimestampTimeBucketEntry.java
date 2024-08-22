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

package com.rawlabs.snapi.truffle.emitter.builtin.timestamp_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.builtin.TimestampTimeBucketEntry;
import com.rawlabs.snapi.frontend.rql2.source.Rql2IntervalType;
import com.rawlabs.snapi.frontend.rql2.source.Rql2StringType;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketIntervalNodeGen;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketStringNodeGen;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;

import java.util.List;

public class TruffleTimestampTimeBucketEntry extends TimestampTimeBucketEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {

    return switch (args.get(0).type()) {
      case Rql2StringType ignored ->
          TimestampTimeBucketStringNodeGen.create(args.get(0).exprNode(), args.get(1).exprNode());
      case Rql2IntervalType ignored ->
          TimestampTimeBucketIntervalNodeGen.create(args.get(0).exprNode(), args.get(1).exprNode());
      default -> throw new RawTruffleInternalErrorException();
    };
  }
}
