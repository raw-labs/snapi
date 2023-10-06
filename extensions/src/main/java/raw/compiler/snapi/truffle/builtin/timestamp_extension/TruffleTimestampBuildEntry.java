package raw.compiler.snapi.truffle.builtin.timestamp_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.builtin.TimestampBuildEntry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package.TimestampBuildNodeGen;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public class TruffleTimestampBuildEntry extends TimestampBuildEntry
    implements TruffleShortEntryExtension {

  @Override
  public ListMap<String, Tuple2<Type, Exp>> getOptionalParamsMap() {
    return this.optionalParamsMap();
  }

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {

    return TimestampBuildNodeGen.create(
        args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5), args.get(6));
  }
}
