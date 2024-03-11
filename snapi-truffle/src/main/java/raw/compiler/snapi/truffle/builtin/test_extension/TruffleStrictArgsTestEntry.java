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
import raw.compiler.rql2.builtin.StrictArgsTestEntry;
import raw.compiler.rql2.source.*;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StaticRecordShapeBuilder;
import raw.runtime.truffle.ast.expressions.binary.PlusNode;
import raw.runtime.truffle.ast.expressions.builtin.numeric.float_package.FloatFromNodeGen;
import raw.runtime.truffle.ast.expressions.iterable.list.ListCountNodeGen;
import raw.runtime.truffle.ast.expressions.literals.FloatNode;
import raw.runtime.truffle.ast.expressions.literals.LongNode;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.expressions.record.RecordBuildNode;
import raw.runtime.truffle.ast.expressions.record.RecordProjNodeGen;
import raw.utils.RecordFieldsNaming;

public class TruffleStrictArgsTestEntry extends StrictArgsTestEntry
    implements TruffleEntryExtension, WithArgs {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode listArg = args.get(0).exprNode();

    Rql2AttrType[] atts = new Rql2AttrType[2];
    atts[0] =
        new Rql2AttrType(
            "a",
            new Rql2LongType(new scala.collection.immutable.HashSet<Rql2TypeProperty>().seq()));
    atts[1] =
        new Rql2AttrType(
            "b",
            new Rql2FloatType(new scala.collection.immutable.HashSet<Rql2TypeProperty>().seq()));

    Vector<String> keys =
        new Vector<>(
            Arrays.asList(Arrays.stream(atts).map(Rql2AttrType::idn).toArray(String[]::new)));
    Vector<String> distinctKeys = RecordFieldsNaming.makeDistinct(keys);

    ExpressionNode recordArg =
        arg(args, "r")
            .orElse(
                new RecordBuildNode(
                    new ExpressionNode[] {new LongNode("0"), new FloatNode("0")},
                    StaticRecordShapeBuilder.build(rawLanguage, atts, keys, distinctKeys)));
    return new PlusNode(
        FloatFromNodeGen.create(
            new PlusNode(
                ListCountNodeGen.create(listArg),
                RecordProjNodeGen.create(recordArg, new StringNode("a")))),
        RecordProjNodeGen.create(recordArg, new StringNode("b")));
  }
}
