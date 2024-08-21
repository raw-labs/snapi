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
import com.rawlabs.compiler.protocol.LocationConfig;
import com.rawlabs.compiler.protocol.PostgreSQLConfig;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.jdbc.api.JdbcServerLocation;
import com.rawlabs.utils.sources.jdbc.pgsql.PostgresqlServerLocation;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.*;

@NodeInfo(shortName = "Location.FromPostgreSQLCredential")
public class LocationFromPostgreSQLCredentialNode extends ExpressionNode {

  @Child private ExpressionNode credentialName;

  public LocationFromPostgreSQLCredentialNode(ExpressionNode credentialName) {
    this.credentialName = credentialName;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    RawContext context = RawContext.get(this);

    String credentialName = (String) this.credentialName.executeGeneric(frame);
    LocationConfig l = context.getLocationConfig(credentialName);
    JdbcServerLocation location = getJdbcServerLocation(l, context.getSettings());

    return new LocationObject(location, "pgsql:" + credentialName);
  }

  @CompilerDirectives.TruffleBoundary
  public JdbcServerLocation getJdbcServerLocation(LocationConfig l, RawSettings rawSettings) {
    if (l.hasPostgresql()) {
      PostgreSQLConfig postgresql = l.getPostgresql();
      return new PostgresqlServerLocation(
          postgresql.getHost(),
          postgresql.getPort(),
          postgresql.getDatabase(),
          postgresql.getUser(),
          postgresql.getPassword(),
          rawSettings);
    } else {
      throw new RawTruffleRuntimeException("credential is not a PostgreSQL server");
    }
  }
}
