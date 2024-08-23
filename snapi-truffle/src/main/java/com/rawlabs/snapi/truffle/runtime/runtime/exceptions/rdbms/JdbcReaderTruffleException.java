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

package com.rawlabs.snapi.truffle.runtime.runtime.exceptions.rdbms;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.runtime.ast.io.jdbc.JdbcQuery;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.TruffleRuntimeException;

public class JdbcReaderTruffleException extends TruffleRuntimeException {

  @CompilerDirectives.TruffleBoundary
  public JdbcReaderTruffleException(String message, JdbcQuery rs, Throwable e, Node location) {
    super(
        String.format("failed to read from database %s: %s", rs.getPublicDescription(), message),
        e,
        location);
  }
}
