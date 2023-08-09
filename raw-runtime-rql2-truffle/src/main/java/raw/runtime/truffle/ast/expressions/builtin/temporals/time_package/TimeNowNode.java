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

package raw.runtime.truffle.ast.expressions.builtin.temporals.time_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;

import java.time.LocalTime;

@NodeInfo(shortName = "Time.Now")
public abstract class TimeNowNode extends ExpressionNode {
  @Specialization
  protected TimeObject now() {
    return new TimeObject(LocalTime.now());
  }
}
