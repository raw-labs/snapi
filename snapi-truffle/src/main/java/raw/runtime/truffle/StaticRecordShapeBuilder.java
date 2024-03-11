package raw.runtime.truffle;

import com.oracle.truffle.api.staticobject.StaticShape;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.runtime.record.RecordShapeWithFields;
import raw.runtime.truffle.runtime.record.RecordStaticObjectFactory;
import raw.runtime.truffle.runtime.record.StaticObjectRecord;
import raw.runtime.truffle.runtime.record.StaticRecordObjectField;

import java.util.Vector;

public class StaticRecordShapeBuilder {
  public static RecordShapeWithFields build(
      RawLanguage language, Rql2AttrType[] atts, Vector<String> keys, Vector<String> distinctKeys) {
    StaticShape.Builder builder = StaticShape.newBuilder(language);
    StaticRecordObjectField[] fields = new StaticRecordObjectField[atts.length];
    for (int i = 0; i < atts.length; i++) {
      StaticRecordObjectField keyField =
          new StaticRecordObjectField(
              i, (Rql2TypeWithProperties) atts[i].tipe(), keys.get(i), distinctKeys.get(i));
      fields[i] = keyField;
      builder.property(keyField, rawToJavaType((Rql2TypeWithProperties) atts[i].tipe()), true);
    }
    StaticShape<RecordStaticObjectFactory> shape =
        builder.build(StaticObjectRecord.class, RecordStaticObjectFactory.class);
    return new RecordShapeWithFields(fields, shape);
  }

  private static Class<?> rawToJavaType(Rql2TypeWithProperties type) {
    return switch (type) {
      case Rql2BoolType ignored when ignored.props().isEmpty() -> boolean.class;
      case Rql2ByteType ignored when ignored.props().isEmpty() -> byte.class;
      case Rql2ShortType ignored when ignored.props().isEmpty() -> short.class;
      case Rql2IntType ignored when ignored.props().isEmpty() -> int.class;
      case Rql2LongType ignored when ignored.props().isEmpty() -> long.class;
      case Rql2FloatType ignored when ignored.props().isEmpty() -> float.class;
      case Rql2DoubleType ignored when ignored.props().isEmpty() -> double.class;
      default -> Object.class;
    };
  }
}
