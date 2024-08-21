/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.snapi.truffle.runtime.ast.osr;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RepeatingNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleInternalErrorException;

public class OSRGeneratorNode extends Node implements RepeatingNode {

  @Child private ExpressionNode conditionNode;

  @Child private ExpressionNode bodyNode;

  public OSRGeneratorNode(ExpressionNode conditionNode, ExpressionNode bodyNode) {
    this.conditionNode = conditionNode;
    this.bodyNode = bodyNode;
  }

  public boolean executeRepeating(VirtualFrame frame) {
    try {
      if (conditionNode.executeBoolean(frame)) {
        bodyNode.executeVoid(frame);
        return true;
      }
      return false;
    } catch (UnexpectedResultException e) {
      throw new RawTruffleInternalErrorException(e);
    }
  }

  @Override
  public String toString() {
    return bodyNode.toString();
  }
}
