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
import raw.runtime.truffle.runtime.record.RecordNodes;

@NodeInfo(shortName = "Record.Project")
@NodeChild("receiverNode")
@NodeChild("indexNode")
public abstract class RecordProjNode extends ExpressionNode {

  @Specialization
  protected Object readMember(
      Object record, String key, @Cached(inline = true) RecordNodes.GetValueNode getValueNode) {
    return getValueNode.execute(this, record, key);
  }

  @Specialization
  protected Object readMember(
      Object record,
      int index,
      @Cached(inline = true) RecordNodes.GetValueByIndexNode getValueByIndexNode) {
    return getValueByIndexNode.execute(this, record, index - 1);
  }
}
