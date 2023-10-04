package raw.compiler.snapi.truffle.builtin.collection_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.UnionCollectionEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionUnionNode;

public class TruffleUnionCollectionEntry extends UnionCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return new CollectionUnionNode(
        args.stream().map(TruffleArg::getExprNode).toArray(ExpressionNode[]::new));
  }
}
