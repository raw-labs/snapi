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

package raw.runtime.truffle.ast.io.json.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import java.io.IOException;
import java.io.OutputStream;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@NodeInfo(shortName = "Json.Write")
@NodeChild(value = "value", type = ExpressionNode.class)
@NodeField(name = "childNode", type = RootNode.class)
public abstract class JsonWriterNode extends StatementNode {

  protected abstract RootNode getChildNode();

  @Specialization
  public void doWrite(
      Object value,
      @Cached JsonWriteNodes.InitGeneratorJsonWriterNode initGeneratorNode,
      @Cached("create(getChildNode().getCallTarget())") DirectCallNode childDirectCall) {
    try (OutputStream os = RawContext.get(this).getOutput();
        JsonGenerator gen = initGeneratorNode.execute(os)) {
      childDirectCall.call(value, gen);
    } catch (IOException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }
}
