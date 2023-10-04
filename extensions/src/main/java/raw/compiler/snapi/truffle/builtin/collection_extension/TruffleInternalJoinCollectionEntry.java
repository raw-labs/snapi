package raw.compiler.snapi.truffle.builtin.collection_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.InternalJoinCollectionEntry;
import raw.compiler.rql2.source.FunType;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionJoinNodeGen;

import java.util.List;

public class TruffleInternalJoinCollectionEntry extends InternalJoinCollectionEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    TruffleArg left = args.get(0);
    TruffleArg right = args.get(1);
    TruffleArg reshape = args.get(2);
    TruffleArg predicate = args.get(3);
    Rql2IterableType rql2IterableType = (Rql2IterableType) right.getType();
    Rql2TypeWithProperties rightType = (Rql2TypeWithProperties) rql2IterableType.innerType();
    boolean reshapeBeforePredicate = ((FunType) predicate.getType()).ms().size() == 1;
    return CollectionJoinNodeGen.create(
        left.getExprNode(),
        right.getExprNode(),
        reshape.getExprNode(),
        predicate.getExprNode(),
        rightType,
        reshapeBeforePredicate);
  }
}
