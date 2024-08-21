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

package raw.compiler.snapi.truffle.builtin.snowflake_extension;

import java.util.List;
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.SnowflakeQueryEntry;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.compiler.snapi.truffle.builtin.jdbc.Jdbc;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationFromSnowflakeCredentialNode;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationFromSnowflakeNode;
import raw.runtime.truffle.runtime.exceptions.rdbms.SnowflakeExceptionHandler;

public class TruffleSnowflakeQueryEntry extends SnowflakeQueryEntry
    implements TruffleEntryExtension, WithArgs {

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode db = args.get(0).exprNode();
    ExpressionNode query = args.get(1).exprNode();

    ExpressionNode location;

    ExpressionNode accountID = arg(args, "accountID").orElse(null);
    if (accountID == null) {
      location = new LocationFromSnowflakeCredentialNode(db);
    } else {
      ExpressionNode username = arg(args, "username").get();
      ExpressionNode password = arg(args, "password").get();
      ExpressionNode options = arg(args, "options").orElse(null);
      location = new LocationFromSnowflakeNode(db, username, password, accountID, options);
    }

    return Jdbc.query(location, query, type, new SnowflakeExceptionHandler(), rawLanguage);
  }
}
