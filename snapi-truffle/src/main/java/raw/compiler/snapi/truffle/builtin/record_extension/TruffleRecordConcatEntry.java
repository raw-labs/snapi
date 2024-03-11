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

package raw.compiler.snapi.truffle.builtin.record_extension;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.RecordConcatEntry;
import raw.compiler.rql2.source.Rql2AttrType;
import raw.compiler.rql2.source.Rql2RecordType;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StaticRecordShapeBuilder;
import raw.runtime.truffle.ast.expressions.record.RecordConcatNodeGen;
import raw.utils.RecordFieldsNaming;
import scala.collection.JavaConverters;

public class TruffleRecordConcatEntry extends RecordConcatEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    Stream<Rql2AttrType> atts1 =
        JavaConverters.asJavaCollection(((Rql2RecordType) args.get(0).type()).atts()).stream()
            .map(a -> (Rql2AttrType) a);

    Stream<Rql2AttrType> atts2 =
        JavaConverters.asJavaCollection(((Rql2RecordType) args.get(1).type()).atts()).stream()
            .map(a -> (Rql2AttrType) a);

    Rql2AttrType[] atts = Stream.concat(atts1, atts2).toArray(Rql2AttrType[]::new);
    Vector<String> keys =
        new Vector<>(
            Arrays.asList(Arrays.stream(atts).map(Rql2AttrType::idn).toArray(String[]::new)));

    Vector<String> distinctKeys = RecordFieldsNaming.makeDistinct(keys);

    return RecordConcatNodeGen.create(
        args.get(0).exprNode(),
        args.get(1).exprNode(),
        StaticRecordShapeBuilder.build(rawLanguage, atts, keys, distinctKeys));
  }
}
