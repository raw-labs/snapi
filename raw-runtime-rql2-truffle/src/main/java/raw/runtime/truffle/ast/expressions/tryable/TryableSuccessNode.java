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

package raw.runtime.truffle.ast.expressions.tryable;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.option.DoubleOption;
import raw.runtime.truffle.runtime.option.IntOption;
import raw.runtime.truffle.runtime.option.ObjectOption;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.tryable.*;

@NodeInfo(shortName = "Tryable.Success")
@NodeChild("value")
public abstract class TryableSuccessNode extends ExpressionNode {

  @Specialization(guards = "options.isOption(option)", limit = "1")
  protected Object createOptionObject(
      Object option, @CachedLibrary("option") OptionLibrary options) {
    return ObjectTryable.BuildSuccess(option);
  }

  @Specialization
  protected Object createBoolean(boolean value) {
    return BooleanTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createByte(byte value) {
    return ByteTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createShort(short value) {
    return ShortTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createInt(int value) {
    return IntTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createLong(long value) {
    return LongTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createFloat(float value) {
    return FloatTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createDouble(double value) {
    return DoubleTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createString(String value) {
    return StringTryable.BuildSuccess(value);
  }

  @Specialization
  protected Object createObject(Object value) {
    return ObjectTryable.BuildSuccess(value);
  }
}
