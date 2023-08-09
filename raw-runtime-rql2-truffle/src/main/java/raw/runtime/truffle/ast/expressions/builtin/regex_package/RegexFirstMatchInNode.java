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

package raw.runtime.truffle.ast.expressions.builtin.regex_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.option.StringOption;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@NodeInfo(shortName = "Regex.FirstMatchIn")
@NodeChild(value = "string")
@NodeChild(value = "pattern")
public abstract class RegexFirstMatchInNode extends ExpressionNode {

  @Specialization
  protected ObjectTryable regexFirstMatchIn(String string, String regex) {
    try {
      Pattern pattern = RegexCache.get(regex);
      Matcher match = pattern.matcher(string);
      if (match.find()) {
        return ObjectTryable.BuildSuccess(new StringOption(match.group()));
      } else {
        return ObjectTryable.BuildSuccess(new StringOption());
      }
    } catch (PatternSyntaxException e) {
      return ObjectTryable.BuildFailure(e.getMessage());
    }
  }
}
