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

package com.rawlabs.snapi.truffle.ast.expressions.literals;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.rawlabs.snapi.frontend.rql2.extensions.LocationDescription$;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.Rql2Context;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.api.Location;

public class LocationConstNode extends ExpressionNode {

  private final byte[] value;
  private final String publicDescription;

  public LocationConstNode(byte[] value, String publicDescription) {
    this.value = value;
    this.publicDescription = publicDescription;
  }

  @Override
  public final Object executeGeneric(VirtualFrame virtualFrame) {
    RawSettings rawSettings = Rql2Context.get(this).getSettings();
    Location location = getLocation(rawSettings);
    return new LocationObject(location, publicDescription);
  }

  @CompilerDirectives.TruffleBoundary
  public Location getLocation(RawSettings rawSettings) {
    return LocationDescription$.MODULE$.toLocation(
        LocationDescription$.MODULE$.deserialize(value), rawSettings);
  }
}
