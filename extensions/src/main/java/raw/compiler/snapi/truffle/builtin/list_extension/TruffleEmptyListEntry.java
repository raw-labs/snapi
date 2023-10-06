package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.EmptyListEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListBuildNode;

import java.util.List;

public class TruffleEmptyListEntry extends EmptyListEntry implements TruffleEntryExtension {
    @Override
    public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
        return new ListBuildNode(type, new ExpressionNode[0]);
    }
}
