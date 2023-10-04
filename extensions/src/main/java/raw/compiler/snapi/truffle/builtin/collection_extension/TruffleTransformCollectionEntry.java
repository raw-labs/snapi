package raw.compiler.snapi.truffle.builtin.collection_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.TransformCollectionEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionTransformNodeGen;

import java.util.List;

public class TruffleTransformCollectionEntry extends TransformCollectionEntry
    implements TruffleEntryExtension {

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return CollectionTransformNodeGen.create(args.get(0).getExprNode(), args.get(1).getExprNode());
  }
}
