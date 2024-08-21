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

package com.rawlabs.snapi.truffle.runtime.runtime.data_structures.treemap;

import static com.rawlabs.snapi.truffle.runtime.runtime.data_structures.treemap.TreeMapStatic.BLACK;

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
