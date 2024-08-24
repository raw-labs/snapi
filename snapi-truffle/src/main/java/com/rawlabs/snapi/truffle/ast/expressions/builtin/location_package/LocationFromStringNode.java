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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.rql2.extensions.LocationDescription$;
import com.rawlabs.snapi.truffle.SnapiContext;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.utils.sources.api.Location;
import scala.util.Either;

@NodeInfo(shortName = "Location.FromString")
public class LocationFromStringNode extends ExpressionNode {

  @Child private ExpressionNode url;

  public LocationFromStringNode(ExpressionNode url) {
    this.url = url;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    String url = (String) this.url.executeGeneric(frame);

    SnapiContext context = SnapiContext.get(this);

    Location location = getLocationFromUrl(url, context);
    return new LocationObject(location, url);
  }

  @CompilerDirectives.TruffleBoundary
  private Location getLocationFromUrl(String url, SnapiContext context) {
    Either<String, Location> maybeLocation =
        LocationDescription$.MODULE$.urlToLocation(
            url, context.getProgramEnvironment(), context.getSettings());
    if (maybeLocation.isLeft()) {
      throw new TruffleRuntimeException(maybeLocation.left().get());
    }
    return maybeLocation.right().get();
  }
}
