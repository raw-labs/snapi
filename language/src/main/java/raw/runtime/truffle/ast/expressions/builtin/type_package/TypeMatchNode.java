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
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.or.OrObject;

public class TypeMatchNode extends ExpressionNode {
  @Child private ExpressionNode typeExp;

  @Children private final ExpressionNode[] closureExps;

  @Child InteropLibrary interop;

  public TypeMatchNode(ExpressionNode child, ExpressionNode[] children) {
    this.typeExp = child;
    this.closureExps = children;
    this.interop = InteropLibrary.getFactory().createDispatched(children.length);
  }

  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    OrObject orType = (OrObject) this.typeExp.executeGeneric(frame);
    int index = orType.getIndex();
    Object[] closures = new Object[closureExps.length];
    for (int i = 0; i < closureExps.length; i++) {
      closures[i] = (Object) closureExps[i].executeGeneric(frame);
    }
    try {
      return interop.execute(closures[index], orType.getValue());
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    }
  }
}
