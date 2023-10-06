package raw.compiler.snapi.truffle.builtin.location_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LocationDescribeEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.literals.IntNode;

import java.util.List;

public class TruffleLocationDescribeEntry extends LocationDescribeEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode sampleSize =
        args.stream()
            .filter(a -> a.getIdentifier() != null && a.getIdentifier().contains("sampleSize"))
            .map(TruffleArg::getExprNode)
            .findFirst()
            .orElse(new IntNode(Integer.toString(Integer.MIN_VALUE)));
    return TruffleEntryExtension.super.toTruffle(type, args, rawLanguage);
  }
}
