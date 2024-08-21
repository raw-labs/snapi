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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.time_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.common.source.Exp;
import com.rawlabs.compiler.snapi.rql2.builtin.TimeNowEntry;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleShortEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.temporals.time_package.TimeNowNodeGen;
import java.util.List;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleTimeNowEntry extends TimeNowEntry implements TruffleShortEntryExtension {

  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return TimeNowNodeGen.create();
  }
}
