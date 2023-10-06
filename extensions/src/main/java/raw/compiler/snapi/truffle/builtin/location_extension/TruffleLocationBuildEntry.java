package raw.compiler.snapi.truffle.builtin.location_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LocationBuildEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

import java.util.List;

public class TruffleLocationBuildEntry extends LocationBuildEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    String[] keys =
        args.stream()
            .filter(a -> a.getIdentifier() != null)
            .map(a -> a.getIdentifier().replace('_', '-'))
            .toArray(String[]::new);

    String[] values =
            args.stream()
                    .filter(a -> a.getIdentifier() != null)
                    .map(a -> a.getIdentifier().replace('_', '-'))
                    .toArray(String[]::new);

    return TruffleEntryExtension.super.toTruffle(type, args, rawLanguage);
  }
}
