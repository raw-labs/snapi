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

package raw.runtime.truffle.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleUnexpectedNullException;
import raw.runtime.truffle.runtime.nullable_tryable.NullableTryableLibrary;
import raw.runtime.truffle.runtime.nullable_tryable.RuntimeNullableTryableHandler;
import raw.runtime.truffle.runtime.option.*;

public final class TypeProtectCastOptionNode extends ExpressionNode {

  @Child private ExpressionNode child;

  private final RuntimeNullableTryableHandler handler = new RuntimeNullableTryableHandler();
  private final NullableTryableLibrary nullableTryables =
      NullableTryableLibrary.getFactory().create(handler);

  public TypeProtectCastOptionNode(ExpressionNode child) {
    this.child = child;
  }

  public Object executeGeneric(VirtualFrame virtualFrame) {
    try {
      return nullableTryables.boxOption(handler, child.executeGeneric(virtualFrame));
    } catch (RawTruffleUnexpectedNullException e) {
      return new EmptyOption();
    }
  }
}
