package raw.compiler.snapi.truffle.builtin.math_extension;

import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.builtin.MathAtn2Entry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.math_package.MathAtn2NodeGen;
import raw.runtime.truffle.ast.expressions.literals.DoubleNode;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

import java.util.List;

public class TruffleMathAtn2Entry extends MathAtn2Entry implements TruffleShortEntryExtension {
  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return MathAtn2NodeGen.create(args.get(0), args.get(1));
  }
}
