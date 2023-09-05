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
import java.util.regex.Pattern;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.list.StringList;

@NodeInfo(shortName = "String.Split")
@NodeChild(value = "string")
@NodeChild(value = "separator")
public abstract class StringSplitNode extends ExpressionNode {
  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected StringList stringSplit(String string, String separator) {
    String escaped = Pattern.quote(separator);
    return new StringList(string.split(escaped));
  }
}
