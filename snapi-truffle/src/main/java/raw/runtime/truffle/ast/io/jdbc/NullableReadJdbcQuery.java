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
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.primitives.NullObject;

@NodeInfo(shortName = "NullableReadJdbcQuery")
public class NullableReadJdbcQuery extends ExpressionNode {

  @Child private DirectCallNode innerParse;
  private final int index;
  private final String colName;

  public NullableReadJdbcQuery(ProgramExpressionNode innerParse, String colName, int idx) {
    this.innerParse = DirectCallNode.create(innerParse.getCallTarget());
    this.colName = colName;
    this.index = idx;
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JdbcQuery rs = (JdbcQuery) args[0];
    if (rs.isNull(index, colName, this)) return NullObject.INSTANCE;
    else return innerParse.call(rs, index, colName);
  }
}
