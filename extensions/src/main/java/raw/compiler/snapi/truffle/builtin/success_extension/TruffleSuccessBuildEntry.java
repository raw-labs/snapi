package raw.compiler.snapi.truffle.builtin.success_extension;

import java.util.List;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.SuccessBuildEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.tryable.TryableSuccessNodeGen;

public class TruffleSuccessBuildEntry extends SuccessBuildEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return TryableSuccessNodeGen.create(args.get(0).getExprNode());
  }
}
