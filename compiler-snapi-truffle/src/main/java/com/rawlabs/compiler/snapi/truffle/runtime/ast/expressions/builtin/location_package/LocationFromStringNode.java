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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.rql2.api.LocationDescription$;
import com.rawlabs.utils.sources.api.Location;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawContext;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.LocationObject;
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

    RawContext context = RawContext.get(this);

    Location location = getLocationFromUrl(url, context);
    return new LocationObject(location, url);
  }

  @CompilerDirectives.TruffleBoundary
  private Location getLocationFromUrl(String url, RawContext context) {
    Either<String, Location> maybeLocation =
        LocationDescription$.MODULE$.urlToLocation(
            url, context.getProgramEnvironment(), context.getSettings());
    if (maybeLocation.isLeft()) {
      throw new RawTruffleRuntimeException(maybeLocation.left().get());
    }
    return maybeLocation.right().get();
  }
}
