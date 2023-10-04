package raw.compiler.snapi.truffle.builtin.collection_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.TupleAvgCollectionEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionTupleAvgNodeGen;

public class TruffleTupleAvgCollectionEntry extends TupleAvgCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return CollectionTupleAvgNodeGen.create(args.get(0).getExprNode());
  }
}
