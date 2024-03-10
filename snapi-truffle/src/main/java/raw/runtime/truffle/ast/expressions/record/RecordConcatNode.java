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
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.record.RecordNodes;

@NodeInfo(shortName = "Record.Concat")
@NodeChild("record1")
@NodeChild("record2")
public abstract class RecordConcatNode extends ExpressionNode {

  @Specialization
  protected Object doConcat(
      Object rec1,
      Object rec2,
      @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode,
      @Cached(inline = true) RecordNodes.GetValueNode getValueNode,
      @Cached(inline = true) RecordNodes.AddPropNode addPropNode) {

    Object result = RawLanguage.get(this).createPureRecord();

    Object[] objKeys1 = getKeysNode.execute(this, rec1);
    Object[] objKeys2 = getKeysNode.execute(this, rec2);

    for (Object obj : objKeys1) {
      result =
          addPropNode.execute(
              this, result, (String) obj, getValueNode.execute(this, rec1, (String) obj));
    }

    for (Object obj : objKeys2) {
      result =
          addPropNode.execute(
              this, result, (String) obj, getValueNode.execute(this, rec2, (String) obj));
    }

    return result;
  }
}
