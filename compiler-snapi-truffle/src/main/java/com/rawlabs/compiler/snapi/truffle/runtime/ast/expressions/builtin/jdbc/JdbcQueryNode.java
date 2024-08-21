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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.jdbc;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawContext;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.rdbms.JdbcExceptionHandler;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.iterable.sources.JdbcQueryCollection;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.LocationObject;

@NodeInfo(shortName = "Jdbc.Query")
public class JdbcQueryNode extends ExpressionNode {

  @Child private ExpressionNode locationExp;
  @Child private ExpressionNode queryExp;
  private final RootCallTarget makeRowCallTarget;
  private final JdbcExceptionHandler exceptionHandler;

  public JdbcQueryNode(
      ExpressionNode locationExp,
      ExpressionNode queryExp,
      ProgramExpressionNode rowReader,
      JdbcExceptionHandler exceptionHandler) {
    this.locationExp = locationExp;
    this.queryExp = queryExp;
    this.makeRowCallTarget = rowReader.getCallTarget();
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    RawSettings rawSettings = RawContext.get(this).getSettings();
    LocationObject dbLocation = (LocationObject) locationExp.executeGeneric(virtualFrame);
    String query = (String) this.queryExp.executeGeneric(virtualFrame);
    return new JdbcQueryCollection(
        dbLocation, query, rawSettings, makeRowCallTarget, exceptionHandler);
  }
}
