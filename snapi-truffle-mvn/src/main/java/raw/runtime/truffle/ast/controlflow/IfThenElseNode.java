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

package raw.runtime.truffle.ast.controlflow;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.oracle.truffle.api.profiles.CountingConditionProfile;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public final class IfThenElseNode extends ExpressionNode {

  @Child private ExpressionNode conditionNode;

  @Child private ExpressionNode thenNode;

  @Child private ExpressionNode elseNode;

  private final CountingConditionProfile condition = CountingConditionProfile.create();

  public IfThenElseNode(
      ExpressionNode conditionNode, ExpressionNode thenNode, ExpressionNode elseNode) {
    this.conditionNode = conditionNode;
    this.thenNode = thenNode;
    this.elseNode = elseNode;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    /*
     * In the interpreter, record profiling information that the condition was executed and with
     * which outcome.
     */
    if (condition.profile(evaluateCondition(frame))) {
      /* Execute the then-branch. */
      return thenNode.executeGeneric(frame);
    } else {
      /* Execute the else-branch. */
      return elseNode.executeGeneric(frame);
    }
  }

  private boolean evaluateCondition(VirtualFrame frame) {
    try {
      /*
       * The condition must evaluate to a boolean value, so we call the boolean-specialized
       * execute method.
       */
      return conditionNode.executeBoolean(frame);
    } catch (UnexpectedResultException ex) {
      /*
       * The condition evaluated to a non-boolean result. This is a type error in the SL
       * program.
       */
      throw new RawTruffleRuntimeException(ex, this);
    }
  }
}
