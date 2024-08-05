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
import raw.client.api.JdbcLocation;
import raw.compiler.rql2.api.LocationDescription$;
import raw.compiler.rql2.api.PostgresqlServerLocationDescription;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
import raw.sources.jdbc.api.JdbcServerLocation;
import raw.sources.jdbc.pgsql.PostgresqlServerLocation;
import raw.utils.RawSettings;

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
    JdbcLocation l = context.getJdbcLocation(credentialName);
    JdbcServerLocation location = getJdbcServerLocation(l, context.getSettings());

    return new LocationObject(location, "pgsql:" + credentialName);
  }

  @CompilerDirectives.TruffleBoundary
  public JdbcServerLocation getJdbcServerLocation(JdbcLocation l, RawSettings rawSettings) {
    PostgresqlServerLocationDescription d =
        (PostgresqlServerLocationDescription) LocationDescription$.MODULE$.toLocationDescription(l);
    return new PostgresqlServerLocation(
        d.host(), d.port(), d.dbName(), d.username(), d.password(), rawSettings);
  }
}
