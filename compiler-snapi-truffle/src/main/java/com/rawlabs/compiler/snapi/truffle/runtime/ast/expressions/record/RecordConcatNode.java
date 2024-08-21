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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.record;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import java.util.List;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.record.RecordNodes;

@NodeInfo(shortName = "Record.Concat")
@NodeChild("record1")
@NodeChild("record2")
@ImportStatic(RecordStaticInitializers.class)
public abstract class RecordConcatNode extends ExpressionNode {

  public static boolean hasDuplicateKeysBetween(Object[] keys1, Object[] keys2) {
    List<Object> list = new ArrayList<>(List.of(keys1));
    list.addAll(List.of(keys2));
    return list.size() != list.stream().distinct().count();
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
          RecordNodes.AddPropNode[] addPropNode2,
      @Cached(value = "hasDuplicateKeysBetween(objKeys1, objKeys2)", neverDefault = false)
          boolean hasDuplicateKeys) {

    Object result;
    if (hasDuplicateKeys) {
      result = lang.createDuplicateKeyRecord();
    } else {
      result = lang.createPureRecord();
    }

    for (int i = 0; i < objKeys1.length; i++) {
      addPropNode1[i].execute(
          thisNode,
          result,
          objKeys1[i],
          getValueNode1[i].execute(thisNode, rec1, objKeys1[i]),
          hasDuplicateKeys);
    }

    for (int i = 0; i < objKeys2.length; i++) {
      addPropNode2[i].execute(
          thisNode,
          result,
          objKeys2[i],
          getValueNode2[i].execute(thisNode, rec2, objKeys2[i]),
          hasDuplicateKeys);
    }

    return result;
  }
}
