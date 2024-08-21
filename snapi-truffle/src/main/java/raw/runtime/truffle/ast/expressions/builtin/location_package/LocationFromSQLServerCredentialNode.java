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

package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.jdbc.api.JdbcServerLocation;
import com.rawlabs.utils.sources.jdbc.sqlserver.SqlServerServerLocation;
import raw.protocol.LocationConfig;
import raw.protocol.SQLServerConfig;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.*;

@NodeInfo(shortName = "Location.FromSQLServerCredential")
public class LocationFromSQLServerCredentialNode extends ExpressionNode {

  @Child private ExpressionNode credentialName;

  public LocationFromSQLServerCredentialNode(ExpressionNode credentialName) {
    this.credentialName = credentialName;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    RawContext context = RawContext.get(this);

    String credentialName = (String) this.credentialName.executeGeneric(frame);
    LocationConfig l = context.getLocationConfig(credentialName);
    JdbcServerLocation location = getJdbcServerLocation(l, context.getSettings());

    return new LocationObject(location, "sqlserver:" + credentialName);
  }

  @CompilerDirectives.TruffleBoundary
  public JdbcServerLocation getJdbcServerLocation(LocationConfig l, RawSettings rawSettings) {
    if (l.hasSqlserver()) {
      SQLServerConfig sqlserver = l.getSqlserver();
      return new SqlServerServerLocation(
          sqlserver.getHost(),
          sqlserver.getPort(),
          sqlserver.getDatabase(),
          sqlserver.getUser(),
          sqlserver.getPassword(),
          rawSettings);
    } else {
      throw new RawTruffleRuntimeException("credential is not a SQL Server server");
    }
  }
}
