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

package raw.compiler.snapi.truffle.builtin.test_extension;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.Rql2AttrType;
import raw.compiler.rql2.source.Rql2LongType;
import raw.compiler.rql2.source.Rql2TypeProperty;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StaticRecordShapeBuilder;
import raw.runtime.truffle.ast.expressions.record.RecordBuildNode;
import raw.utils.RecordFieldsNaming;

public interface TruffleValueArg extends TruffleEntryExtension {
  @Override
  default ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {

    Rql2AttrType[] atts = new Rql2AttrType[1];
    atts[0] =
        new Rql2AttrType(
            "arg",
            new Rql2LongType(new scala.collection.immutable.HashSet<Rql2TypeProperty>().seq()));

    Vector<String> keys =
        new Vector<>(
            Arrays.asList(Arrays.stream(atts).map(Rql2AttrType::idn).toArray(String[]::new)));
    Vector<String> distinctKeys = RecordFieldsNaming.makeDistinct(keys);

    return new RecordBuildNode(
        new ExpressionNode[] {args.get(0).exprNode()},
        StaticRecordShapeBuilder.build(rawLanguage, atts, keys, distinctKeys));
  }
}
