package raw.runtime.truffle.runtime.data_structures.dynamic_array;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

public class DynamicArrayObject extends DynamicObject implements TruffleObject {

  public DynamicArrayObject(Shape shape) {
    super(shape);
  }
}
