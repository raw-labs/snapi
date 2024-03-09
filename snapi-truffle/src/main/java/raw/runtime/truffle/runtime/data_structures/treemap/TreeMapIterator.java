package raw.runtime.truffle.runtime.data_structures.treemap;

import static raw.runtime.truffle.runtime.data_structures.treemap.TreeMapStatic.predecessor;
import static raw.runtime.truffle.runtime.data_structures.treemap.TreeMapStatic.successor;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class TreeMapIterator {
  TreeMapObject tree;
  TreeMapNode next;
  TreeMapNode lastReturned;
  int expectedModCount;

  TreeMapIterator(TreeMapObject tree) {
    expectedModCount = tree.getModCount();
    lastReturned = null;
    next = tree.getFirstEntry();
    this.tree = tree;
  }

  public final boolean hasNext() {
    return next != null;
  }

  public final TreeMapNode nextNode() {
    TreeMapNode e = next;
    if (e == null) throw new NoSuchElementException();
    if (tree.getModCount() != expectedModCount) throw new ConcurrentModificationException();
    next = successor(e);
    lastReturned = e;
    return e;
  }

  public final TreeMapNode nextKey() {
    TreeMapNode e = next;
    if (e == null) throw new NoSuchElementException();
    if (tree.getModCount() != expectedModCount) throw new ConcurrentModificationException();
    next = successor(e);
    lastReturned = e;
    return e;
  }

  final TreeMapNode prevEntry() {
    TreeMapNode e = next;
    if (e == null) throw new NoSuchElementException();
    if (tree.getModCount() != expectedModCount) throw new ConcurrentModificationException();
    next = predecessor(e);
    lastReturned = e;
    return e;
  }

  public void remove() {
    if (lastReturned == null) throw new IllegalStateException();
    if (tree.getModCount() != expectedModCount) throw new ConcurrentModificationException();
    // deleted entries are replaced by their successors
    if (lastReturned.left != null && lastReturned.right != null) next = lastReturned;
    tree.deleteEntry(lastReturned);
    expectedModCount = tree.getModCount();
    lastReturned = null;
  }
}
