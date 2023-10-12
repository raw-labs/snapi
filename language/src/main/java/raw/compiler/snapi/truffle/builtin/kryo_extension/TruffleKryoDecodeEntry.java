package raw.compiler.snapi.truffle.builtin.kryo_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.KryoDecodeEntry;
import raw.compiler.rql2.source.ExpType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.io.kryo.KryoFromNode;

import java.util.List;

public class TruffleKryoDecodeEntry extends KryoDecodeEntry implements TruffleEntryExtension {
    @Override
    public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
        ExpType exprType = (ExpType) args.get(1).type();
        return new KryoFromNode(args.get(0).exprNode(), (Rql2TypeWithProperties) exprType.t());
    }
}
