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

package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.sources.api.Location;

@NodeInfo(shortName = "Location.FromString")
public class LocationFromStringNode extends ExpressionNode {

  @Child private ExpressionNode url;

  public LocationFromStringNode(ExpressionNode url) {
    this.url = url;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    String url = (String) this.url.executeGeneric(frame);

    // FIXME (msb): Do regex here and build location object
    throw new RawTruffleRuntimeException("Location.FromString not implemented");
//    Location location = null;
//
//    return new LocationObject(location);
  }

}
