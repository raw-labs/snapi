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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "Record.Concat")
@NodeChild("record1")
@NodeChild("record2")
public abstract class RecordConcatNode extends ExpressionNode {

  @Child
  RecordNodes.WriteIndexNode writeIndexNode = insert(RecordNodesFactory.WriteIndexNodeGen.create());

  @Child
  RecordNodes.ReadIndexNode readIndexNode = insert(RecordNodesFactory.ReadIndexNodeGen.create());

  @Specialization
  @ExplodeLoop
  protected Object doConcat(Object rec1, Object rec2) {
    RecordObject newRecord = RawLanguage.get(this).createRecord();
    RecordObject record1 = (RecordObject) rec1;
    RecordObject record2 = (RecordObject) rec2;
    String[] keys1 = record1.keys();
    String[] keys2 = record2.keys();
    int length1 = keys1.length;
    int length2 = keys2.length;
    String member;
    for (int i = 0; i < length1; i++) {
      member = keys1[i];
      writeIndexNode.execute(newRecord, i, member, readIndexNode.execute(record1, i));
    }
    for (int i = 0; i < length2; i++) {
      member = keys2[i];
      writeIndexNode.execute(newRecord, i + length1, member, readIndexNode.execute(record2, i));
    }
    return newRecord;
  }
}
