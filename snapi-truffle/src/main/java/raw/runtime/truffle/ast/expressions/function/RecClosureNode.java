package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.function.Function;
import raw.runtime.truffle.runtime.function.RecClosure;

public class RecClosureNode extends ExpressionNode {

  @CompilerDirectives.CompilationFinal private final Function function;

  @Children private final ExpressionNode[] defaultArgumentExps;

  public RecClosureNode(Function f, ExpressionNode[] defaultArgumentExps) {
    this.function = f;
    this.defaultArgumentExps = defaultArgumentExps;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame virtualFrame) {
    int nArgs = defaultArgumentExps.length;
    Object[] defaultArguments = new Object[nArgs];
    for (int i = 0; i < nArgs; i++) {
      if (defaultArgumentExps[i] != null) {
        defaultArguments[i] = defaultArgumentExps[i].executeGeneric(virtualFrame);
      } else {
        defaultArguments[i] = null;
      }
    }
    return new RecClosure(this.function, defaultArguments, virtualFrame.materialize());
  }
}
