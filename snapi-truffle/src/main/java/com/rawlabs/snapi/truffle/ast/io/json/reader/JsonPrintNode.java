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

package com.rawlabs.snapi.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.io.json.writer.JsonWriteNodes;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@NodeInfo(shortName = "PrintJson")
@NodeChild(value = "result")
@NodeField(name = "childCallTarget", type = RootCallTarget.class)
public abstract class JsonPrintNode extends ExpressionNode {

  protected abstract RootCallTarget getChildCallTarget();

  @Specialization
  protected Object doParse(
      Object result,
      @Cached(inline = true) JsonWriteNodes.InitGeneratorJsonWriterNode initGenerator,
      @Cached("create(getChildCallTarget())") DirectCallNode childDirectCall) {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JsonGenerator gen = initGenerator.execute(this, stream)) {
      childDirectCall.call(result, gen);
      gen.flush();
      return stream.toString();
    } catch (IOException e) {
      throw new TruffleRuntimeException(e.getMessage(), e, this);
    }
  }
}
