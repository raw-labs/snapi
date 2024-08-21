/*
 * Copyright 2024 RAW Labs S.A.
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

import com.oracle.truffle.api.nodes.Node;
import java.util.ArrayList;
import java.util.List;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.record.RecordNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.record.RecordNodesFactory;

public class RecordStaticInitializers {

  public static RawLanguage getCachedLanguage(Node node) {
    return RawLanguage.get(node);
  }

  public static boolean hasDuplicateKeys(Object[] keys) {
    List<Object> list = new ArrayList<>(List.of(keys));
    return list.size() != list.stream().distinct().count();
  }

  public static RecordNodes.AddPropNode[] getAddPropNode(int size) {
    RecordNodes.AddPropNode[] result = new RecordNodes.AddPropNode[size];
    for (int i = 0; i < result.length; i++) {
      result[i] = RecordNodesFactory.AddPropNodeGen.create();
    }
    return result;
  }

  public static RecordNodes.AddPropNode[] getAddPropNodePlusOne(int size) {
    return getAddPropNode(size + 1);
  }

  public static RecordNodes.GetValueNode[] getValueNode(int size) {
    RecordNodes.GetValueNode[] result = new RecordNodes.GetValueNode[size];
    for (int i = 0; i < result.length; i++) {
      result[i] = RecordNodesFactory.GetValueNodeGen.create();
    }
    return result;
  }
}
