package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.TransformListEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListTransformNodeGen;

import java.util.List;

public class TruffleTransformListEntry extends TransformListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    FunType funType = (FunType) args.get(1).getType();
    return ListTransformNodeGen.create(
        args.get(0).getExprNode(), args.get(1).getExprNode(), (Rql2Type) funType.r());
  }
}
