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

package raw.runtime.truffle.runtime.data_structures.treemap;

import static raw.runtime.truffle.runtime.data_structures.treemap.TreeMapStatic.predecessor;
import static raw.runtime.truffle.runtime.data_structures.treemap.TreeMapStatic.successor;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class TreeMapIterator {
  private final TreeMapObject tree;
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

  public final Object nextKey() {
    TreeMapNode e = next;
    if (e == null) throw new NoSuchElementException();
    if (tree.getModCount() != expectedModCount) throw new ConcurrentModificationException();
    next = successor(e);
    lastReturned = e;
    return e.key;
  }

  final TreeMapNode prevNode() {
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
