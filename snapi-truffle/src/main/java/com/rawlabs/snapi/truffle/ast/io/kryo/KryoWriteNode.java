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

package com.rawlabs.snapi.truffle.ast.io.kryo;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.frontend.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.kryo.KryoNodes;
import com.rawlabs.snapi.truffle.runtime.kryo.KryoNodesFactory;
import java.io.ByteArrayOutputStream;

public class KryoWriteNode extends ExpressionNode {
  @Child private ExpressionNode valueNode;
  @Child private KryoNodes.KryoWriteNode writer = KryoNodesFactory.KryoWriteNodeGen.create();
  private final Rql2TypeWithProperties t;

  public KryoWriteNode(ExpressionNode valueNode, Rql2TypeWithProperties t) {
    this.valueNode = valueNode;
    this.t = t;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    ByteArrayOutputStream array = new ByteArrayOutputStream();
    Output output = new Output(array);
    writer.execute(this, output, t, valueNode.executeGeneric(virtualFrame));
    output.close();
    return array.toByteArray();
  }
}
