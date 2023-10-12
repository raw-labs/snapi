package raw.compiler.snapi.truffle.builtin.test_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.VarNullableStringValueTestEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.option.OptionGetOrElseNodeGen;

public class TruffleVarNullableStringValueTestEntry extends VarNullableStringValueTestEntry
    implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode result = new StringNode("");
    for (TruffleArg arg : args) {
      result =
          new PlusNode(result, OptionGetOrElseNodeGen.create(arg.exprNode(), new StringNode("")));
    }
    return result;
  }
}
