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

import static raw.runtime.truffle.runtime.data_structures.treemap.TreeMapStatic.*;

import com.oracle.truffle.api.interop.TruffleObject;

public class TreeMapObject implements TruffleObject {

  private TreeMapNode root;
  private int size = 0;
  private int modCount = 0;

  public TreeMapObject() {}

  public TreeMapNode getRoot() {
    return root;
  }

  public int getSize() {
    return size;
  }

  public int getModCount() {
    return modCount;
  }

  public void setRoot(TreeMapNode root) {
    this.root = root;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setModCount(int modCount) {
    this.modCount = modCount;
  }

  public void addEntryToEmptyMap(Object key, Object value) {
    root = new TreeMapNode(key, value, null);
    size = 1;
    modCount++;
  }

  public void addEntry(Object key, Object value, TreeMapNode parent, boolean addToLeft) {
    TreeMapNode e = new TreeMapNode(key, value, parent);
    if (addToLeft) parent.left = e;
    else parent.right = e;
    fixAfterInsertion(e);
    size++;
    modCount++;
  }

  private void rotateLeft(TreeMapNode p) {
    if (p != null) {
      TreeMapNode r = p.right;
      p.right = r.left;
      if (r.left != null) r.left.parent = p;
      r.parent = p.parent;
      if (p.parent == null) root = r;
      else if (p.parent.left == p) p.parent.left = r;
      else p.parent.right = r;
      r.left = p;
      p.parent = r;
    }
  }

  /** From CLR */
  private void rotateRight(TreeMapNode p) {
    if (p != null) {
      TreeMapNode l = p.left;
      p.left = l.right;
      if (l.right != null) l.right.parent = p;
      l.parent = p.parent;
      if (p.parent == null) root = l;
      else if (p.parent.right == p) p.parent.right = l;
      else p.parent.left = l;
      l.right = p;
      p.parent = l;
    }
  }

  private void fixAfterInsertion(TreeMapNode x) {
    x.color = RED;

    while (x != null && x != root && x.parent.color == RED) {
      if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
        TreeMapNode y = rightOf(parentOf(parentOf(x)));
        if (colorOf(y) == RED) {
          setColor(parentOf(x), BLACK);
          setColor(y, BLACK);
          setColor(parentOf(parentOf(x)), RED);
          x = parentOf(parentOf(x));
        } else {
          if (x == rightOf(parentOf(x))) {
            x = parentOf(x);
            rotateLeft(x);
          }
          setColor(parentOf(x), BLACK);
          setColor(parentOf(parentOf(x)), RED);
          rotateRight(parentOf(parentOf(x)));
        }
      } else {
        TreeMapNode y = leftOf(parentOf(parentOf(x)));
        if (colorOf(y) == RED) {
          setColor(parentOf(x), BLACK);
          setColor(y, BLACK);
          setColor(parentOf(parentOf(x)), RED);
          x = parentOf(parentOf(x));
        } else {
          if (x == leftOf(parentOf(x))) {
            x = parentOf(x);
            rotateRight(x);
          }
          setColor(parentOf(x), BLACK);
          setColor(parentOf(parentOf(x)), RED);
          rotateLeft(parentOf(parentOf(x)));
        }
      }
    }
    root.color = BLACK;
  }

  public void clear() {
    modCount++;
    size = 0;
    root = null;
  }

  final TreeMapNode getFirstEntry() {
    TreeMapNode p = root;
    if (p != null) while (p.left != null) p = p.left;
    return p;
  }

  public void deleteEntry(TreeMapNode p) {
    modCount++;
    size--;

    // If strictly internal, copy successor's element to p and then make p
    // point to successor.
    if (p.left != null && p.right != null) {
      TreeMapNode s = successor(p);
      p.key = s.key;
      p.value = s.value;
      p = s;
    } // p has 2 children

    // Start fixup at replacement node, if it exists.
    TreeMapNode replacement = (p.left != null ? p.left : p.right);

    if (replacement != null) {
      // Link replacement to parent
      replacement.parent = p.parent;
      if (p.parent == null) root = replacement;
      else if (p == p.parent.left) p.parent.left = replacement;
      else p.parent.right = replacement;

      // Null out links so they are OK to use by fixAfterDeletion.
      p.left = p.right = p.parent = null;

      // Fix replacement
      if (p.color == BLACK) fixAfterDeletion(replacement);
    } else if (p.parent == null) { // return if we are the only node.
      root = null;
    } else { //  No children. Use self as phantom replacement and unlink.
      if (p.color == BLACK) fixAfterDeletion(p);

      if (p.parent != null) {
        if (p == p.parent.left) p.parent.left = null;
        else if (p == p.parent.right) p.parent.right = null;
        p.parent = null;
      }
    }
  }

  /** From CLR */
  private void fixAfterDeletion(TreeMapNode x) {
    while (x != root && colorOf(x) == BLACK) {
      if (x == leftOf(parentOf(x))) {
        TreeMapNode sib = rightOf(parentOf(x));

        if (colorOf(sib) == RED) {
          setColor(sib, BLACK);
          setColor(parentOf(x), RED);
          rotateLeft(parentOf(x));
          sib = rightOf(parentOf(x));
        }

        if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
          setColor(sib, RED);
          x = parentOf(x);
        } else {
          if (colorOf(rightOf(sib)) == BLACK) {
            setColor(leftOf(sib), BLACK);
            setColor(sib, RED);
            rotateRight(sib);
            sib = rightOf(parentOf(x));
          }
          setColor(sib, colorOf(parentOf(x)));
          setColor(parentOf(x), BLACK);
          setColor(rightOf(sib), BLACK);
          rotateLeft(parentOf(x));
          x = root;
        }
      } else { // symmetric
        TreeMapNode sib = leftOf(parentOf(x));

        if (colorOf(sib) == RED) {
          setColor(sib, BLACK);
          setColor(parentOf(x), RED);
          rotateRight(parentOf(x));
          sib = leftOf(parentOf(x));
        }

        if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
          setColor(sib, RED);
          x = parentOf(x);
        } else {
          if (colorOf(leftOf(sib)) == BLACK) {
            setColor(rightOf(sib), BLACK);
            setColor(sib, RED);
            rotateLeft(sib);
            sib = leftOf(parentOf(x));
          }
          setColor(sib, colorOf(parentOf(x)));
          setColor(parentOf(x), BLACK);
          setColor(leftOf(sib), BLACK);
          rotateRight(parentOf(x));
          x = root;
        }
      }
    }

    setColor(x, BLACK);
  }

  public TreeMapIterator iterator() {
    return new TreeMapIterator(this);
  }
}
