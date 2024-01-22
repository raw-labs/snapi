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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer;

import com.esotericsoftware.kryo.io.Input;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.order_by.OffHeapGroupByKeys;

public class OrderByInputBuffer {

  private final OffHeapGroupByKeys offHeapGroupByKeys;
  private final Input input;
  private Object[] keys;
  private int itemsLeft;

  public OrderByInputBuffer(OffHeapGroupByKeys offHeapGroupByKeys, Input input) {
    this.input = input;
    this.keys = null;
    this.offHeapGroupByKeys = offHeapGroupByKeys;
  }

  public void setKeys(Object[] keys) {
    this.keys = keys;
  }

  public void setItemsLeftFromInput() {
    itemsLeft = input.readInt();
  }

  public void decreaseItemsLeft() {
    itemsLeft--;
  }

  public Input getInput() {
    return input;
  }

  public Object[] getKeys() {
    return keys;
  }

  public int getItemsLeft() {
    return itemsLeft;
  }

  public OffHeapGroupByKeys getOffHeapGroupByKey() {
    return offHeapGroupByKeys;
  }
}
