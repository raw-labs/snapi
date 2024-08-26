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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.regex_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.list.ObjectList;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.primitives.NullObject;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@NodeInfo(shortName = "Regex.FirstMatchIn")
@NodeChild(value = "string")
@NodeChild(value = "pattern")
public abstract class RegexGroupsNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected Object regexGroups(String string, String regex) {
    try {
      Pattern pattern = RegexCache.get(regex);
      Matcher matcher = pattern.matcher(string);

      if (matcher.matches()) {
        // Group zero is the full match
        Object[] groups = new Object[matcher.groupCount()];
        for (int i = 0; i < matcher.groupCount(); i++) {
          String value = matcher.group(i + 1);
          groups[i] = Objects.requireNonNullElse(value, NullObject.INSTANCE);
        }
        return new ObjectList(groups);
      } else {
        String error =
            String.format(
                "string '%s' does not match pattern '%s'", string, matcher.pattern().pattern());
        return new ErrorObject(error);
      }
    } catch (PatternSyntaxException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
