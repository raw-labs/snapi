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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.tryable;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;

@NodeInfo(shortName = "Tryable.Success")
@NodeChild("value")
public abstract class TryableSuccessNode extends ExpressionNode {

  @Specialization
  protected Object createBoolean(boolean value) {
    return value;
  }

  @Specialization
  protected Object createByte(byte value) {
    return value;
  }

  @Specialization
  protected Object createShort(short value) {
    return value;
  }

  @Specialization
  protected Object createInt(int value) {
    return value;
  }

  @Specialization
  protected Object createLong(long value) {
    return value;
  }

  @Specialization
  protected Object createFloat(float value) {
    return value;
  }

  @Specialization
  protected Object createDouble(double value) {
    return value;
  }

  @Specialization
  protected Object createString(String value) {
    return value;
  }

  @Specialization
  protected Object createObject(Object value) {
    return value;
  }
}
