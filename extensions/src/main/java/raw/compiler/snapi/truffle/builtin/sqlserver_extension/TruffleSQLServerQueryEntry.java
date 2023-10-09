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

  private record JdbcParam(String key, ExpressionNode value) {
  }

  private final Map<String, String> keyMappings =
      new HashMap<>() {
        {
          put("host", "db-host");
          put("port", "db-port");
          put("username", "db-username");
          put("password", "db-password");
        }
      };

  protected static String[] optionalKeys(List<TruffleArg> args, Map<String, String> keyMappings) {
    List<JdbcParam> optionalArgs =
        args.stream()
            .map(arg -> new JdbcParam(keyMappings.get(arg.getIdentifier()), arg.getExprNode())).filter(param -> param.key() != null).toList();
    return optionalArgs.stream().map(JdbcParam::key).toArray(String[]::new);

  }

  protected static ExpressionNode[] optionalValues(List<TruffleArg> args, Map<String, String> keyMappings) {
    List<JdbcParam> optionalArgs =
        args.stream()
            .map(arg -> new JdbcParam(keyMappings.get(arg.getIdentifier()), arg.getExprNode())).filter(param -> param.key() != null).toList();
    return optionalArgs.stream().map(JdbcParam::value).toArray(ExpressionNode[]::new);
  }

  protected static Rql2TypeWithProperties[] types(List<TruffleArg> args) {
    return args.stream()
        .filter(arg -> arg.getIdentifier() != null)
        .map(arg -> (Rql2TypeWithProperties) arg.getType())
        .toArray(Rql2TypeWithProperties[]::new);
  }

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode db = args.get(0).getExprNode();
    String[] keys = optionalKeys(args, keyMappings);
    ExpressionNode[] values = optionalValues(args, keyMappings);
    Rql2TypeWithProperties[] types = types(args);
    ExpressionNode location =
        new LocationBuildNode(new PlusNode(new StringNode("sqlserver:"), db), keys, values, types);
    return TruffleJdbc.query(
        location, args.get(1).getExprNode(), type, new SqlServerExceptionHandler(), rawLanguage);
  }
}
