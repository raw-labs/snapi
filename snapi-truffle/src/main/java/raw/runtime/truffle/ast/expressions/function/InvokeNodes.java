package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.function.FunctionExecuteNodes;
import raw.runtime.truffle.runtime.function.Lambda;

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

    @Specialization(guards = "argumentValues.length == 1")
    static Object execTwo(
        Node node,
        Lambda function,
        String[] argNames,
        Object[] argumentValues,
        @Bind("$node") Node thisNode,
        @Cached Lambda.LambdaExecuteTwoNode functionExecOne) {
      return functionExecOne.execute(thisNode, function, argumentValues[0], argumentValues[1]);
    }

    @Specialization
    static Object execTwo(
        Node node,
        Object function,
        String[] argNames,
        Object[] argumentValues,
        @Bind("$node") Node thisNode,
        @Cached FunctionExecuteNodes.FunctionExecuteWithNames functionExecOne) {
      return functionExecOne.execute(thisNode, function, argNames, argumentValues);
    }
  }
}
