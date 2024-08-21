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

package com.rawlabs.snapi.truffle.runtime.ast.expressions.option;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.NullObject;

@NodeInfo(shortName = "Option.None")
public class OptionNoneNode extends ExpressionNode {

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    // TODO (msb): Create per type if we want to 'set()'.
    return NullObject.INSTANCE;
  }
}
