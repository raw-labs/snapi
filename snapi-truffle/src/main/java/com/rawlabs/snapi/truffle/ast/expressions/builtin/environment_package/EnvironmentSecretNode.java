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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.environment_package;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.Rql2Context;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.generator.collection.StaticInitializers;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Environment.Secret")
@NodeChild(value = "key")
@ImportStatic(StaticInitializers.class)
public abstract class EnvironmentSecretNode extends ExpressionNode {

  @Specialization
  protected static Object doSecret(
      String key,
      @Bind("$node") Node thisNode,
      @Cached(value = "getRql2Context(thisNode)", neverDefault = true) Rql2Context context) {
    try {
      return context.getSecret(key);
    } catch (TruffleRuntimeException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
