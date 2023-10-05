package raw.compiler.snapi.truffle.builtin.type_extension;

import java.util.Comparator;
import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.TypesMerger;
import raw.compiler.rql2.builtin.TypeMatchEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2OrType;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.type_package.TypeMatchNode;

public class TruffleTypeMatchEntry extends TypeMatchEntry implements TruffleEntryExtension {

  private final TypesMerger typesMerger = new TypesMerger();

  private record Handler(int idx, TruffleArg arg) {}

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Rql2OrType orType = (Rql2OrType) args.get(0).getType();
    ExpressionNode[] handlers =
        args.stream()
            .skip(1)
            .map(
                arg -> {
                  Type paramType =
                      ((FunType) arg.getType()).ms().apply(0); // first (and only) parameter type
                  int idx =
                      orType
                          .tipes()
                          .indexWhere(
                              t ->
                                  typesMerger.propertyCompatible(
                                      t, paramType)); // where is that type in the or-type?
                  return new Handler(idx, arg);
                })
            .sorted(Comparator.comparingInt(Handler::idx))
            . // reorder items by index
            map(Handler::arg)
            .map(TruffleArg::getExprNode)
            .toArray(ExpressionNode[]::new); // extract 'e' (the function)
    return new TypeMatchNode(args.getFirst().getExprNode(), handlers);
  }
}
