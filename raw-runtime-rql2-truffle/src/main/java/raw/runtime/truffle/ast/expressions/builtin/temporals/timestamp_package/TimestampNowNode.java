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

package raw.runtime.truffle.ast.expressions.builtin.temporals.timestamp_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.LocalDateTime;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "Timestamp.Now")
public abstract class TimestampNowNode extends ExpressionNode {
  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected TimestampObject now() {
    return new TimestampObject(LocalDateTime.now());
  }
}
