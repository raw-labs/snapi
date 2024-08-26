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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleUnexpectedNullException;
import com.rawlabs.snapi.truffle.runtime.primitives.NullObject;

public final class TypeProtectCastOptionNode extends ExpressionNode {

  @Child private ExpressionNode child;

  public TypeProtectCastOptionNode(ExpressionNode child) {
    this.child = child;
  }

  public Object executeGeneric(VirtualFrame virtualFrame) {
    try {
      return child.executeGeneric(virtualFrame);
    } catch (TruffleUnexpectedNullException e) {
      return NullObject.INSTANCE;
    }
  }
}
