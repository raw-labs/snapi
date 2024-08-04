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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.*;
import raw.sources.jdbc.api.JdbcServerLocation;
import raw.sources.jdbc.pgsql.PostgresqlServerLocation;

@NodeInfo(shortName = "Location.FromPostgreSQL")
public class LocationFromPostgreSQLNode extends ExpressionNode {

  @Child private ExpressionNode host;
  @Child private ExpressionNode port;
  @Child private ExpressionNode db;
  @Child private ExpressionNode username;
  @Child private ExpressionNode password;

  public LocationFromPostgreSQLNode(
      ExpressionNode host,
      ExpressionNode port,
      ExpressionNode db,
      ExpressionNode username,
      ExpressionNode password) {
    this.host = host;
    this.port = port;
    this.db = db;
    this.username = username;
    this.password = password;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    String host = (String) this.host.executeGeneric(frame);
    int port = (int) this.port.executeGeneric(frame);
    String db = (String) this.db.executeGeneric(frame);
    String username = (String) this.username.executeGeneric(frame);
    String password = (String) this.password.executeGeneric(frame);

    JdbcServerLocation location =
        new PostgresqlServerLocation(
            host, port, db, username, password, RawContext.get(this).getSettings());

    return new LocationObject(location);
  }
}