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
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.option.StringOption;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@NodeInfo(shortName = "Regex.FirstMatchIn")
@NodeChild(value = "string")
@NodeChild(value = "pattern")
public abstract class RegexGroupsNode extends ExpressionNode {

  @Specialization
  protected ObjectTryable regexGroups(String string, String regex) {
    try {
      Pattern pattern = RegexCache.get(regex);
      Matcher matcher = pattern.matcher(string);

      if (matcher.matches()) {
        // Group zero is the full match
        StringOption[] groups = new StringOption[matcher.groupCount()];
        for (int i = 0; i < matcher.groupCount(); i++) {
          String value = matcher.group(i + 1);
          if (value == null) {
            groups[i] = new StringOption();
          } else {
            groups[i] = new StringOption(value);
          }
        }
        return ObjectTryable.BuildSuccess(new ObjectList(groups));
      } else {
        String error =
            String.format(
                "string '%s' does not match pattern '%s'", string, matcher.pattern().pattern());
        return ObjectTryable.BuildFailure(error);
      }
    } catch (PatternSyntaxException e) {
      return ObjectTryable.BuildFailure(e.getMessage());
    }
  }
}
