package raw.runtime.truffle.runtime.data_structures.map;

import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

public class MapObject extends DynamicObject implements TruffleObject {

  public MapObject(Shape shape) {
    super(shape);
  }
}
