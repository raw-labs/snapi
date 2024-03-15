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

import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.runtime.record.RecordNodesFactory;

@NodeInfo(shortName = "Record.Concat")
@NodeChild("record1")
@NodeChild("record2")
public abstract class RecordConcatNode extends ExpressionNode {

  public static RawLanguage getCachedLanguage(Node node) {
    return RawLanguage.get(node);
  }

  public static RecordNodes.AddPropNode[] getAddPropNode(int size) {
    RecordNodes.AddPropNode[] result = new RecordNodes.AddPropNode[size];
    for (int i = 0; i < result.length; i++) {
      result[i] = RecordNodesFactory.AddPropNodeGen.create();
    }
    return result;
  }

  public static RecordNodes.GetValueNode[] getValueNode(int size) {
    RecordNodes.GetValueNode[] result = new RecordNodes.GetValueNode[size];
    for (int i = 0; i < result.length; i++) {
      result[i] = RecordNodesFactory.GetValueNodeGen.create();
    }
    return result;
  }

  @Specialization
  @ExplodeLoop
  protected static Object doConcat(
      Object rec1,
      Object rec2,
      @Bind("$node") Node thisNode,
      @Cached(value = "getCachedLanguage(thisNode)", neverDefault = true) RawLanguage lang,
      @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode1,
      @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode2,
      @Cached(value = "getKeysNode1.execute(thisNode, rec1)", neverDefault = true, dimensions = 1)
          Object[] objKeys1,
      @Cached(value = "getKeysNode2.execute(thisNode, rec2)", neverDefault = true, dimensions = 1)
          Object[] objKeys2,
      @Cached(value = "getValueNode(objKeys1.length)", neverDefault = true)
          RecordNodes.GetValueNode[] getValueNode1,
      @Cached(value = "getValueNode(objKeys2.length)", neverDefault = true)
          RecordNodes.GetValueNode[] getValueNode2,
      @Cached(value = "getAddPropNode(objKeys1.length)", neverDefault = true)
          RecordNodes.AddPropNode[] addPropNode1,
      @Cached(value = "getAddPropNode(objKeys2.length)", neverDefault = true)
          RecordNodes.AddPropNode[] addPropNode2) {

    Object result = lang.createPureRecord();

    for (int i = 0; i < objKeys1.length; i++) {
      result =
          addPropNode1[i].execute(
              thisNode, result, objKeys1[i], getValueNode1[i].execute(thisNode, rec1, objKeys1[i]));
    }

    for (int i = 0; i < objKeys2.length; i++) {
      result =
          addPropNode2[i].execute(
              thisNode, result, objKeys2[i], getValueNode2[i].execute(thisNode, rec2, objKeys2[i]));
    }

    return result;
  }
}
