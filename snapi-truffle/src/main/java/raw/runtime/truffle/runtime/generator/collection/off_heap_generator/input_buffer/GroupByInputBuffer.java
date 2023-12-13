package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer;

import com.esotericsoftware.kryo.io.Input;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.off_heap.group_by.OffHeapGroupByKey;

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
