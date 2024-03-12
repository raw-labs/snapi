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
