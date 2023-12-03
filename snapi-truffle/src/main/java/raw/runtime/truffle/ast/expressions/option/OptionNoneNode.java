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

package raw.runtime.truffle.ast.expressions.option;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.base.source.Type;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.NullObject;

@NodeInfo(shortName = "Option.None")
public class OptionNoneNode extends ExpressionNode {

  private final Type tipe;

  public OptionNoneNode(Type tipe) {
    this.tipe = tipe;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    // TODO (msb): Create per type if we want to 'set()'.
    return NullObject.INSTANCE;
  }
}
