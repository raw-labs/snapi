package raw.compiler.snapi.truffle.builtin.regex_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.RegexReplaceEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ExpressionNode;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import java.util.List;

import raw.runtime.truffle.ast.expressions.builtin.regex_package.RegexReplaceNodeGen;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;

public class TruffleRegexReplaceEntry extends RegexReplaceEntry implements TruffleEntryExtension {
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return RegexReplaceNodeGen.create(args.get(0).getExprNode(), args.get(1).getExprNode(), args.get(2).getExprNode());
  }
}
