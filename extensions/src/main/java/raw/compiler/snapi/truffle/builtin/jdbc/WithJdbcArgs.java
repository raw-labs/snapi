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

package raw.compiler.snapi.truffle.builtin.jdbc;

import java.util.List;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.runtime.truffle.ExpressionNode;

public interface WithJdbcArgs extends WithArgs {
  default ExpressionNode host(List<TruffleArg> args) {
    return arg(args, "host").orElse(null);
  }

  default ExpressionNode port(List<TruffleArg> args) {
    return arg(args, "port").orElse(null);
  }

  default ExpressionNode username(List<TruffleArg> args) {
    return arg(args, "username").orElse(null);
  }

  default ExpressionNode password(List<TruffleArg> args) {
    return arg(args, "password").orElse(null);
  }
}
