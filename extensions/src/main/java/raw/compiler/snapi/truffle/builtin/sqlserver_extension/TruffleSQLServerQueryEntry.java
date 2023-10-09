package raw.compiler.snapi.truffle.builtin.sqlserver_extension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.SQLServerQueryEntry;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.rql2.truffle.builtin.TruffleJdbc;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.jdbc.WithJdbcArgs;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationBuildNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.runtime.exceptions.rdbms.SqlServerExceptionHandler;

public class TruffleSQLServerQueryEntry extends SQLServerQueryEntry
    implements TruffleEntryExtension, WithJdbcArgs {

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode db = args.get(0).getExprNode();

    String[] allKeys = new String[] {"db-host", "db-port", "db-username", "db-password"};
    ExpressionNode[] allValues =
        new ExpressionNode[] {host(args), port(args), username(args), password(args)};

    String[] keys =
        IntStream.range(0, allKeys.length)
            .filter(i -> allValues[i] != null)
            .mapToObj(i -> allKeys[i])
            .toArray(String[]::new);

    ExpressionNode[] values =
        IntStream.range(0, allValues.length)
            .filter(i -> allValues[i] != null)
            .mapToObj(i -> allValues[i])
            .toArray(ExpressionNode[]::new);

    Rql2TypeWithProperties[] types =
        args.stream()
            .skip(1)
            .filter(a -> a.getIdentifier() != null)
            .map(a -> (Rql2TypeWithProperties) a.getType())
            .toArray(Rql2TypeWithProperties[]::new);

    ExpressionNode location =
        new LocationBuildNode(new PlusNode(new StringNode("sqlserver:"), db), keys, values, types);
    return TruffleJdbc.query(
        location, args.get(1).getExprNode(), type, new SqlServerExceptionHandler(), rawLanguage);
  }
}
