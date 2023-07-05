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

package raw.runtime.truffle.ast.csv.writer;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvWriterRawTruffleException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

import java.io.IOException;
import java.io.OutputStream;

import static com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING;

public class CsvIterableWriterNode extends StatementNode {

    @Child
    private ExpressionNode dataNode;

    @Child
    private DirectCallNode itemWriter;

    @Child
    private IterableLibrary iterables = IterableLibrary.getFactory().createDispatched(3);

    @Child
    private GeneratorLibrary generators = GeneratorLibrary.getFactory().createDispatched(3);

    private String[] columnNames;

    public CsvIterableWriterNode(ExpressionNode dataNode, RootNode writerNode, String[] columnNames) {
        this.dataNode = dataNode;
        this.columnNames = columnNames;
        itemWriter = DirectCallNode.create(writerNode.getCallTarget());
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        try (OutputStream os = RawContext.get(this).getOutput();
             CsvGenerator gen = createGenerator(os)) {
            Object iterable = dataNode.executeGeneric(frame);
            Object generator = iterables.getGenerator(iterable);
            generators.init(generator);
            while (generators.hasNext(generator)) {
                Object item = generators.next(generator);
                itemWriter.call(item, gen);
            }
        } catch (IOException e) {
            throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
        }
    }

    @CompilerDirectives.TruffleBoundary
    private CsvGenerator createGenerator(OutputStream os) {
        try {
            CsvFactory factory = new CsvFactory();
            CsvGenerator generator = factory.createGenerator(os, JsonEncoding.UTF8);
            CsvSchema.Builder schemaBuilder = CsvSchema.builder();
            for (String colName : columnNames) {
                schemaBuilder.addColumn(colName);
            }
            schemaBuilder.setColumnSeparator(',');
            schemaBuilder.setUseHeader(true);
            schemaBuilder.setLineSeparator('\n');
            schemaBuilder.setQuoteChar('"');
            schemaBuilder.setNullValue("");
            generator.setSchema(schemaBuilder.build());
            generator.enable(STRICT_CHECK_FOR_QUOTING);
            return generator;
        } catch (IOException e) {
            throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
        }
    }

}
