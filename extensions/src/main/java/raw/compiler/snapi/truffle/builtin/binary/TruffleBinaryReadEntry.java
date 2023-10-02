package raw.compiler.snapi.truffle.builtin.binary;

import raw.compiler.rql2.builtin.BinaryReadEntry;
import raw.compiler.snapi.truffle.TruffleShortEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.binary_package.BinaryReadNodeGen;
import scala.collection.Seq;

import java.util.List;

public class TruffleBinaryReadEntry extends BinaryReadEntry implements TruffleShortEntryExtension {

  @Override
  public ExpressionNode toTruffle(List<ExpressionNode> args) {
    return BinaryReadNodeGen.create(args.get(0));
  }
}
