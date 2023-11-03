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

package raw.runtime.truffle.ast.expressions.binary;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ast.BinaryNode;
import raw.runtime.truffle.runtime.primitives.DecimalObject;

@NodeInfo(shortName = "-")
public abstract class SubNode extends BinaryNode {

  @Specialization
  protected byte sub(byte left, byte right) {
    return (byte) (left - right);
  }

  @Specialization
  protected short sub(short left, short right) {
    return (short) (left - right);
  }

  @Specialization
  protected int sub(int left, int right) {
    return left - right;
  }

  @Specialization
  protected long sub(long left, long right) {
    return left - right;
  }

  @Specialization
  protected float sub(float left, float right) {
    return left - right;
  }

  @Specialization
  protected double sub(double left, double right) {
    return left - right;
  }

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected DecimalObject sub(DecimalObject left, DecimalObject right) {
    return new DecimalObject(left.getBigDecimal().subtract(right.getBigDecimal()));
  }
}
