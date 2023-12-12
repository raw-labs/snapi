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

package raw.runtime.truffle.ast.expressions.option;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import raw.runtime.truffle.ExpressionNode;

@NodeChild("value")
public abstract class OptionSomeNode extends ExpressionNode {

  @Specialization
  protected Object someByte(byte value) {
    return value;
  }

  @Specialization
  protected Object someShort(short value) {
    return value;
  }

  @Specialization
  protected Object someInt(int value) {
    return value;
  }

  @Specialization
  protected Object someLong(long value) {
    return value;
  }

  @Specialization
  protected Object someFloat(float value) {
    return value;
  }

  @Specialization
  protected Object someDouble(double value) {
    return value;
  }

  @Specialization
  protected Object someBoolean(boolean value) {
    return value;
  }

  @Specialization
  protected Object someString(String value) {
    return value;
  }

  @Specialization
  protected Object someOption(Object value) {
    return value;
  }
}
