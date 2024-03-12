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

@NodeInfo(shortName = "Record.Concat")
@NodeChild("record1")
@NodeChild("record2")
@NodeField(name = "shapeWithFields", type = RecordShapeWithFields.class)
public abstract class RecordConcatNode extends ExpressionNode {

  @Idempotent
  protected abstract RecordShapeWithFields getShapeWithFields();

  @Specialization
  protected Object doConcat(
      Object rec1,
      Object rec2,
      @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode,
      @Cached(inline = true) RecordNodes.GetValueNode getValueNode) {

    Object[] objKeys1 = getKeysNode.execute(this, rec1);
    Object[] objKeys2 = getKeysNode.execute(this, rec2);

    StaticObjectRecord record =
        getShapeWithFields().getShape().getFactory().create(getShapeWithFields());

    for (int i = 0; i < objKeys1.length; i++) {
      getShapeWithFields()
          .getFieldByIndex(i)
          .setObject(record, getValueNode.execute(this, rec1, (String) objKeys1[i]));
    }

    // with offset of the first
    for (int i = 0; i < objKeys2.length; i++) {
      getShapeWithFields()
          .getFieldByIndex(objKeys1.length + i)
          .setObject(record, getValueNode.execute(this, rec2, (String) objKeys2[i]));
    }

    return record;
  }
}
