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

package raw.runtime.truffle.ast.expressions.builtin.environment_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Bind;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.generator.collection.StaticInitializers;
import raw.runtime.truffle.runtime.list.ObjectList;

@NodeInfo(shortName = "Environment.Scopes")
@ImportStatic(StaticInitializers.class)
public abstract class EnvironmentScopesNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected static Object doScopes(
      @Bind("$node") Node thisNode,
      @Cached(value = "getScopes(thisNode)", neverDefault = true, dimensions = 1) String[] scopes) {
    return new ObjectList(scopes);
  }
}
