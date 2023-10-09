package raw.compiler.snapi.truffle.builtin.nullable_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.NullableTransformEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.option.OptionMapNodeGen;

public class TruffleNullableTransformEntry extends NullableTransformEntry
    implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return OptionMapNodeGen.create(args.get(0).getExprNode(), args.get(1).getExprNode());
  }
}
