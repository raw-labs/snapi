package raw.compiler.snapi.truffle;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

import java.util.List;

public interface TruffleEntryExtension {

  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    throw new UnsupportedOperationException("Not implemented");
  }

  default ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    return toTruffle(
        type,
        args.stream()
            .map(a -> new TruffleArg(emitter.recurseExp(a.e()), a.t(), a.idn().getOrElse(null)))
            .toList(),
        emitter.getLanguage());
  }
}
