package raw.compiler.snapi.truffle.builtin.binary;

import raw.compiler.rql2.builtin.BinaryBase64Entry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.binary_package.BinaryBase64NodeGen;

import java.util.List;

public class TruffleBinaryBase64Entry extends BinaryBase64Entry
    implements TruffleShortEntryExtension {
  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return BinaryBase64NodeGen.create(args.get(0));
  }
}
