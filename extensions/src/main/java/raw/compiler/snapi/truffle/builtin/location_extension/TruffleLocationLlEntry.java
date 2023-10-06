package raw.compiler.snapi.truffle.builtin.location_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LocationLlEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationLlNodeGen;

import java.util.List;

public class TruffleLocationLlEntry extends LocationLlEntry implements TruffleEntryExtension {
    @Override
    public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
        return LocationLlNodeGen.create(args.get(0).getExprNode());
    }
}
