package raw.compiler.snapi.truffle.builtin.long_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LongFromEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.numeric.long_package.LongFromNodeGen;

import java.util.List;

public class TruffleLongFromEntry extends LongFromEntry implements TruffleEntryExtension {
    @Override
    public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
        return LongFromNodeGen.create(args.get(0).getExprNode());
    }
}
