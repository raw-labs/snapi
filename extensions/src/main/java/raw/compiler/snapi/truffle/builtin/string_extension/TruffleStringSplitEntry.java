package raw.compiler.snapi.truffle.builtin.string_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.builtin.StringSplitEntry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.string_package.StringSplitNodeGen;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleStringSplitEntry extends StringSplitEntry
    implements TruffleShortEntryExtension {

  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return StringSplitNodeGen.create(args.get(0), args.get(1));
  }
}
