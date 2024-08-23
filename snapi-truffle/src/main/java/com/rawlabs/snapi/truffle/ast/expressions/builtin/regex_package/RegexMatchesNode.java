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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@NodeInfo(shortName = "Regex.Matches")
@NodeChild(value = "string")
@NodeChild(value = "pattern")
public abstract class RegexMatchesNode extends ExpressionNode {

  @Specialization
  protected Object regexMatches(String string, String regex) {
    try {
      Pattern pattern = RegexCache.get(regex);
      return pattern.matcher(string).matches();
    } catch (PatternSyntaxException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
