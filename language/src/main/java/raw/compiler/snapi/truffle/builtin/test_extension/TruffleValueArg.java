package raw.compiler.snapi.truffle.builtin.test_extension;

import raw.compiler.base.source.Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.record.RecordBuildNode;

import java.util.List;

public interface TruffleValueArg extends TruffleEntryExtension {
  @Override
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return new RecordBuildNode(
        new ExpressionNode[] {new StringNode("arg"), args.get(0).exprNode()});
  }
}
