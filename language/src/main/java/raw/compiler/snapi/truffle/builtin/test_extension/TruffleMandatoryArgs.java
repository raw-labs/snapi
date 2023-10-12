package raw.compiler.snapi.truffle.builtin.test_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;

public interface TruffleMandatoryArgs extends TruffleEntryExtension {
  @Override
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return new PlusNode(args.get(0).exprNode(), args.get(1).exprNode());
  }
}
