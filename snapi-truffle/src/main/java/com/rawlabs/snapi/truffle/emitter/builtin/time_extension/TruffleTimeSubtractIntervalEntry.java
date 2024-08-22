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

package com.rawlabs.snapi.truffle.emitter.builtin.time_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.common.source.Exp;
import com.rawlabs.snapi.frontend.rql2.builtin.TimeSubtractIntervalEntry;
import com.rawlabs.snapi.truffle.emitter.TruffleShortEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.temporals.time_package.TimeSubtractIntervalNodeGen;
import java.util.List;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleTimeSubtractIntervalEntry extends TimeSubtractIntervalEntry
    implements TruffleShortEntryExtension {

  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return TimeSubtractIntervalNodeGen.create(args.get(0), args.get(1));
  }
}
