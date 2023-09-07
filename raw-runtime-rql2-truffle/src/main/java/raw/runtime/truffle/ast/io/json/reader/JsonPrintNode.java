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

package raw.runtime.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodes;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

@NodeInfo(shortName = "PrintJson")
@NodeChild(value = "result")
@NodeField(name = "childNode", type = RootNode.class)
public abstract class JsonPrintNode extends ExpressionNode {

  protected abstract RootNode getChildNode();

  @Specialization
  protected Object doParse(
      Object result,
      @Cached JsonWriteNodes.InitGeneratorJsonWriterNode initGenerator,
      @Cached("create(getChildNode().getCallTarget())") DirectCallNode childDirectCall) {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonGenerator gen = initGenerator.execute(stream)) {
      childDirectCall.call(result, gen);
      gen.flush();
      return stream.toString();
    } catch (IOException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }
}
