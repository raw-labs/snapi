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

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import raw.runtime.truffle.ast.io.jdbc.JdbcQuery;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.utils.RawSettings;

public class JdbcQueryComputeNext {

  private final LocationObject dbLocation;
  private final String query;
  private final RawSettings rawSettings;
  private final RootCallTarget rowParserCallTarget;
  private final JdbcExceptionHandler exceptionHandler;

  private JdbcQuery rs = null;

  public JdbcQueryComputeNext(
      LocationObject dbLocation,
      String query,
      RawSettings rawSettings,
      RootCallTarget rowParserCallTarget,
      JdbcExceptionHandler exceptionHandler) {
    this.dbLocation = dbLocation;
    this.query = query;
    this.rawSettings = rawSettings;
    this.rowParserCallTarget = rowParserCallTarget;
    this.exceptionHandler = exceptionHandler;
  }

  public JdbcQuery getRs() {
    return rs;
  }

  public void init() {
    this.rs =
        new JdbcQuery(
            this.dbLocation,
            this.query,
            this.rawSettings,
            this.exceptionHandler);
  }

  public void close() {
    this.rs.close();
    this.rs = null;
  }

  public RootCallTarget getRowParserCallTarget() {
    return rowParserCallTarget;
  }
}
