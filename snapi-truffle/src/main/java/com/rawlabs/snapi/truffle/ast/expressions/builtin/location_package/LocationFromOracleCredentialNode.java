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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.protocol.compiler.LocationConfig;
import com.rawlabs.protocol.compiler.OracleConfig;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.Rql2Context;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.jdbc.api.JdbcServerLocation;
import com.rawlabs.utils.sources.jdbc.oracle.OracleServerLocation;

@NodeInfo(shortName = "Location.FromOracleCredential")
public class LocationFromOracleCredentialNode extends ExpressionNode {

  @Child private ExpressionNode credentialName;

  public LocationFromOracleCredentialNode(ExpressionNode credentialName) {
    this.credentialName = credentialName;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    Rql2Context context = Rql2Context.get(this);

    String credentialName = (String) this.credentialName.executeGeneric(frame);
    LocationConfig l = context.getLocationConfig(credentialName);
    JdbcServerLocation location = getJdbcServerLocation(l, context.getSettings());

    return new LocationObject(location, "oracle:" + credentialName);
  }

  @CompilerDirectives.TruffleBoundary
  public JdbcServerLocation getJdbcServerLocation(LocationConfig l, RawSettings rawSettings) {
    if (l.hasOracle()) {
      OracleConfig oracle = l.getOracle();
      return new OracleServerLocation(
          oracle.getHost(),
          oracle.getPort(),
          oracle.getDatabase(),
          oracle.getUser(),
          oracle.getPassword(),
          rawSettings);
    } else {
      throw new TruffleRuntimeException("credential is not an Oracle server");
    }
  }
}
