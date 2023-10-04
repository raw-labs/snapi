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

package raw.compiler.snapi.truffle;

import raw.compiler.base.source.Type;
import raw.runtime.truffle.ExpressionNode;

public class TruffleArg {
  private final ExpressionNode exprNode;
  private final Type type;
  private final String identifier;

  public TruffleArg(ExpressionNode e, Type t, String identifier) {
    this.exprNode = e;
    this.type = t;
    this.identifier = identifier;
  }

  public ExpressionNode getExprNode() {
    return exprNode;
  }

  public Type getType() {
    return type;
  }
  public String getIdentifier() {
    return identifier;
  }
}
