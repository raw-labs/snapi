package raw.compiler.snapi.truffle.builtin.time_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.builtin.TimeParseEntry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.temporals.time_package.TimeParseNodeGen;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleTimeParseEntry extends TimeParseEntry implements TruffleShortEntryExtension {

  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return TimeParseNodeGen.create(args.get(0), args.get(1));
  }
}
