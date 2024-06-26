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

package raw.runtime.truffle.ast.io.csv.writer;

import static com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import java.io.IOException;
import java.io.OutputStream;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.csv.CsvWriterRawTruffleException;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ListNodesFactory;
import raw.runtime.truffle.runtime.list.ObjectList;

public class CsvListWriterNode extends StatementNode {

  @Child private ExpressionNode dataNode;

  @Child private DirectCallNode itemWriter;

  @Child private ListNodes.SizeNode sizeNode = ListNodesFactory.SizeNodeGen.create();

  @Child private ListNodes.GetNode getNode = ListNodesFactory.GetNodeGen.create();

  private final String[] columnNames;
  private final String lineSeparator;
  private final OutputStream os;

  public CsvListWriterNode(
      ExpressionNode dataNode,
      RootCallTarget writeRootCallTarget,
      String[] columnNames,
      String lineSeparator) {
    this.dataNode = dataNode;
    itemWriter = DirectCallNode.create(writeRootCallTarget);
    this.columnNames = columnNames;
    this.lineSeparator = lineSeparator;
    this.os = RawContext.get(this).getOutput();
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    try (CsvGenerator gen = createGenerator(os)) {
      ObjectList list = (ObjectList) dataNode.executeGeneric(frame);
      long size = sizeNode.execute(this, list);
      for (long i = 0; i < size; i++) {
        Object item = getNode.execute(this, list, i);
        itemWriter.call(item, gen);
      }
    } catch (IOException e) {
      throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
    }
  }

  @TruffleBoundary
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
      schemaBuilder.setLineSeparator(lineSeparator);
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
