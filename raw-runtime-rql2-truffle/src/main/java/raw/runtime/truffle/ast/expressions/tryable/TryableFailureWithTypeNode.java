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
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.tryable.*;

@NodeInfo(shortName = "Tryable.FailureWithType")
@NodeChild("zeroedValue")
@NodeChild("message")
public abstract class TryableFailureWithTypeNode extends ExpressionNode {

  @Specialization
  protected Object tryableFailureBoolean(boolean zeroedValue, String message) {
    return BooleanTryable.BuildFailure(message);
  }

  @Specialization
  protected Object tryableFailureByte(byte zeroedValue, String message) {
    return ByteTryable.BuildFailure(message);
  }

  @Specialization
  protected Object tryableFailureShort(String message, short zeroedValue) {
    return ShortTryable.BuildFailure(message);
  }

  @Specialization
  protected Object tryableFailureInt(int zeroedValue, String message) {
    return IntTryable.BuildFailure(message);
  }

  @Specialization
  protected Object tryableFailureLong(long zeroedValue, String message) {
    return LongTryable.BuildFailure(message);
  }

  @Specialization
  protected Object tryableFailureString(String zeroedValue, String message) {
    return StringTryable.BuildFailure(message);
  }

  @Specialization
  protected Object tryableFailureDecimal(BigDecimal zeroedValue, String message) {
    return ObjectTryable.BuildFailure(message);
  }

  @Specialization
  protected Object tryableFailureObject(Object zeroedValue, String message) {
    return ObjectTryable.BuildFailure(message);
  }
}
