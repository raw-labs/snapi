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

package com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.input_buffer;

import com.esotericsoftware.kryo.io.Input;
import com.oracle.truffle.api.CompilerDirectives;
import com.rawlabs.snapi.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;

public class GroupByInputBuffer {
  private final OffHeapGroupByKey offHeapGroupByKey;
  private final Input input;
  private Object key;
  private int itemsLeft;

  public GroupByInputBuffer(OffHeapGroupByKey offHeapGroupByKey, Input input) {
    this.input = input;
    this.key = null;
    this.itemsLeft = 0;
    this.offHeapGroupByKey = offHeapGroupByKey;
  }

  public void setKey(Object key) {
    this.key = key;
  }

  @CompilerDirectives.TruffleBoundary
  public void setItemsLeftFromInput() {
    itemsLeft = input.readInt();
  }

  public void decreaseItemsLeft() {
    itemsLeft--;
  }

  public Input getInput() {
    return input;
  }

  public Object getKey() {
    return key;
  }

  public int getItemsLeft() {
    return itemsLeft;
  }

  public OffHeapGroupByKey getOffHeapGroupByKey() {
    return offHeapGroupByKey;
  }
}
