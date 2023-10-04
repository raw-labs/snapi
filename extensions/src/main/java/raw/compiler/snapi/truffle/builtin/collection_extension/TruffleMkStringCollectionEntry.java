package raw.compiler.snapi.truffle.builtin.collection_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.MkStringCollectionEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionMkStringNodeGen;
import raw.runtime.truffle.ast.expressions.literals.StringNode;

public class TruffleMkStringCollectionEntry extends MkStringCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode start =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains("start"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new StringNode(""));

    ExpressionNode sep =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains("sep"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new StringNode(""));

    ExpressionNode end =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains("end"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new StringNode(""));

    return CollectionMkStringNodeGen.create(args.get(0).getExprNode(), start, sep, end);
  }
}
