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

package raw.runtime.truffle.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;

@NodeInfo(shortName = "TypeCastAny")
@NodeChild("value")
@NodeField(name = "tipe", type = Rql2TypeWithProperties.class)
public abstract class TypeCastAnyNode extends ExpressionNode {

  @Idempotent
  protected abstract Rql2TypeWithProperties getTipe();

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected Object exec(
      Object value, @Cached(inline = true) TypeCastNodes.TypeCastAnyRecursiveNode castNode) {
    return castNode.execute(this, value, getTipe());
  }
}
