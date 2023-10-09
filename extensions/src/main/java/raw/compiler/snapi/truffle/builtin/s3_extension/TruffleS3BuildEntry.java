package raw.compiler.snapi.truffle.builtin.s3_extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.S3BuildEntry;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode;

public class TruffleS3BuildEntry extends S3BuildEntry implements TruffleEntryExtension {

  private static final Map<String, String> keyMap =
      new HashMap<>() {
        {
          put("region", "s3-region");
          put("accessKey", "s3-access-key");
          put("secretKey", "s3-secret-key");
        }
      };

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode url = args.get(0).getExprNode();
    String[] keys =
        args.stream()
            .skip(1)
            .map(TruffleArg::getIdentifier)
            .filter(Objects::nonNull)
            .map(keyMap::get)
            .toArray(String[]::new);
    ExpressionNode[] values =
        args.stream().skip(1).map(TruffleArg::getExprNode).toArray(ExpressionNode[]::new);
    Rql2TypeWithProperties[] types =
        args.stream()
            .skip(1)
            .filter(arg -> arg.getIdentifier() != null)
            .map(arg -> (Rql2TypeWithProperties) arg.getType())
            .toArray(Rql2TypeWithProperties[]::new);
    return new LocationBuildNode(url, keys, values, types);
  }
}
