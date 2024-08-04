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

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import raw.compiler.rql2.api.LocationDescription$;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.sources.api.Location;
import raw.utils.RawSettings;
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
    return new LocationObject(location);
  }

  @CompilerDirectives.TruffleBoundary
  private Location getLocationFromUrl(String url, RawContext context) {
    Either<String, Location> maybeLocation = LocationDescription$.MODULE$.urlToLocation(url, context.getProgramEnvironment(), context.getSettings());
    if (maybeLocation.isLeft()) {
      throw new RawTruffleRuntimeException(maybeLocation.left().get());
    }
    return maybeLocation.right().get();
  }

}
