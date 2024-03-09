package raw.runtime.truffle.runtime.data_structures.treemap;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.runtime.data_structures.PropertyType;
import raw.runtime.truffle.runtime.operators.OperatorNodes;

public class TreeMapNodes {
  @NodeInfo(shortName = "TreeMap.Put")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class TreeMapPutNode extends Node {

    public abstract void execute(Node node, TreeMapObject mapObject, Object key, Object value);

    @Specialization
    static void exec(
        Node node,
        TreeMapObject treeMapObject,
        Object key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached OperatorNodes.CompareNode compareNode) {
      TreeMapNode t = treeMapObject.getRoot();
      if (t == null) {
        treeMapObject.addEntryToEmptyMap(key, value);
        return;
      }
      int cmp;
      TreeMapNode parent;
      // split comparator and comparable paths
      do {
        parent = t;
        cmp = compareNode.execute(thisNode, key, t.key);
        if (cmp < 0) t = t.left;
        else if (cmp > 0) t = t.right;
        else {
          t.value = value;
          return;
        }
      } while (t != null);
      treeMapObject.addEntry(key, value, parent, cmp < 0);
    }
  }

  @NodeInfo(shortName = "TreeMap.Get")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(PropertyType.class)
  public abstract static class TreeMapGetNode extends Node {

    public abstract Object execute(Node node, TreeMapObject mapObject, Object key);

    @Specialization
    static Object exec(
        Node node,
        TreeMapObject treeMapObject,
        Object key,
        @Bind("$node") Node thisNode,
        @Cached OperatorNodes.CompareNode compareNode) {
      TreeMapNode p = treeMapObject.getRoot();
      while (p != null) {
        int cmp = compareNode.execute(thisNode, key, p.key);
        if (cmp < 0) p = p.left;
        else if (cmp > 0) p = p.right;
        else return p;
      }
      return null;
    }
  }
}
