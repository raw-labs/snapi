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

package raw.compiler.snapi.truffle.builtin.math_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.Exp;
import raw.compiler.rql2.builtin.MathPowerEntry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.math_package.MathPowerNodeGen;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleMathPowerEntry extends MathPowerEntry implements TruffleShortEntryExtension {
  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return MathPowerNodeGen.create(args.get(0), args.get(1));
  }
}
