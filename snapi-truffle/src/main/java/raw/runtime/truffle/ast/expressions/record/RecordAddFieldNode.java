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

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "Record.AddField")
@NodeChild("inRecordNode")
@NodeChild("keyNode")
@NodeChild("valueNode")
public abstract class RecordAddFieldNode extends ExpressionNode {

  @Specialization
  protected Object doAddField(
      Object rec,
      String newKey,
      Object newValue,
      @Cached(inline = true) RecordNodes.WriteIndexNode writeIndexNode,
      @Cached(inline = true) RecordNodes.ReadIndexNode readIndexNode) {
    RecordObject record = (RecordObject) rec;
    RecordObject newRecord = RawLanguage.get(this).createRecord();
    String[] keys = record.keys();
    int length = keys.length;
    String member;
    for (int i = 0; i < length; i++) {
      member = keys[i];
      writeIndexNode.execute(this, newRecord, i, member, readIndexNode.execute(this, record, i));
    }
    writeIndexNode.execute(this, newRecord, length, newKey, newValue);
    return newRecord;
  }
}
