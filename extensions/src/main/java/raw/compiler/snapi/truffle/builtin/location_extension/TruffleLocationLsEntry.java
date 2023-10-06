package raw.compiler.snapi.truffle.builtin.location_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LocationLsEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationLsNodeGen;

import java.util.List;

public class TruffleLocationLsEntry extends LocationLsEntry implements TruffleEntryExtension {
    @Override
    public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
        return LocationLsNodeGen.create(args.get(0).getExprNode());
    }
}
