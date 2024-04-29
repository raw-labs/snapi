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

package raw.compiler.snapi.truffle.builtin.jdbc;

import static raw.compiler.snapi.truffle.builtin.CompilerScalaConsts.*;

import com.oracle.truffle.api.frame.FrameDescriptor;
import raw.compiler.base.source.Type;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.ast.io.csv.writer.internal.*;
import raw.runtime.truffle.ast.io.jdbc.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import scala.collection.JavaConverters;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Jdbc {



    static public JdbcQueryNode query(
            ExpressionNode location,
            ExpressionNode query,
            Type t,
            JdbcExceptionHandler exceptionHandler,
            RawLanguage lang) {
        Rql2IterableType iterableType = (Rql2IterableType) t;
        Rql2RecordType recordType = (Rql2RecordType) iterableType.innerType();
        assert iterableType.props().isEmpty();
        assert recordType.props().isEmpty();

        FrameDescriptor frameDescriptor = new FrameDescriptor();

        Stream<AttrWithIndex> attrWithIndexStream = IntStream.range(0, recordType.atts().size())
                .mapToObj(i -> new AttrWithIndex(i, recordType.atts().apply(i)));

        ProgramExpressionNode[] columnParsers = attrWithIndexStream
                .map(att -> columnReader(att.index, att.attr.idn(), att.attr.tipe(), lang))
                .toArray(ProgramExpressionNode[]::new);

        // Should we reuse the attrWithIndexStream?
        RecordReadJdbcQuery recordParser =
                new RecordReadJdbcQuery(
                        columnParsers,
                        JavaConverters.asJavaCollection(recordType.atts()).stream().map(a -> (Rql2AttrType) a).toArray(Rql2AttrType[]::new));
        return new JdbcQueryNode(
                location,
                query,
                new ProgramExpressionNode(lang, frameDescriptor, recordParser),
                exceptionHandler);
    }

    static private ProgramExpressionNode columnReader(int index, String colName, Type t, RawLanguage lang) {
        FrameDescriptor frameDescriptor = new FrameDescriptor();
        ExpressionNode node = switch (t) {
            case Rql2TypeWithProperties r when r.props().contains(tryable) -> {
                ProgramExpressionNode inner = columnReader(index, colName, r.cloneAndRemoveProp(tryable), lang);
                yield new TryableReadJdbcQuery(inner, colName, index);
            }
            case Rql2TypeWithProperties r when r.props().contains(nullable) -> {
                ProgramExpressionNode inner = columnReader(index, colName, r.cloneAndRemoveProp(nullable), lang);
                yield new NullableReadJdbcQuery(inner, colName, index);
            }
            case Rql2ByteType ignored -> new ByteReadJdbcQuery(colName, index);
            case Rql2ShortType ignored -> new ShortReadJdbcQuery(colName, index);
            case Rql2IntType ignored -> new IntReadJdbcQuery(colName, index);
            case Rql2LongType ignored -> new LongReadJdbcQuery(colName, index);
            case Rql2FloatType ignored -> new FloatReadJdbcQuery(colName, index);
            case Rql2DoubleType ignored -> new DoubleReadJdbcQuery(colName, index);
            case Rql2DecimalType ignored -> new DecimalReadJdbcQuery(colName, index);
            case Rql2StringType ignored -> new StringReadJdbcQuery(colName, index);
            case Rql2DateType ignored -> new DateReadJdbcQuery(colName, index);
            case Rql2TimeType ignored -> new TimeReadJdbcQuery(colName, index);
            case Rql2TimestampType ignored -> new TimestampReadJdbcQuery(colName, index);
            case Rql2BoolType ignored -> new BoolReadJdbcQuery(colName, index);
            case Rql2BinaryType ignored -> new BinaryReadJdbcQuery(colName, index);
            default -> throw new RawTruffleInternalErrorException();
        };
        return new ProgramExpressionNode(lang, frameDescriptor, node);
    }

}
