package raw.compiler.snapi.truffle.builtin.string_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.StringReadEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.string_package.StringReadNodeGen;
import raw.runtime.truffle.ast.expressions.literals.StringNode;

public class TruffleStringReadEntry extends StringReadEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode encoding =
        args.stream()
            .filter(arg -> arg.getIdentifier() != null && arg.getIdentifier().equals("encoding"))
            .findFirst()
            .map(TruffleArg::getExprNode)
            .orElseGet(() -> new StringNode("utf-8"));
    return StringReadNodeGen.create(args.get(0).getExprNode(), encoding);
  }
}
