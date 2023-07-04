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

package raw.runtime.truffle.ast.jdbc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

@NodeInfo(shortName = "TryableParsePostgreSQL")
public class TryableReadJdbcQuery extends ExpressionNode {

  @Child
  private DirectCallNode innerParse;
  private final int idx;

  public TryableReadJdbcQuery(ProgramExpressionNode innerParse, int idx) {
    this.innerParse = DirectCallNode.create(innerParse.getCallTarget());
    this.idx = idx;
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JdbcQuery rs = (JdbcQuery) args[0];
    try {
      Object value = innerParse.call(rs, idx);
      return ObjectTryable.BuildSuccess(value);
    } catch (RawTruffleRuntimeException e) {
      return ObjectTryable.BuildFailure(e.getMessage());
    }
  }

}
