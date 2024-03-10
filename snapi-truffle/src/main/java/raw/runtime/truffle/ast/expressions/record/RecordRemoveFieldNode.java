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

@NodeInfo(shortName = "Record.RemoveField")
@NodeChild("record")
@NodeChild("dropKey")
public abstract class RecordRemoveFieldNode extends ExpressionNode {

  @Specialization
  protected Object doRemoveField(
      Object record,
      String dropKey,
      @Cached(inline = true) RecordNodes.RemovePropNode removePropNode) {
    return removePropNode.execute(this, record, dropKey);
  }
}
