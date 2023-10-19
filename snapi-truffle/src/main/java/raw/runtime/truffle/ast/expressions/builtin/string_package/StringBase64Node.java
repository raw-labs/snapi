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
import java.util.Base64;
import raw.runtime.truffle.ExpressionNode;

@NodeInfo(shortName = "String.Base64")
@NodeChild(value = "string")
public abstract class StringBase64Node extends ExpressionNode {

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected String stringBase64(String string) {
    return Base64.getEncoder().encodeToString(string.getBytes());
  }
}
