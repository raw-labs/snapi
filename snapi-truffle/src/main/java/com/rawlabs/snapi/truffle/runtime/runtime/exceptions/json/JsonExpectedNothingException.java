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

package com.rawlabs.snapi.truffle.runtime.runtime.exceptions.json;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.Node;

public class JsonExpectedNothingException extends JsonParserTruffleException {

  @CompilerDirectives.TruffleBoundary
  public JsonExpectedNothingException(String token) {
    super(String.format("unexpected value found, token '%s'", token));
  }

  @CompilerDirectives.TruffleBoundary
  public JsonExpectedNothingException(String token, Throwable cause, Node location) {
    super(String.format("unexpected value found, token '%s'", token), cause, location);
  }
}
