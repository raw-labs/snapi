package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.TakeListEntry;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListTakeNodeGen;

import java.util.List;

public class TruffleTakeListEntry extends TakeListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2ListType listType = (Rql2ListType) type;
    return ListTakeNodeGen.create(
        args.get(0).getExprNode(), args.get(1).getExprNode(), (Rql2Type) listType.innerType());
  }
}
