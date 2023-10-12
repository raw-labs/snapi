package raw.compiler.snapi.truffle.builtin.test_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;
import raw.runtime.truffle.ast.expressions.literals.IntNode;

public interface TruffleVarArgs extends TruffleEntryExtension {
  @Override
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode result = new IntNode("0");
    for (TruffleArg arg : args) {
      result = new PlusNode(result, arg.exprNode());
    }
    return result;
  }
}
