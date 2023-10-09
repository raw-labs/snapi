package raw.compiler.snapi.truffle.builtin.nullable_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.NullableUnsafeGetEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ExpressionNode;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import java.util.List;

import raw.runtime.truffle.ast.expressions.option.OptionUnsafeGetNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class TruffleNullableUnsafeGetEntry extends NullableUnsafeGetEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return OptionUnsafeGetNodeGen.create(args.get(0).getExprNode());
  }
}
