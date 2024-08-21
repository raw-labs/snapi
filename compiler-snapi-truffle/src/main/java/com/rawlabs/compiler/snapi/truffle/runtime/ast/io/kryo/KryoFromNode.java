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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import java.io.ByteArrayInputStream;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.kryo.KryoNodes;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.kryo.KryoNodesFactory;

public class KryoFromNode extends ExpressionNode {
  @Child private ExpressionNode valueNode;
  @Child private KryoNodes.KryoReadNode reader = KryoNodesFactory.KryoReadNodeGen.create();
  private final Rql2TypeWithProperties t;

  public KryoFromNode(ExpressionNode valueNode, Rql2TypeWithProperties t) {
    this.valueNode = valueNode;
    this.t = t;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    byte[] binary = (byte[]) valueNode.executeGeneric(virtualFrame);
    Input input = new Input(new ByteArrayInputStream(binary));
    Object object = reader.execute(this, input, t);
    input.close();
    return object;
  }
}
