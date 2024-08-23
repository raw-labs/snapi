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
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.Rql2Context;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleInternalErrorException;
import com.rawlabs.snapi.truffle.runtime.runtime.list.ListNodes;
import com.rawlabs.snapi.truffle.runtime.runtime.list.ListNodesFactory;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.*;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.sources.jdbc.api.JdbcServerLocation;
import com.rawlabs.utils.sources.jdbc.snowflake.SnowflakeServerLocation;
import java.util.HashMap;
import java.util.Map;

@NodeInfo(shortName = "Location.FromSnowflake")
public class LocationFromSnowflakeNode extends ExpressionNode {

  @Child private ExpressionNode db;
  @Child private ExpressionNode username;
  @Child private ExpressionNode password;
  @Child private ExpressionNode accountID;
  @Child private ExpressionNode options;

  @Child private InteropLibrary interops = InteropLibrary.getFactory().createDispatched(3);
  @Child private ListNodes.SizeNode sizeNode = ListNodesFactory.SizeNodeGen.create();
  @Child private ListNodes.GetNode getNode = ListNodesFactory.GetNodeGen.create();

  public LocationFromSnowflakeNode(
      ExpressionNode db,
      ExpressionNode username,
      ExpressionNode password,
      ExpressionNode accountID,
      ExpressionNode options) {
    this.db = db;
    this.username = username;
    this.password = password;
    this.accountID = accountID;
    this.options = options;
  }

  @Override
  public Object executeGeneric(VirtualFrame frame) {
    try {
      String db = (String) this.db.executeGeneric(frame);
      String username = (String) this.username.executeGeneric(frame);
      String password = (String) this.password.executeGeneric(frame);
      String accountID = (String) this.accountID.executeGeneric(frame);

      // Build args vector
      Map<String, String> parameters = new HashMap<>();
      if (this.options != null) {
        Object value = this.options.executeGeneric(frame);
        int size = (int) sizeNode.execute(this, value);
        for (int i = 0; i < size; i++) {
          Object record = getNode.execute(this, value, i);
          Object keys = interops.getMembers(record);
          Object key = interops.readMember(record, (String) interops.readArrayElement(keys, 0));
          Object val = interops.readMember(record, (String) interops.readArrayElement(keys, 1));
          // ignore entries where key or val is null
          if (key != NullObject.INSTANCE && val != NullObject.INSTANCE) {
            parameters.put((String) key, (String) val);
          }
        }
      }

      JdbcServerLocation location =
          getJdbcServerLocation(
              db, username, password, accountID, parameters, Rql2Context.get(this).getSettings());

      return new LocationObject(location, "snowflake:" + db);
    } catch (UnsupportedMessageException
        | InvalidArrayIndexException
        | UnknownIdentifierException e) {
      throw new TruffleInternalErrorException(e, this);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public JdbcServerLocation getJdbcServerLocation(
      String db,
      String username,
      String password,
      String accountID,
      Map<String, String> parameters,
      RawSettings rawSettings) {
    return new SnowflakeServerLocation(db, username, password, accountID, parameters, rawSettings);
  }
}
