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

package raw.compiler.snapi.truffle.builtin.oracle_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.OracleQueryEntry;
import java.util.List;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.compiler.snapi.truffle.builtin.jdbc.Jdbc;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationFromOracleCredentialNode;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationFromOracleNode;
import raw.runtime.truffle.ast.expressions.literals.IntNode;
import raw.runtime.truffle.runtime.exceptions.rdbms.OracleExceptionHandler;

public class TruffleOracleQueryEntry extends OracleQueryEntry
    implements TruffleEntryExtension, WithArgs {

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode db = args.get(0).exprNode();
    ExpressionNode query = args.get(1).exprNode();

    ExpressionNode location;

    ExpressionNode host = arg(args, "host").orElse(null);
    if (host == null) {
      location = new LocationFromOracleCredentialNode(db);
    } else {
      ExpressionNode port = arg(args, "port").orElse(new IntNode(1521));
      ExpressionNode username = arg(args, "username").get();
      ExpressionNode password = arg(args, "password").get();
      location = new LocationFromOracleNode(host, port, db, username, password);
    }

    return Jdbc.query(location, query, type, new OracleExceptionHandler(), rawLanguage);
  }
}
