package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.BuildListEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode;

import java.util.List;

public class TruffleBuildListEntry extends BuildListEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return new ListBuildNode(
        type, args.stream().map(TruffleArg::getExprNode).toArray(ExpressionNode[]::new));
  }
}
