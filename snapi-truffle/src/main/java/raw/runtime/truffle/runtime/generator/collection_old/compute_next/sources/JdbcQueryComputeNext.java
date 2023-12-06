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

package raw.runtime.truffle.runtime.generator.collection_old.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.truffle.ast.io.jdbc.JdbcQuery;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import raw.runtime.truffle.runtime.generator.collection_old.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.SourceContext;

@ExportLibrary(ComputeNextLibrary.class)
public class JdbcQueryComputeNext {

  private final LocationObject dbLocation;
  private final String query;
  protected final RootCallTarget rowParserCallTarget;
  private final SourceContext context;

  private JdbcQuery rs = null;
  private final JdbcExceptionHandler exceptionHandler;

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

  @ExportMessage
  void init() {
    rs = new JdbcQuery(dbLocation.getLocationDescription(), query, context, exceptionHandler);
  }

  @ExportMessage
  void close() {
    if (rs != null) {
      rs.close();
      rs = null;
    }
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  Object computeNext(
      @Cached(value = "this.rowParserCallTarget", allowUncached = true, neverDefault = true)
          RootCallTarget cachedTarget,
      @Cached(value = "create(cachedTarget)", allowUncached = true) DirectCallNode rowParser) {
    boolean ok = rs.next();
    if (ok) {
      return rowParser.call(rs);
    } else {
      throw new BreakException();
    }
  }
}
