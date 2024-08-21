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

package raw.compiler.snapi.truffle.builtin.time_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.common.source.Exp;
import com.rawlabs.compiler.snapi.rql2.builtin.TimeSubtractIntervalEntry;
import java.util.List;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.temporals.time_package.TimeSubtractIntervalNodeGen;
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
