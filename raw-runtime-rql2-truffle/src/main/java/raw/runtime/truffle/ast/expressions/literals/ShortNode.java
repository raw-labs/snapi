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

package raw.runtime.truffle.ast.expressions.literals;

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;

public class ShortNode extends ExpressionNode {

  private final String value;

  public ShortNode(String value) {
    this.value = value;
  }

  @Override
  public final short executeShort(VirtualFrame virtualFrame) {
    return Short.parseShort(value);
  }

  @Override
  public final Object executeGeneric(VirtualFrame virtualFrame) {
    return Short.parseShort(value);
  }
}
