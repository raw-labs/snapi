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
      @Cached(inline = true) RecordNodes.CloneNode cloneNode,
      @Cached(inline = true) RecordNodes.AddPropNode addPropNode) {
    Object clone = cloneNode.execute(this, rec);
    return addPropNode.execute(this, clone, newKey, newValue);
  }
}
