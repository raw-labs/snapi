package raw.compiler.snapi.truffle.builtin.collection_extension;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.OrderByCollectionEntry;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionOrderByNode;

public class TruffleOrderByCollectionEntry extends OrderByCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    AtomicInteger index = new AtomicInteger();

    ExpressionNode[] keyFunctions =
        args.stream()
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 0)
            .map(a -> args.get(a).getExprNode())
            .toArray(ExpressionNode[]::new);

    Rql2TypeWithProperties[] keyTypes =
        args.stream()
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 0)
            .map(a -> (Rql2TypeWithProperties) args.get(a).getType())
            .toArray(Rql2TypeWithProperties[]::new);

    ExpressionNode[] orderings =
        args.stream()
            .map(a -> index.getAndIncrement())
            .filter(a -> a % 2 == 1)
            .map(a -> args.get(a).getExprNode())
            .toArray(ExpressionNode[]::new);
    Rql2IterableType valueType = (Rql2IterableType) args.get(0).getType();

    return new CollectionOrderByNode(
        args.get(0).getExprNode(), keyFunctions, orderings, keyTypes, valueType);
  }
}
