package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.FirstListEntry;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListFirstNodeGen;

import java.util.List;

public class TruffleFirstListEntry extends FirstListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return ListFirstNodeGen.create(args.get(0).getExprNode(), (Rql2Type) type);
  }
}
