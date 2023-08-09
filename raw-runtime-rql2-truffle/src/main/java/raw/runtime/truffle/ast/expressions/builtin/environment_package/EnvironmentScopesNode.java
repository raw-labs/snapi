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

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.list.ObjectList;
import scala.collection.Iterator;

@NodeInfo(shortName = "Environment.Scopes")
public abstract class EnvironmentScopesNode extends ExpressionNode {

  @Specialization
  protected Object doScopes() {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    String[] bl = new String[context.scopes().size()];
    Iterator<String> it = context.scopes().iterator();
    int i = 0;
    while (it.hasNext()) {
      bl[i] = it.next();
      i++;
    }
    return new ObjectList(bl);
  }
}
