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

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;

@NodeInfo(shortName = "Record.Build")
public class RecordBuildNode extends ExpressionNode {

  @Children private final RecordNodes.AddPropNode[] addPropNode;

  @Children private final ExpressionNode[] elementNodes;

  private final RawLanguage language = RawLanguage.get(this);

  public RecordBuildNode(ExpressionNode[] elementsNodes) {
    CompilerAsserts.compilationConstant(elementsNodes.length);
    // elementsNodes is a an array of k1, v1, k2, v2, ..., kn, vn.
    assert elementsNodes.length % 2 == 0;
    this.elementNodes = elementsNodes;
    this.addPropNode = new RecordNodes.AddPropNode[elementsNodes.length / 2];
    for (int i = 0; i < addPropNode.length; i++) {
      this.addPropNode[i] = RecordNodesFactory.AddPropNodeGen.create();
    }
  }

  @ExplodeLoop
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object record = language.createPureRecord();
    for (int i = 0, j = 0; i < elementNodes.length; i += 2, j++) {
      // i jump by 2 because we have k1, v1, k2, v2, ..., kn, vn.
      Object key = elementNodes[i].executeGeneric(frame);
      Object value = elementNodes[i + 1].executeGeneric(frame);
      record = addPropNode[j].execute(this, record, key, value);
    }
    return record;
  }
}
