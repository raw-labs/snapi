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
import java.util.Arrays;
import raw.runtime.truffle.ExpressionNode;

@NodeInfo(shortName = "String.LevenshteinDistance")
@NodeChild(value = "string1")
@NodeChild(value = "string")
public abstract class StringLevenshteinDistanceNode extends ExpressionNode {

  private int costOfSubstitution(char a, char b) {
    return a == b ? 0 : 1;
  }

  @CompilerDirectives.TruffleBoundary
  private int min(int... numbers) {
    return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
  }

  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected int levenshteinDistanceDP(String string1, String string2) {
    int[][] dp = new int[string1.length() + 1][string2.length() + 1];

    for (int i = 0; i <= string1.length(); i++) {
      for (int j = 0; j <= string2.length(); j++) {
        if (i == 0) {
          dp[i][j] = j;
        } else if (j == 0) {
          dp[i][j] = i;
        } else {
          dp[i][j] =
              min(
                  dp[i - 1][j - 1]
                      + costOfSubstitution(string1.charAt(i - 1), string2.charAt(j - 1)),
                  dp[i - 1][j] + 1,
                  dp[i][j - 1] + 1);
        }
      }
    }

    return dp[string1.length()][string2.length()];
  }
}
