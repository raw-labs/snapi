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
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "Timestamp.ToUnixTimestamp")
@NodeChild("timestamp")
public abstract class TimestampToUnixTimestampNode extends ExpressionNode {
  @Specialization
  @CompilerDirectives.TruffleBoundary
  protected long fromUnixTimestamp(TimestampObject timestampObj) {
    ZoneId zoneID;
    RawContext context = RawContext.get(this);
    try {
      String zone =
          context.getSourceContext().settings().getStringOpt("raw.runtime.time-zone", true).get();
      zoneID = ZoneId.of(zone);
    } catch (NoSuchElementException ex) {
      zoneID = ZoneId.systemDefault();
    }
    return timestampObj.getTimestamp().atZone(zoneID).toEpochSecond();
  }
}
