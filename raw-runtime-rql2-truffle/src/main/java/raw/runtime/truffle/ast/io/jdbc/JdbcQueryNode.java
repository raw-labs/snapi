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

package raw.runtime.truffle.ast.io.jdbc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import raw.runtime.truffle.runtime.iterable.sources.JdbcQueryCollection;
import raw.runtime.truffle.runtime.primitives.LocationObject;

@NodeInfo(shortName = "Jdbc.Query")
public class JdbcQueryNode extends ExpressionNode {

  @Child
  private ExpressionNode locationExp;
  @Child
  private ExpressionNode queryExp;
  @Child
  private DirectCallNode makeRow;

  private final JdbcExceptionHandler exceptionHandler;

  public JdbcQueryNode(ExpressionNode locationExp, ExpressionNode queryExp, ProgramExpressionNode rowReader, JdbcExceptionHandler exceptionHandler) {
    this.locationExp = locationExp;
    this.queryExp = queryExp;
    this.makeRow = DirectCallNode.create(rowReader.getCallTarget());
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    LocationObject dbLocation = (LocationObject) locationExp.executeGeneric(virtualFrame);
    String query = (String) this.queryExp.executeGeneric(virtualFrame);
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    return new JdbcQueryCollection(dbLocation, query, context, makeRow, exceptionHandler);
  }
}
