package raw.compiler.snapi.truffle.builtin.csv_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.CsvReadEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

import java.util.List;

public class TruffleCsvReadEntry extends CsvReadEntry implements TruffleEntryExtension {
    @Override
    public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
        return null;
    }
}
