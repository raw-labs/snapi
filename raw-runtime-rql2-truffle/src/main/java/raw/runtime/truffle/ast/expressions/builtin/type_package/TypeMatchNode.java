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
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.or.OrObject;

public class TypeMatchNode extends ExpressionNode {
  @Node.Child private ExpressionNode typeExp;

  @Node.Children private final ExpressionNode[] closureExps;

  public TypeMatchNode(ExpressionNode child, ExpressionNode[] children) {
    this.typeExp = child;
    this.closureExps = children;
  }

  public Object executeGeneric(VirtualFrame frame) {
    OrObject orType = (OrObject) this.typeExp.executeGeneric(frame);

    Closure closure = (Closure) closureExps[orType.getIndex()].executeGeneric(frame);
    return closure.call(orType.getValue());
  }
}
