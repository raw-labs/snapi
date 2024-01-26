package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

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
    static Object doNonClosure(
        Node node,
        NonClosure nonClosure,
        @Bind("$node") Node thisNode,
        @Cached NonClosure.NonClosureExecuteZeroNode executeZeroNode) {
      return executeZeroNode.execute(thisNode, nonClosure);
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
    static Object doNonClosure(
        Node node,
        NonClosure nonClosure,
        Object argument,
        @Bind("$node") Node thisNode,
        @Cached NonClosure.NonClosureExecuteOneNode executeOneNode) {
      return executeOneNode.execute(thisNode, nonClosure, argument);
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
    static Object doNonClosure(
        Node node,
        NonClosure nonClosure,
        Object argument1,
        Object argument2,
        @Bind("$node") Node thisNode,
        @Cached NonClosure.NonClosureExecuteTwoNode executeTwoNode) {
      return executeTwoNode.execute(thisNode, nonClosure, argument1, argument2);
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
    static Object doNonClosure(
        Node node,
        NonClosure nonClosure,
        String[] namedArgNames,
        Object[] arguments,
        @Bind("$node") Node thisNode,
        @Cached NonClosure.NonClosureExecuteWithNamesNode executeWithNames) {
      return executeWithNames.execute(thisNode, nonClosure, namedArgNames, arguments);
    }
  }
}
