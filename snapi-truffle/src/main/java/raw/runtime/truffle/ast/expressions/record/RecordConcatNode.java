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
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.Vector;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.record.ConcatRecord;
import raw.runtime.truffle.runtime.record.RecordNodes;

@NodeInfo(shortName = "Record.Concat")
@NodeChild("record1")
@NodeChild("record2")
public abstract class RecordConcatNode extends ExpressionNode {

  @Specialization
  protected Object doConcat(
      Object rec1, Object rec2, @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode) {

    Vector<String> keys1 = new Vector<>();
    Vector<String> keys2 = new Vector<>();
    Object[] objKeys1 = getKeysNode.execute(this, rec1);
    Object[] objKeys2 = getKeysNode.execute(this, rec2);

    for (Object obj : objKeys1) {
      keys1.add(obj.toString());
    }

    for (Object obj : objKeys2) {
      keys2.add(obj.toString());
    }

    return new ConcatRecord(rec1, rec2, keys1, keys2);
  }
}
