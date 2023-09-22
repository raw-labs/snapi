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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;

public class ByteNode extends ExpressionNode {

  private final String value;

  public ByteNode(String value) {
    this.value = value;
  }

  @Override
  public final byte executeByte(VirtualFrame virtualFrame) {
    return parseByte();
  }

  @Override
  public final Object executeGeneric(VirtualFrame virtualFrame) {
    return parseByte();
  }

  @CompilerDirectives.TruffleBoundary
  private byte parseByte() {
    return Byte.parseByte(value);
  }
}
