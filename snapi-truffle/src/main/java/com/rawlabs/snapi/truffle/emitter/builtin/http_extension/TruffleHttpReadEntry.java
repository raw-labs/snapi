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

package com.rawlabs.snapi.truffle.emitter.builtin.http_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.HttpReadEntry;
import com.rawlabs.snapi.frontend.snapi.source.Exp;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.http_package.HttpReadNodeGen;
import com.rawlabs.snapi.truffle.emitter.TruffleShortEntryExtension;
import java.util.List;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleHttpReadEntry extends HttpReadEntry implements TruffleShortEntryExtension {
  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return HttpReadNodeGen.create(args.get(0), args.get(1));
  }
}
