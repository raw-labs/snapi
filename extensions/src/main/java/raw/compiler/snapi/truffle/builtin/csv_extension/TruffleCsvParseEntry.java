package raw.compiler.snapi.truffle.builtin.csv_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.CsvParseEntry;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

import java.util.List;

public class TruffleCsvParseEntry extends CsvParseEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    CsvParser makeParser = new CsvParser(args);
    ExpressionNode url =
        args.stream()
            .filter(a -> a.getIdentifier() == null)
            .findFirst()
            .orElseThrow()
            .getExprNode();
    return makeParser.stringParser(url, (Rql2TypeWithProperties) type, rawLanguage);
  }
}
