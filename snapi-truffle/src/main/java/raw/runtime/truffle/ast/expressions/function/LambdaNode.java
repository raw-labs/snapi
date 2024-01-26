package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Function;
import raw.runtime.truffle.runtime.function.Lambda;

public class LambdaNode extends ExpressionNode {
  private final RootCallTarget callTarget;
  @CompilerDirectives.CompilationFinal private Lambda lambda;

  public LambdaNode(Function f) {
    callTarget = f.getCallTarget();
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame virtualFrame) {
    if (lambda == null) {
      lambda = new Lambda(callTarget, virtualFrame);
    }
    return lambda;
  }
}
