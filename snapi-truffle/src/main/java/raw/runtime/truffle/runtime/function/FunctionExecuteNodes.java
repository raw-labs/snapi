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

package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

// Proxy nodes that dispatches to the correct execute method of a function runtime object.
public class FunctionExecuteNodes {
  @NodeInfo(shortName = "Function.ExecuteZero")
  @GenerateUncached
  @GenerateInline
  public abstract static class FunctionExecuteZero extends Node {

    public abstract Object execute(Node node, Object function);

    @Specialization
    static Object doClosure(
        Node node,
        Closure closure,
        @Bind("$node") Node thisNode,
        @Cached Closure.ClosureExecuteZeroNode executeZeroNode) {
      return executeZeroNode.execute(thisNode, closure);
    }

    @Specialization
    static Object doRecClosure(
        Node node,
        RecClosure recClosure,
        @Bind("$node") Node thisNode,
        @Cached RecClosure.RecClosureExecuteZeroNode executeZeroNode) {
      return executeZeroNode.execute(thisNode, recClosure);
    }
    @Specialization
    static Object doLambda(
        Node node,
        Lambda lambda,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteZeroNode executeZeroNode) {
      return executeZeroNode.execute(thisNode, lambda);
    }
  }

  @NodeInfo(shortName = "Function.ExecuteOne")
  @GenerateUncached
  @GenerateInline
  public abstract static class FunctionExecuteOne extends Node {

    public abstract Object execute(Node node, Object function, Object argument);

    @Specialization
    static Object doClosure(
        Node node,
        Closure closure,
        Object argument,
        @Bind("$node") Node thisNode,
        @Cached Closure.ClosureExecuteOneNode executeOneNode) {
      return executeOneNode.execute(thisNode, closure, argument);
    }

    @Specialization
    static Object doClosure(
        Node node,
        RecClosure recClosure,
        Object argument,
        @Bind("$node") Node thisNode,
        @Cached RecClosure.RecClosureExecuteOneNode executeOneNode) {
      return executeOneNode.execute(thisNode, recClosure, argument);
    }

    @Specialization
    static Object doLambda(
        Node node,
        Lambda lambda,
        Object argument,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteOneNode executeOneNode) {
      return executeOneNode.execute(thisNode, lambda, argument);
    }
  }

  @NodeInfo(shortName = "Function.ExecuteTwo")
  @GenerateUncached
  @GenerateInline
  public abstract static class FunctionExecuteTwo extends Node {

    public abstract Object execute(Node node, Object function, Object argument1, Object argument2);

    @Specialization
    static Object doClosure(
        Node node,
        Closure closure,
        Object argument1,
        Object argument2,
        @Bind("$node") Node thisNode,
        @Cached Closure.ClosureExecuteTwoNode executeTwoNode) {
      return executeTwoNode.execute(thisNode, closure, argument1, argument2);
    }

    @Specialization
    static Object doRecClosure(
        Node node,
        RecClosure recClosure,
        Object argument1,
        Object argument2,
        @Bind("$node") Node thisNode,
        @Cached RecClosure.RecClosureExecuteTwoNode executeTwoNode) {
      return executeTwoNode.execute(thisNode, recClosure, argument1, argument2);
    }

    @Specialization
    static Object doLambda(
        Node node,
        Lambda lambda,
        Object argument1,
        Object argument2,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteTwoNode executeTwoNode) {
      return executeTwoNode.execute(thisNode, lambda, argument1, argument2);
    }
  }

  @NodeInfo(shortName = "Function.ExecuteWithNames")
  @GenerateUncached
  @GenerateInline
  public abstract static class FunctionExecuteWithNames extends Node {

    public abstract Object execute(
        Node node, Object function, String[] namedArgNames, Object[] arguments);

    @Specialization
    static Object doClosure(
        Node node,
        Closure closure,
        String[] namedArgNames,
        Object[] arguments,
        @Bind("$node") Node thisNode,
        @Cached Closure.ClosureExecuteWithNamesNode executeWithNames) {
      return executeWithNames.execute(thisNode, closure, namedArgNames, arguments);
    }

    @Specialization
    static Object doRecClosure(
        Node node,
        RecClosure recClosure,
        String[] namedArgNames,
        Object[] arguments,
        @Bind("$node") Node thisNode,
        @Cached RecClosure.RecClosureExecuteWithNamesNode executeWithNames) {
      return executeWithNames.execute(thisNode, recClosure, namedArgNames, arguments);
    }

    @Specialization
    static Object doLambda(
        Node node,
        Lambda lambda,
        String[] namedArgNames,
        Object[] arguments,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteManyNode executeWithNames) {
      return executeWithNames.execute(thisNode, lambda, arguments);
    }
  }
}
