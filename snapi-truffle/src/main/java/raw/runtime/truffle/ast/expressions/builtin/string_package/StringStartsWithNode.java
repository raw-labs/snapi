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

package raw.runtime.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;

@NodeInfo(shortName = "String.StartsWith")
@NodeChild(value = "string")
@NodeChild(value = "prefix")
public abstract class StringStartsWithNode extends ExpressionNode {

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected boolean stringReplace(String string, String prefix) {
    return string.startsWith(prefix);
  }
}
