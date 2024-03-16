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

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import java.util.List;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.record.RecordNodes;

@NodeInfo(shortName = "Record.AddField")
@NodeChild("inRecordNode")
@NodeChild("keyNode")
@NodeChild("valueNode")
@ImportStatic(RecordStaticInitializers.class)
public abstract class RecordAddFieldNode extends ExpressionNode {

  public static boolean hasDuplicateKeysWithKey(Object[] keys, Object key) {
    List<Object> list = new ArrayList<>(List.of(keys));
    list.add(key);
    return list.size() != list.stream().distinct().count();
  }

  @Specialization
  protected static Object doAddField(
      Object rec,
      String newKey,
      Object newValue,
      @Bind("$node") Node thisNode,
      @Cached(value = "getCachedLanguage(thisNode)", neverDefault = true) RawLanguage lang,
      @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode,
      @Cached(value = "getKeysNode.execute(thisNode, rec)", neverDefault = true, dimensions = 1)
          Object[] objKeys,
      @Cached(value = "hasDuplicateKeysWithKey(objKeys, newKey)", neverDefault = false)
          boolean hasDuplicateKeys,
      @Cached(value = "getValueNode(objKeys.length)", neverDefault = true)
          RecordNodes.GetValueNode[] getValueNode,
      @Cached(value = "getAddPropNodePlusOne(objKeys.length)", neverDefault = true)
          RecordNodes.AddPropNode[] addPropNode) {

    Object result;
    if (hasDuplicateKeys) {
      result = lang.createDuplicateKeyRecord();
    } else {
      result = lang.createPureRecord();
    }

    for (int i = 0; i < objKeys.length; i++) {
      addPropNode[i].execute(
          thisNode,
          result,
          objKeys[i],
          getValueNode[i].execute(thisNode, rec, objKeys[i]),
          hasDuplicateKeys);
    }

    addPropNode[objKeys.length].execute(thisNode, result, newKey, newValue, hasDuplicateKeys);
    return result;
  }
}
