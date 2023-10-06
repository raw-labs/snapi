package raw.compiler.snapi.truffle.builtin.regex_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.RegexGroupsEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ExpressionNode;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import java.util.List;

import raw.runtime.truffle.ast.expressions.builtin.regex_package.RegexGroupsNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class TruffleRegexGroupsEntry extends RegexGroupsEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return RegexGroupsNodeGen.create(args.get(0).getExprNode(), args.get(1).getExprNode());
  }
}
