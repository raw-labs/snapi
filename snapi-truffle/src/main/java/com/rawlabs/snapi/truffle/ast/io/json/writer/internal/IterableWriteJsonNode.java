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

package com.rawlabs.snapi.truffle.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.ProgramStatementNode;
import com.rawlabs.snapi.truffle.ast.io.json.writer.JsonWriteNodes;
import com.rawlabs.snapi.truffle.ast.io.json.writer.JsonWriteNodesFactory;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodesFactory;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodesFactory;

@NodeInfo(shortName = "IterableWriteJson")
public class IterableWriteJsonNode extends StatementNode {

  @Child private DirectCallNode childDirectCall;

  @Child
  private IterableNodes.GetGeneratorNode getGeneratorNode =
      IterableNodesFactory.GetGeneratorNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorInitNode generatorInitNode =
      GeneratorNodesFactory.GeneratorInitNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorCloseNode generatorCloseNodeNode =
      GeneratorNodesFactory.GeneratorCloseNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorNextNode generatorNextNode =
      GeneratorNodesFactory.GeneratorNextNodeGen.create();

  @Child
  private GeneratorNodes.GeneratorHasNextNode generatorHasNextNode =
      GeneratorNodesFactory.GeneratorHasNextNodeGen.create();

  @Child
  private JsonWriteNodes.WriteStartArrayJsonWriterNode writeStartArrayNode =
      JsonWriteNodesFactory.WriteStartArrayJsonWriterNodeGen.create();

  @Child
  private JsonWriteNodes.WriteEndArrayJsonWriterNode writeEndArrayNode =
      JsonWriteNodesFactory.WriteEndArrayJsonWriterNodeGen.create();

  public IterableWriteJsonNode(ProgramStatementNode childProgramStatementNode) {
    this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
  }

  @Override
  public void executeVoid(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    Object iterable = args[0];
    JsonGenerator gen = (JsonGenerator) args[1];
    Object generator = getGeneratorNode.execute(this, iterable);
    try {
      generatorInitNode.execute(this, generator);
      writeStartArrayNode.execute(this, gen);

      while (generatorHasNextNode.execute(this, generator)) {
        childDirectCall.call(generatorNextNode.execute(this, generator), gen);
      }

      writeEndArrayNode.execute(this, gen);
    } finally {
      generatorCloseNodeNode.execute(this, generator);
    }
  }
}
