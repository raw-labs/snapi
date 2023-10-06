package raw.compiler.snapi.truffle.builtin.sqlserver_extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.SQLServerQueryEntry;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.rql2.truffle.builtin.TruffleJdbc;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.runtime.exceptions.rdbms.SqlServerExceptionHandler;

public class TruffleSQLServerQueryEntry extends SQLServerQueryEntry
    implements TruffleEntryExtension {

  private record Handler(String key, ExpressionNode value) {}

  private final Map<String, String> keyMappings =
      new HashMap<>() {
        {
          put("host", "db-host");
          put("port", "db-port");
          put("username", "db-username");
          put("password", "db-password");
        }
      };

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode db = args.get(0).getExprNode();
    List<Handler> optionalArgs =
        args.stream()
            .filter(arg -> keyMappings.containsKey(arg.getIdentifier()))
            .map(
                arg -> {
                  String idn = arg.getIdentifier();
                  return new Handler(keyMappings.get(idn), arg.getExprNode());
                })
            .toList();
    String[] keys = optionalArgs.stream().map(Handler::key).toArray(String[]::new);
    ExpressionNode[] values =
        optionalArgs.stream().map(Handler::value).toArray(ExpressionNode[]::new);
    Rql2TypeWithProperties[] types =
        args.stream()
            .filter(arg -> arg.getIdentifier() != null)
            .map(arg -> (Rql2TypeWithProperties) arg.getType())
            .toArray(Rql2TypeWithProperties[]::new);
    ExpressionNode location =
        new LocationBuildNode(new PlusNode(new StringNode("sqlserver:"), db), keys, values, types);
    return TruffleJdbc.query(
        location, args.get(1).getExprNode(), type, new SqlServerExceptionHandler(), rawLanguage);
  }
}
