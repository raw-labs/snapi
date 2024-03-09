package raw.runtime.truffle.runtime.data_structures.treemap;

public class TreeMapStatic {
  public static final boolean RED = false;
  public static final boolean BLACK = true;

  public static boolean colorOf(TreeMapNode p) {
    return (p == null ? BLACK : p.color);
  }

  public static TreeMapNode parentOf(TreeMapNode p) {
    return (p == null ? null : p.parent);
  }

  public static void setColor(TreeMapNode p, boolean c) {
    if (p != null) p.color = c;
  }

  public static TreeMapNode leftOf(TreeMapNode p) {
    return (p == null) ? null : p.left;
  }

  public static TreeMapNode rightOf(TreeMapNode p) {
    return (p == null) ? null : p.right;
  }

  static <K, V> TreeMapNode predecessor(TreeMapNode t) {
    if (t == null) return null;
    else if (t.left != null) {
      TreeMapNode p = t.left;
      while (p.right != null) p = p.right;
      return p;
    } else {
      TreeMapNode p = t.parent;
      TreeMapNode ch = t;
      while (p != null && ch == p.left) {
        ch = p;
        p = p.parent;
      }
      return p;
    }
  }

  static TreeMapNode successor(TreeMapNode t) {
    if (t == null) return null;
    else if (t.right != null) {
      TreeMapNode p = t.right;
      while (p.left != null) p = p.left;
      return p;
    } else {
      TreeMapNode p = t.parent;
      TreeMapNode ch = t;
      while (p != null && ch == p.right) {
        ch = p;
        p = p.parent;
      }
      return p;
    }
  }
}
