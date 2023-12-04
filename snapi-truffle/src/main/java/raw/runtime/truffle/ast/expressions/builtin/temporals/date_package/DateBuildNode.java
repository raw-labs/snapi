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

package raw.runtime.truffle.ast.expressions.builtin.temporals.date_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.DateTimeException;
import java.time.LocalDate;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Date.Build")
@NodeChild("y")
@NodeChild("m")
@NodeChild("d")
public abstract class DateBuildNode extends ExpressionNode {

  @Specialization
  @CompilerDirectives.TruffleBoundary
  public Object buildDate(int y, int m, int d) {
    try {
      return new DateObject(LocalDate.of(y, m, d));
    } catch (DateTimeException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
