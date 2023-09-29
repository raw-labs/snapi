package raw.compiler.snapi.truffle;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.EntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

import java.util.List;

public abstract class TruffleEntryExtension {

  ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage);
}
