package raw.compiler.snapi.truffle.builtin.binary;

import java.util.List;

import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.builtin.FromStringBinaryEntryExtension;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.binary_package.BinaryFromStringNodeGen;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleFromStringBinaryEntry extends FromStringBinaryEntryExtension
    implements TruffleShortEntryExtension {

  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return BinaryFromStringNodeGen.create(args.get(0));
  }
}
