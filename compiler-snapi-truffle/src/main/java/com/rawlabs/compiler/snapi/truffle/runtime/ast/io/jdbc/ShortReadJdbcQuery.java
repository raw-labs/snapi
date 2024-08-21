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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.jdbc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;

@NodeInfo(shortName = "Jdbc.ShortRead")
public class ShortReadJdbcQuery extends ExpressionNode {

  private final String idx;

  public ShortReadJdbcQuery(String idx) {
    this.idx = idx;
  }

  public Object executeGeneric(VirtualFrame frame) {
    return this.executeShort(frame);
  }

  @Override
  public final short executeShort(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JdbcQuery rs = (JdbcQuery) args[0];
    return rs.getShort(idx, this);
  }
}
