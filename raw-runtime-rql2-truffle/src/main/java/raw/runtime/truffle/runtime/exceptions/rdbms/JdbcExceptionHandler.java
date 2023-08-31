/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.runtime.truffle.runtime.exceptions.rdbms;

import com.oracle.truffle.api.nodes.Node;
import java.sql.SQLException;
import raw.runtime.truffle.ast.io.jdbc.JdbcQuery;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public class JdbcExceptionHandler {

  public RawTruffleRuntimeException rewrite(SQLException e, JdbcQuery rs, Node location) {
    return new JdbcReaderRawTruffleException(e.getMessage(), rs, e, location);
  }

  public RawTruffleRuntimeException columnParseError(
      SQLException e, String colName, Node location) {
    return new JdbcParserRawTruffleException(
        String.format("column '%s': %s", colName, e.getMessage()), e, location);
  }

  public RawTruffleRuntimeException rewrite(SQLException e, JdbcQuery rs) {
    return new JdbcReaderRawTruffleException(e.getMessage(), rs, e, null);
  }
}
