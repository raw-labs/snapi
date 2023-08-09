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

package raw.runtime.truffle.ast.io.kryo;

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.frame.VirtualFrame;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.kryo.KryoWriter;
import raw.runtime.truffle.runtime.kryo.KryoWriterLibrary;

import java.io.ByteArrayOutputStream;

public class KryoWriteNode extends ExpressionNode {
  private final ExpressionNode valueNode;
  private final KryoWriterLibrary writers = KryoWriterLibrary.getUncached();
  private final Rql2TypeWithProperties t;

  public KryoWriteNode(ExpressionNode valueNode, Rql2TypeWithProperties t) {
    this.valueNode = valueNode;
    this.t = t;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    ByteArrayOutputStream array = new ByteArrayOutputStream();
    Output output = new Output(array);
    KryoWriter writer = new KryoWriter();
    writers.write(writer, output, t, valueNode.executeGeneric(virtualFrame));
    output.close();
    return array.toByteArray();
  }
}
