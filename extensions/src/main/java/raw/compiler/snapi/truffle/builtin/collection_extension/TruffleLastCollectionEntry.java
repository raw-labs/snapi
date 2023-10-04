package raw.compiler.snapi.truffle.builtin.collection_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LastCollectionEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionLastNodeGen;

import java.util.List;

public class TruffleLastCollectionEntry extends LastCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return CollectionLastNodeGen.create(args.get(0).getExprNode());
  }
}
