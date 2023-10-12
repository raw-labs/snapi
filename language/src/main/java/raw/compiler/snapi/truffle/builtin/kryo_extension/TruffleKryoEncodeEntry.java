package raw.compiler.snapi.truffle.builtin.kryo_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.KryoEncodeEntry;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.io.kryo.KryoWriteNode;

import java.util.List;

public class TruffleKryoEncodeEntry extends KryoEncodeEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return new KryoWriteNode(args.get(0).exprNode(), (Rql2TypeWithProperties) args.get(0).type());
  }
}
