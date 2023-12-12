package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.input_buffer;

import com.esotericsoftware.kryo.io.Input;
import raw.runtime.truffle.runtime.generator.collection.off_heap_generator.group_by_key.OffHeapGroupByKey;

public class InputBuffer {
  private final OffHeapGroupByKey offHeapGroupByKey;
  private final Input input;
  private Object key;
  private int itemsLeft;

  public InputBuffer(Input input, OffHeapGroupByKey offHeapGroupByKey) {
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
