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

package raw.runtime.truffle.ast.expressions.record;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.record.*;

@NodeInfo(shortName = "Record.Build")
public class RecordBuildNode extends ExpressionNode {

  @Children private final ExpressionNode[] values;

  private final RecordShapeWithFields shapeWithFields;

  public RecordBuildNode(ExpressionNode[] values, RecordShapeWithFields shapeWithFields) {
    CompilerAsserts.compilationConstant(values.length);
    this.values = values;
    this.shapeWithFields = shapeWithFields;
  }

  @ExplodeLoop
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    StaticObjectRecord record = shapeWithFields.getShape().getFactory().create(shapeWithFields);

    for (int i = 0; i < values.length; i++) {
      shapeWithFields.getFieldByIndex(i).setObject(record, values[i].executeGeneric(frame));
    }

    return record;
  }
}
