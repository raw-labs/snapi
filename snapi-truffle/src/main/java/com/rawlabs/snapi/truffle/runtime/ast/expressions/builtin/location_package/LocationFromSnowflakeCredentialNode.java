/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.protocol.compiler.LocationConfig;
import com.rawlabs.protocol.compiler.SnowflakeConfig;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawContext;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.*;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.jdbc.api.JdbcServerLocation;
import com.rawlabs.utils.sources.jdbc.snowflake.SnowflakeServerLocation;

@NodeInfo(shortName = "Location.FromSnowflakeCredential")
public class LocationFromSnowflakeCredentialNode extends ExpressionNode {

  @Child private ExpressionNode credentialName;

  public LocationFromSnowflakeCredentialNode(ExpressionNode credentialName) {
    this.credentialName = credentialName;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    RawContext context = RawContext.get(this);

    String credentialName = (String) this.credentialName.executeGeneric(frame);
    LocationConfig l = context.getLocationConfig(credentialName);
    JdbcServerLocation location = getJdbcServerLocation(l, context.getSettings());

    return new LocationObject(location, "snowflake:" + credentialName);
  }

  @CompilerDirectives.TruffleBoundary
  public JdbcServerLocation getJdbcServerLocation(LocationConfig l, RawSettings rawSettings) {
    if (l.hasSnowflake()) {
      SnowflakeConfig snowflake = l.getSnowflake();
      return new SnowflakeServerLocation(
          snowflake.getDatabase(),
          snowflake.getUser(),
          snowflake.getPassword(),
          snowflake.getAccountIdentifier(),
          snowflake.getParameters(),
          rawSettings);
    } else {
      throw new RawTruffleRuntimeException("credential is not a Snowflake server");
    }
  }
}
