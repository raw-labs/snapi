package raw.compiler.snapi.truffle.builtin.nullable_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.NullableIsNullEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ExpressionNode;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import java.util.List;

import raw.runtime.truffle.ast.expressions.option.OptionIsDefinedNodeGen;
import raw.runtime.truffle.ast.expressions.unary.NotNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class TruffleNullableIsNullEntry extends NullableIsNullEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return NotNodeGen.create(OptionIsDefinedNodeGen.create(args.get(0).getExprNode()));
  }
}
