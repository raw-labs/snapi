package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.UnsafeFromListEntry;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListFromUnsafeNodeGen;

import java.util.List;

public class TruffleUnsafeFromListEntry extends UnsafeFromListEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2ListType rql2ListType = (Rql2ListType) type;
    return ListFromUnsafeNodeGen.create(
        args.get(0).getExprNode(), (Rql2Type) rql2ListType.innerType());
  }
}
