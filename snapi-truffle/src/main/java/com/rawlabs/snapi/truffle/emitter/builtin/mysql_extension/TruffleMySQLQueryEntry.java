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

package com.rawlabs.snapi.truffle.emitter.builtin.mysql_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.MySQLQueryEntry;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package.LocationFromMySQLCredentialNode;
import com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package.LocationFromMySQLNode;
import com.rawlabs.snapi.truffle.ast.expressions.literals.IntNode;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.emitter.builtin.WithArgs;
import com.rawlabs.snapi.truffle.emitter.builtin.jdbc.Jdbc;
import com.rawlabs.snapi.truffle.runtime.exceptions.rdbms.MySQLExceptionHandler;
import java.util.List;

public class TruffleMySQLQueryEntry extends MySQLQueryEntry
    implements TruffleEntryExtension, WithArgs {

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, Rql2Language rawLanguage) {
    ExpressionNode db = args.get(0).exprNode();
    ExpressionNode query = args.get(1).exprNode();

    ExpressionNode location;

    ExpressionNode host = arg(args, "host").orElse(null);
    if (host == null) {
      location = new LocationFromMySQLCredentialNode(db);
    } else {
      ExpressionNode port = arg(args, "port").orElse(new IntNode(3306));
      ExpressionNode username = arg(args, "username").get();
      ExpressionNode password = arg(args, "password").get();
      location = new LocationFromMySQLNode(host, port, db, username, password);
    }

    return Jdbc.query(location, query, type, new MySQLExceptionHandler(), rawLanguage);
  }
}
