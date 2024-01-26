package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Function;
import raw.runtime.truffle.runtime.function.Lambda;

public class LambdaNode extends ExpressionNode {

  @CompilerDirectives.CompilationFinal private final RootCallTarget callTarget;

  public LambdaNode(Function f) {
    callTarget = f.getCallTarget();
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame virtualFrame) {
    return new Lambda(callTarget, virtualFrame);
  }
}
