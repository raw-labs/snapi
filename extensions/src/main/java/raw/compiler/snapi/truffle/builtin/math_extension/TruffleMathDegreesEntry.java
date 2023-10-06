package raw.compiler.snapi.truffle.builtin.math_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.builtin.MathDegreesEntry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.math_package.MathDegreesNodeGen;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleMathDegreesEntry extends MathDegreesEntry
    implements TruffleShortEntryExtension {
  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return MathDegreesNodeGen.create(args.get(0));
  }
}
