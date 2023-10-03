package raw.compiler.snapi.truffle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.truffle.TruffleEmitter;
import raw.runtime.truffle.ExpressionNode;
import scala.Tuple2;
import scala.collection.immutable.ListMap;

public interface TruffleShortEntryExtension extends TruffleEntryExtension {

  ListMap<String, Tuple2<Type, Exp>> optionalParamsMap = new ListMap<>();

  ExpressionNode toTruffle(List<ExpressionNode> args);

  default ExpressionNode toTruffle(Type type, List<Rql2Arg> args, TruffleEmitter emitter) {
    List<ExpressionNode> orderedArgs =
            args.stream()
                    .filter(a -> a.idn().isEmpty())
                    .map(a -> emitter.recurseExp(a.e())).collect(Collectors.toList());

    optionalParamsMap
        .keys()
        .toList()
        .foreach(
            (String k) -> {
              ExpressionNode e =
                  args.stream()
                      .filter(a -> a.idn().get().equals(k))
                      .map(a -> emitter.recurseExp(a.e()))
                      .findFirst()
                      .orElse(emitter.recurseExp(optionalParamsMap.apply(k)._2()));
              orderedArgs.add(e);
              return null;
            });

    return this.toTruffle(orderedArgs);
  }
}
