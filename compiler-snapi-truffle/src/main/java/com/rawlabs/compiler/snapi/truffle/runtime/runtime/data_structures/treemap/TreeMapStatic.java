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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.data_structures.treemap;

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

  static TreeMapNode predecessor(TreeMapNode t) {
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
