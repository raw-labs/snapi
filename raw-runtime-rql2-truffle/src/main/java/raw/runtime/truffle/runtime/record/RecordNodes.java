package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.DynamicObjectLibrary;

public class RecordNodes {
  @NodeInfo(shortName = "Record.AddByKey")
  @GenerateUncached
  public abstract static class PutValueNode extends Node {

    public abstract void execute(DynamicObject values, int keysSize, Object value);

    @Specialization(limit = "3")
    void exec(
        DynamicObject values,
        int keysSize,
        Object value,
        @CachedLibrary("values") DynamicObjectLibrary valuesLibrary) {
      valuesLibrary.put(values, keysSize, value);
    }
  }

  @NodeInfo(shortName = "Record.ReadIndex")
  @GenerateUncached
  public abstract static class ReadIndexNode extends Node {

    public abstract Object execute(DynamicObject values, int index);

    @Specialization(limit = "3")
    Object exec(
        DynamicObject values,
        int index,
        @CachedLibrary("values") DynamicObjectLibrary valuesLibrary) {
      return valuesLibrary.getOrDefault(values, index, null);
    }
  }
}
