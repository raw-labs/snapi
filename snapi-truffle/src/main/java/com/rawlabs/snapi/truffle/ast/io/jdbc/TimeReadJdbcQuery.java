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

package com.rawlabs.snapi.truffle.ast.io.jdbc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.primitives.TimeObject;

@NodeInfo(shortName = "Time.DateRead")
public class TimeReadJdbcQuery extends ExpressionNode {

  private final String idx;

  public TimeReadJdbcQuery(String idx) {
    this.idx = idx;
  }

  public Object executeGeneric(VirtualFrame frame) {
    return this.executeTime(frame);
  }

  @Override
  public final TimeObject executeTime(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    JdbcQuery rs = (JdbcQuery) args[0];
    return rs.getTime(idx, this);
  }
}