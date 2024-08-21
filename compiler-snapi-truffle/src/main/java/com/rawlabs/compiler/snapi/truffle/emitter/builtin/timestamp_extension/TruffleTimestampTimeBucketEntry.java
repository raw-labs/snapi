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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.timestamp_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.TimestampTimeBucketEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2IntervalType;
import com.rawlabs.compiler.snapi.rql2.source.Rql2StringType;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketIntervalNodeGen;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketStringNodeGen;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;

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
