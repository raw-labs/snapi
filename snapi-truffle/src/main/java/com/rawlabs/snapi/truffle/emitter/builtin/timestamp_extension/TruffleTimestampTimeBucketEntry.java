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
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.TimestampTimeBucketEntry;
import com.rawlabs.snapi.frontend.snapi.source.SnapiIntervalType;
import com.rawlabs.snapi.frontend.snapi.source.SnapiStringType;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketIntervalNodeGen;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.temporals.timestamp_package.TimestampTimeBucketStringNodeGen;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleInternalErrorException;

import java.util.List;

public class TruffleTimestampTimeBucketEntry extends TimestampTimeBucketEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {

    return switch (args.get(0).type()) {
      case SnapiStringType ignored ->
          TimestampTimeBucketStringNodeGen.create(args.get(0).exprNode(), args.get(1).exprNode());
      case SnapiIntervalType ignored ->
          TimestampTimeBucketIntervalNodeGen.create(args.get(0).exprNode(), args.get(1).exprNode());
      default -> throw new TruffleInternalErrorException();
    };
  }
}
