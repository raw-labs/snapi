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

package raw.runtime.truffle.runtime.data_structures.treemap;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.ArrayList;
import raw.runtime.truffle.PropertyType;
import raw.runtime.truffle.runtime.operators.OperatorNodes;

public class TreeMapNodes {

  @NodeInfo(shortName = "TreeMap.GetOrCreate")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(raw.runtime.truffle.PropertyType.class)
  public abstract static class TreeMapGetOrCreate extends Node {

    public abstract Object execute(Node node, TreeMapObject mapObject, Object key);

    @Specialization
    static Object exec(
        Node node,
        TreeMapObject treeMapObject,
        Object key,
        @Bind("$node") Node thisNode,
        @Cached OperatorNodes.CompareNode compareNode) {
      int cmp = 0;
      TreeMapNode parent = null;
      TreeMapNode t = treeMapObject.getRoot();
      while (t != null) { // if `t` is null, we don't enter the loop, `parent` remains null.
        parent = t;
        cmp = compareNode.execute(thisNode, key, t.key);
        if (cmp < 0) t = t.left;
        else if (cmp > 0) t = t.right;
        else {
          return t.value;
        }
      }
      ArrayList<Object> result = new ArrayList<>();
      if (parent != null) {
        // we entered the loop and exited because `t` is null
        treeMapObject.addEntry(key, result, parent, cmp < 0);
      } else {
        // we didn't enter the loop, we insert in the root
        treeMapObject.addEntryToEmptyMap(key, result);
      }
      return result;
    }
  }

  @NodeInfo(shortName = "TreeMap.GetOrCreateArrayKeys")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class TreeMapGetOrCreateArrayKeysNode extends Node {

    public abstract Object execute(Node node, TreeMapObject mapObject, Object key, int[] orderings);

    @Specialization
    static Object exec(
        Node node,
        TreeMapObject treeMapObject,
        Object key,
        int[] orderings,
        @Bind("$node") Node thisNode,
        @Cached OperatorNodes.CompareKeys compareNode) {
      int cmp = 0;
      TreeMapNode parent = null;
      TreeMapNode t = treeMapObject.getRoot();
      while (t != null) { // if `t` is null, we don't enter the loop, `parent` remains null.
        parent = t;
        cmp = compareNode.execute(thisNode, (Object[]) key, (Object[]) t.key, orderings);
        if (cmp < 0) t = t.left;
        else if (cmp > 0) t = t.right;
        else {
          return t.value;
        }
      }
      ArrayList<Object> result = new ArrayList<>();
      if (parent != null) {
        // we entered the loop and exited because `t` is null
        treeMapObject.addEntry(key, result, parent, cmp < 0);
      } else {
        // we didn't enter the loop, we insert in the root
        treeMapObject.addEntryToEmptyMap(key, result);
      }
      return result;
    }
  }
}
