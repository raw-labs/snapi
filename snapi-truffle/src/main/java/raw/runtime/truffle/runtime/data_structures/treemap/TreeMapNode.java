package raw.runtime.truffle.runtime.data_structures.treemap;

import static raw.runtime.truffle.runtime.data_structures.treemap.TreeMapStatic.BLACK;

public class TreeMapNode {
  Object key;
  Object value;
  TreeMapNode left;
  TreeMapNode right;
  TreeMapNode parent;
  boolean color = BLACK;

  TreeMapNode(Object key, Object value, TreeMapNode parent) {
    this.key = key;
    this.value = value;
    this.parent = parent;
  }

  public Object getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

  public Object setValue(Object value) {
    Object oldValue = this.value;
    this.value = value;
    return oldValue;
  }
}
