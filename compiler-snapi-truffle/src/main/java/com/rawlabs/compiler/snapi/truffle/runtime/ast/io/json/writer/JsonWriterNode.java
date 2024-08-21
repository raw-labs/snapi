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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.StatementNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.StaticInitializers;
import java.io.IOException;
import java.io.OutputStream;

@NodeInfo(shortName = "Json.Write")
@NodeChild(value = "value", type = ExpressionNode.class)
@NodeField(name = "childCallTarget", type = RootCallTarget.class)
@ImportStatic(StaticInitializers.class)
public abstract class JsonWriterNode extends StatementNode {

  protected abstract RootCallTarget getChildCallTarget();

  @Specialization
  public void doWrite(
      Object value,
      @Bind("$node") Node thisNode,
      @Cached(inline = true) JsonWriteNodes.InitGeneratorJsonWriterNode initGeneratorNode,
      @Cached("create(getChildCallTarget())") DirectCallNode childDirectCall,
      @Cached(value = "getOutputStream(thisNode)", neverDefault = true) OutputStream outputStream) {
    try (JsonGenerator gen = initGeneratorNode.execute(this, outputStream)) {
      childDirectCall.call(value, gen);
    } catch (IOException e) {
      throw new RawTruffleRuntimeException(e.getMessage(), e, thisNode);
    }
  }
}
