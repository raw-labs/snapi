package raw.compiler.snapi.truffle.builtin.test_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.builtin.StrictArgsColPassThroughTestEntry;
import raw.compiler.snapi.truffle.TruffleEmitter;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.binary.MultNodeGen;
import raw.runtime.truffle.ast.expressions.iterable.collection.CollectionTransformNodeGen;
import raw.runtime.truffle.ast.expressions.literals.IntNode;
import raw.runtime.truffle.ast.local.ReadParamNode;

public class TruffleStrictArgsColPassThroughTestEntry extends StrictArgsColPassThroughTestEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    return CollectionTransformNodeGen.create(
        emitter.recurseExp(args.get(0).e()),
        emitter.recurseLambda(() -> MultNodeGen.create(new ReadParamNode(0), new IntNode("10"))));
  }
}
