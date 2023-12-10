package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import raw.runtime.truffle.ast.io.jdbc.JdbcQuery;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.SourceContext;

public class JdbcQueryComputeNext {

  private final LocationObject dbLocation;
  private final String query;
  private final RootCallTarget rowParserCallTarget;
  private final SourceContext context;
  private final JdbcExceptionHandler exceptionHandler;

  private JdbcQuery rs = null;

  public JdbcQueryComputeNext(
      LocationObject dbLocation,
      String query,
      SourceContext context,
      RootCallTarget rowParserCallTarget,
      JdbcExceptionHandler exceptionHandler) {
    this.context = context;
    this.dbLocation = dbLocation;
    this.query = query;
    this.rowParserCallTarget = rowParserCallTarget;
    this.exceptionHandler = exceptionHandler;
  }

  public JdbcQuery getRs() {
    return rs;
  }

  public void init() {
    this.rs =
        new JdbcQuery(
            this.dbLocation.getLocationDescription(),
            this.query,
            this.context,
            this.exceptionHandler);
  }

  public void close() {
    this.rs.close();
    this.rs = null;
  }
}
