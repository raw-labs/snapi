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
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.record.*;

@NodeInfo(shortName = "Jdbc.RecordRead")
public class RecordReadJdbcQuery extends ExpressionNode {
  @Children private DirectCallNode[] childDirectCalls;
  private final RecordShapeWithFields shapeWithFields;

  private final int length;

  public RecordReadJdbcQuery(
      ProgramExpressionNode[] columnParsers, RecordShapeWithFields shapeWithFields) {
    this.childDirectCalls = new DirectCallNode[columnParsers.length];
    for (int i = 0; i < columnParsers.length; i++) {
      this.childDirectCalls[i] = DirectCallNode.create(columnParsers[i].getCallTarget());
    }
    this.shapeWithFields = shapeWithFields;
    this.length = columnParsers.length;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JdbcQuery rs = (JdbcQuery) args[0];
    StaticObjectRecord result = shapeWithFields.getShape().getFactory().create(shapeWithFields);
    for (int i = 0; i < length; i++) {
      Object value = childDirectCalls[i].call(rs);
      shapeWithFields.fields[i].setObject(result, value);
    }
    return result;
  }
}
