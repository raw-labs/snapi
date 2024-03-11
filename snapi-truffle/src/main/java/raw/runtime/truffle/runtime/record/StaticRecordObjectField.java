package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.staticobject.StaticProperty;
import raw.compiler.rql2.source.*;

public class StaticRecordObjectField extends StaticProperty {
  final String index;
  final Rql2TypeWithProperties type;
  final String distinctKey;
  final String key;

  public StaticRecordObjectField(
      int index, Rql2TypeWithProperties type, String key, String distinctKey) {
    this.index = String.valueOf(index);
    this.type = type;
    this.key = key;
    this.distinctKey = distinctKey;
  }

  @Override
  public String getId() {
    return index;
  }

  public Rql2TypeWithProperties getType() {
    return type;
  }

  public String getDistinctKey() {
    return distinctKey;
  }

  public String getKey() {
    return key;
  }

  public void set(StaticObjectRecord staticObject, Object value) {
    switch (type) {
      case Rql2BoolType ignored when ignored.props().isEmpty() -> this.setBoolean(staticObject, (boolean) value);
      case Rql2ByteType ignored when ignored.props().isEmpty() -> this.setByte(staticObject, (byte) value);
      case Rql2ShortType ignored when ignored.props().isEmpty() -> this.setShort(staticObject, (short) value);
      case Rql2IntType ignored when ignored.props().isEmpty() -> this.setInt(staticObject, (int) value);
      case Rql2LongType ignored when ignored.props().isEmpty() -> this.setLong(staticObject, (long) value);
      case Rql2FloatType ignored when ignored.props().isEmpty() -> this.setFloat(staticObject, (float) value);
      case Rql2DoubleType ignored when ignored.props().isEmpty() -> this.setDouble(staticObject, (double) value);
      default -> this.setObject(staticObject, value);
    }
  }

  public Object get(StaticObjectRecord staticObject) {
    return switch (type) {
      case Rql2BoolType ignored when ignored.props().isEmpty() -> this.getBoolean(staticObject);
      case Rql2ByteType ignored when ignored.props().isEmpty() -> this.getByte(staticObject);
      case Rql2ShortType ignored when ignored.props().isEmpty() -> this.getShort(staticObject);
      case Rql2IntType ignored when ignored.props().isEmpty() -> this.getInt(staticObject);
      case Rql2LongType ignored when ignored.props().isEmpty() -> this.getLong(staticObject);
      case Rql2FloatType ignored when ignored.props().isEmpty() -> this.getFloat(staticObject);
      case Rql2DoubleType ignored when ignored.props().isEmpty() -> this.getDouble(staticObject);
      default -> this.getObject(staticObject);
    };
  }
}
