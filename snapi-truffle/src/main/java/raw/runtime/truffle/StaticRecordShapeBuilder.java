package raw.runtime.truffle;

import com.oracle.truffle.api.staticobject.DefaultStaticProperty;
import com.oracle.truffle.api.staticobject.StaticShape;
import java.util.Vector;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.runtime.record.RecordShapeWithFields;
import raw.runtime.truffle.runtime.record.RecordStaticObjectFactory;
import raw.runtime.truffle.runtime.record.StaticObjectRecord;

public class StaticRecordShapeBuilder {
  public static RecordShapeWithFields build(
      RawLanguage language, Rql2AttrType[] atts, Vector<String> keys, Vector<String> distinctKeys) {
    StaticShape.Builder builder = StaticShape.newBuilder(language);
    DefaultStaticProperty[] fields = new DefaultStaticProperty[atts.length];
    for (int i = 0; i < atts.length; i++) {
      DefaultStaticProperty keyField = new DefaultStaticProperty(String.valueOf(i));
      fields[i] = keyField;
      builder.property(keyField, Object.class, false);
    }
    StaticShape<RecordStaticObjectFactory> shape =
        builder.build(StaticObjectRecord.class, RecordStaticObjectFactory.class);
    return new RecordShapeWithFields(
        fields, keys.toArray(new String[0]), distinctKeys.toArray(new String[0]), shape);
  }
}
