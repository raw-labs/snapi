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

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordShapeWithFields;
import raw.runtime.truffle.runtime.record.StaticObjectRecord;

@NodeInfo(shortName = "Record.RemoveField")
@NodeChild("record")
@NodeField(name = "shapeWithFields", type = RecordShapeWithFields.class)
public abstract class RecordRemoveFieldNode extends ExpressionNode {

  @Idempotent
  protected abstract RecordShapeWithFields getShapeWithFields();

  @Specialization
  protected Object doRemoveField(
      Object record, @Cached(inline = true) RecordNodes.GetValueNode getValueNode) {

    StaticObjectRecord newRecord =
        getShapeWithFields().getShape().getFactory().create(getShapeWithFields());

    for (int i = 0; i < getShapeWithFields().fields.length; i++) {
      getShapeWithFields()
          .getFields()[i]
          .setObject(
              newRecord,
              getValueNode.execute(this, record, getShapeWithFields().getDistinctKeys()[i]));
    }

    return newRecord;
  }
}
