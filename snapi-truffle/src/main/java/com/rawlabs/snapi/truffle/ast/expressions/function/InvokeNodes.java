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

package com.rawlabs.snapi.truffle.ast.expressions.function;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.function.FunctionExecuteNodes;
import com.rawlabs.snapi.truffle.runtime.function.Lambda;

// A proxy node that dispatches to the correct execute method of a function runtime object.
public class InvokeNodes {
  @NodeInfo(shortName = "Invoke")
  @GenerateUncached
  @GenerateInline
  public abstract static class Invoke extends Node {

    public abstract Object execute(
        Node node, Object function, String[] argNames, Object[] argumentValues);

    @Specialization(guards = "argumentValues.length == 0")
    static Object execZero(
        Node node,
        Lambda function,
        String[] argNames,
        Object[] argumentValues,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteZeroNode functionExecZero) {
      return functionExecZero.execute(thisNode, function);
    }

    @Specialization(guards = "argumentValues.length == 1")
    static Object execOne(
        Node node,
        Lambda function,
        String[] argNames,
        Object[] argumentValues,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteOneNode functionExecOne) {
      return functionExecOne.execute(thisNode, function, argumentValues[0]);
    }

    @Specialization(guards = "argumentValues.length == 2")
    static Object execTwo(
        Node node,
        Lambda function,
        String[] argNames,
        Object[] argumentValues,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteTwoNode functionExecTwo) {
      return functionExecTwo.execute(thisNode, function, argumentValues[0], argumentValues[1]);
    }

    @Specialization
    static Object execMany(
        Node node,
        Object function,
        String[] argNames,
        Object[] argumentValues,
        @Bind("$node") Node thisNode,
        @Cached FunctionExecuteNodes.FunctionExecuteWithNames functionWithNames) {
      return functionWithNames.execute(thisNode, function, argNames, argumentValues);
    }
  }
}
