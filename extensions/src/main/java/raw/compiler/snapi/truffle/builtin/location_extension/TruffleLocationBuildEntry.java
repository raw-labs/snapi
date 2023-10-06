package raw.compiler.snapi.truffle.builtin.location_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.LocationBuildEntry;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode;

import java.util.List;

public class TruffleLocationBuildEntry extends LocationBuildEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    String[] keys =
        args.stream()
            .filter(a -> a.getIdentifier() != null)
            .map(a -> a.getIdentifier().replace('_', '-'))
            .toArray(String[]::new);

    ExpressionNode[] values =
        args.stream()
            .filter(a -> a.getIdentifier() != null)
            .map(TruffleArg::getExprNode)
            .toArray(ExpressionNode[]::new);

    Rql2TypeWithProperties[] types =
        args.stream()
            .filter(a -> a.getIdentifier() != null)
            .map(a -> (Rql2TypeWithProperties) a.getType())
            .toArray(Rql2TypeWithProperties[]::new);

    return new LocationBuildNode(args.get(0).getExprNode(), keys, values, types);
  }
}
