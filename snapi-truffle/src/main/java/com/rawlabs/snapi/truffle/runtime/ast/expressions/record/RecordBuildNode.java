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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.record;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.record.RecordNodesFactory;
import java.util.List;

@NodeInfo(shortName = "Record.Build")
public class RecordBuildNode extends ExpressionNode {

  @Children private final RecordNodes.AddPropNode[] addPropNode;

  @Children private final ExpressionNode[] elementNodes;

  private final String[] keys;

  private final boolean hasDuplicateKeys;

  private final RawLanguage language = RawLanguage.get(this);

  public RecordBuildNode(ExpressionNode[] elementsNodes, String[] keys) {
    CompilerAsserts.compilationConstant(elementsNodes.length);
    this.elementNodes = elementsNodes;
    List<String> listOfKeys = List.of(keys);
    hasDuplicateKeys = listOfKeys.size() != listOfKeys.stream().distinct().count();
    this.keys = keys;
    this.addPropNode = new RecordNodes.AddPropNode[elementsNodes.length];
    for (int i = 0; i < addPropNode.length; i++) {
      this.addPropNode[i] = RecordNodesFactory.AddPropNodeGen.create();
    }
  }

  @ExplodeLoop
  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Object record;
    if (hasDuplicateKeys) {
      record = language.createDuplicateKeyRecord();
    } else {
      record = language.createPureRecord();
    }
    for (int i = 0; i < elementNodes.length; i++) {
      Object value = elementNodes[i].executeGeneric(frame);
      addPropNode[i].execute(this, record, keys[i], value, hasDuplicateKeys);
    }
    return record;
  }
}
