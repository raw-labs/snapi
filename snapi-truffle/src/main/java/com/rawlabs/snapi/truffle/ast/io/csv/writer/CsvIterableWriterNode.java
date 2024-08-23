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

package com.rawlabs.snapi.truffle.ast.io.csv.writer;

import static com.fasterxml.jackson.dataformat.csv.CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.Rql2Context;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.csv.CsvWriterTruffleException;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodesFactory;
import java.io.IOException;
import java.io.OutputStream;

public class CsvIterableWriterNode extends StatementNode {

  @Child private ExpressionNode dataNode;

  @Child private DirectCallNode itemWriter;

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorInitNode generatorInitNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode generatorNextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorHasNextNode generatorHasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode generatorCloseNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  private final String[] columnNames;
  private final String lineSeparator;

  private final OutputStream os = Rql2Context.get(this).getOutput();

  public CsvIterableWriterNode(
      ExpressionNode dataNode,
      RootCallTarget writeRootCallTarget,
      String[] columnNames,
      String lineSeparator) {
    this.dataNode = dataNode;
    this.columnNames = columnNames;
    this.lineSeparator = lineSeparator;
    itemWriter = DirectCallNode.create(writeRootCallTarget);
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object iterable = dataNode.executeGeneric(frame);
    Object generator = getGeneratorNode.execute(this, iterable);
    try (CsvGenerator gen = createGenerator(os)) {
      generatorInitNode.execute(this, generator);
      while (generatorHasNextNode.execute(this, generator)) {
        Object item = generatorNextNode.execute(this, generator);
        itemWriter.call(item, gen);
      }
    } catch (IOException e) {
      throw new CsvWriterTruffleException(e.getMessage(), e, this);
    } finally {
      generatorCloseNode.execute(this, generator);
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
      throw new CsvWriterTruffleException(e.getMessage(), e, this);
    }
  }
}
