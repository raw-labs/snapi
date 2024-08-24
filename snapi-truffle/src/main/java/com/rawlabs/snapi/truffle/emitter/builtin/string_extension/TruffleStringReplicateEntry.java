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

package com.rawlabs.snapi.truffle.emitter.builtin.string_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.StringReplicateEntry;
import com.rawlabs.snapi.frontend.snapi.source.Exp;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.string_package.StringReplicateNodeGen;
import com.rawlabs.snapi.truffle.emitter.TruffleShortEntryExtension;
import java.util.List;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleStringReplicateEntry extends StringReplicateEntry
    implements TruffleShortEntryExtension {

  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return StringReplicateNodeGen.create(args.get(0), args.get(1));
  }
}
