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
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
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
  @ExplodeLoop
  protected Object doConcat(StaticObjectRecord rec1, StaticObjectRecord rec2) {
    StaticObjectRecord record =
        getShapeWithFields().getShape().getFactory().create(getShapeWithFields());

    int len1 = rec1.__shapeRef__.fields.length;
    int len2 = rec2.__shapeRef__.fields.length;

    for (int i = 0; i < len1; i++) {
      getShapeWithFields()
          .getFieldByIndex(i)
          .setObject(record, rec1.__shapeRef__.getFieldByIndex(i).getObject(rec1));
    }

    // with offset of the first
    for (int i = 0; i < len2; i++) {
      getShapeWithFields()
          .getFieldByIndex(len1 + i)
          .setObject(record, rec2.__shapeRef__.getFieldByIndex(i).getObject(rec2));
    }

    return record;
  }
}
