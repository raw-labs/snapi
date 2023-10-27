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

package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.instrumentation.Tag;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.function.Closure;

@NodeInfo(shortName = "invoke")
public final class InvokeNode extends ExpressionNode {

  @Child private ExpressionNode functionNode;
  @Child private InteropLibrary interop = InteropLibrary.getFactory().createDispatched(3);
  @Children private final ExpressionNode[] argumentNodes;

  private final String[] argNames;

  public InvokeNode(
      ExpressionNode functionNode, String[] argNames, ExpressionNode[] argumentNodes) {
    this.functionNode = functionNode;
    assert (argNames.length == argumentNodes.length);
    this.argNames = argNames;
    this.argumentNodes = argumentNodes;
  }

  @ExplodeLoop
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    CompilerAsserts.compilationConstant(argumentNodes.length);

    Closure closure = (Closure) functionNode.executeGeneric(frame);
    Object[] argumentValues = new Object[argumentNodes.length];
    for (int i = 0; i < argumentNodes.length; i++) {
      argumentValues[i] = argumentNodes[i].executeGeneric(frame);
    }

    closure.setNamedArgNames(argNames);

    try {
      return interop.execute(closure, argumentValues);
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    }
  }

  @Override
  public boolean hasTag(Class<? extends Tag> tag) {
    if (tag == StandardTags.CallTag.class) {
      return true;
    }
    return super.hasTag(tag);
  }
}
