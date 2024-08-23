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

package com.rawlabs.snapi.truffle.runtime.runtime.exceptions.rdbms;

import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.runtime.ast.io.jdbc.JdbcQuery;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;
import java.sql.SQLException;

public class JdbcExceptionHandler {

  public TruffleRuntimeException rewrite(SQLException e, JdbcQuery rs, Node location) {
    return new JdbcReaderTruffleException(e.getMessage(), rs, e, location);
  }

  public TruffleRuntimeException columnParseError(SQLException e, String colName, Node location) {
    return new JdbcParserTruffleException(
        String.format("column '%s': %s", colName, e.getMessage()), e, location);
  }

  public TruffleRuntimeException rewrite(SQLException e, JdbcQuery rs) {
    return new JdbcReaderTruffleException(e.getMessage(), rs, e, null);
  }
}
