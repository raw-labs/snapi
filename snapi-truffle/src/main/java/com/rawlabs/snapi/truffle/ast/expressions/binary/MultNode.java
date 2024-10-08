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

package com.rawlabs.snapi.truffle.ast.expressions.binary;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.BinaryNode;
import com.rawlabs.snapi.truffle.runtime.primitives.DecimalObject;

@NodeInfo(shortName = "*")
public abstract class MultNode extends BinaryNode {

  @Specialization
  protected byte mul(byte left, byte right) {
    return (byte) (left * right);
  }

  @Specialization
  protected short mul(short left, short right) {
    return (short) (left * right);
  }

  @Specialization
  protected int mul(int left, int right) {
    return left * right;
  }

  @Specialization
  protected long mul(long left, long right) {
    return left * right;
  }

  @Specialization
  protected float mul(float left, float right) {
    return left * right;
  }

  @Specialization
  protected double mul(double left, double right) {
    return left * right;
  }

  @Specialization
  @TruffleBoundary
  protected DecimalObject mul(DecimalObject left, DecimalObject right) {
    return new DecimalObject(left.getBigDecimal().multiply(right.getBigDecimal()));
  }
}
