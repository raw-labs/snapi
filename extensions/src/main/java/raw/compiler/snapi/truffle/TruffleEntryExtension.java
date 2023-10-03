package raw.compiler.snapi.truffle;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.truffle.TruffleEmitter;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

import java.util.List;
import java.util.stream.Collectors;

public interface TruffleEntryExtension {

  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    throw new UnsupportedOperationException("Not implemented");
  }

  default ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    return toTruffle(
        type,
        args.stream()
            .map(
                a ->
                    new TruffleArg(
                        emitter.recurseExp(a.e()),
                        a.t(),
                        a.idn().isDefined() ? a.idn().get() : null))
            .collect(Collectors.toList()),
        emitter.rawLanguage());
  }
}
