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

package com.rawlabs.snapi.truffle.ast.expressions.record;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import java.util.ArrayList;
import java.util.List;

@NodeInfo(shortName = "Record.RemoveField")
@NodeChild("record")
@NodeChild("dropKey")
@ImportStatic(RecordStaticInitializers.class)
public abstract class RecordRemoveFieldNode extends ExpressionNode {

  public static boolean hasDuplicateKeysWithoutKey(Object[] keys, Object key) {
    List<Object> list = new ArrayList<>(List.of(keys));
    list.remove(key);
    return list.size() != list.stream().distinct().count();
  }

  @Specialization
  protected static Object doRemoveField(
      Object record,
      Object dropKey,
      @Bind("$node") Node thisNode,
      @Cached(value = "getCachedLanguage(thisNode)", neverDefault = true) Rql2Language lang,
      @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode,
      @Cached(value = "getKeysNode.execute(thisNode, record)", neverDefault = true, dimensions = 1)
          Object[] objKeys,
      @Cached(value = "hasDuplicateKeysWithoutKey(objKeys, dropKey)", neverDefault = false)
          boolean hasDuplicateKeys,
      @Cached(value = "getValueNode(objKeys.length)", neverDefault = true)
          RecordNodes.GetValueNode[] getValueNode,
      @Cached(value = "getAddPropNode(objKeys.length)", neverDefault = true)
          RecordNodes.AddPropNode[] addPropNode) {
    Object result;
    if (hasDuplicateKeys) {
      result = lang.createDuplicateKeyRecord();
    } else {
      result = lang.createPureRecord();
    }

    for (int i = 0; i < objKeys.length; i++) {
      if (!objKeys[i].equals(dropKey)) {
        addPropNode[i].execute(
            thisNode,
            result,
            objKeys[i],
            getValueNode[i].execute(thisNode, record, objKeys[i]),
            hasDuplicateKeys);
      }
    }
    return result;
  }
}
