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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;

@NodeInfo(shortName = "Timestamp.FromUnixTimestamp")
@NodeChild("epoch")
public abstract class TimestampFromUnixTimestampNode extends ExpressionNode {
  @Specialization
  protected TimestampObject fromUnixTimestamp(long epoch) {
    ZoneId zoneID;
    RawContext context = RawContext.get(this);
    try {
      String zone =
          context.getRuntimeContext().settings().getStringOpt("raw.runtime.time-zone", true).get();
      zoneID = ZoneId.of(zone);
    } catch (NoSuchElementException ex) {
      zoneID = ZoneId.systemDefault();
    }
    Instant instant = Instant.ofEpochSecond(epoch);
    return new TimestampObject(LocalDateTime.ofInstant(instant, zoneID));
  }
}
