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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.nodes.Node;

public class JsonRecordFieldNotFoundException extends JsonParserRawTruffleException {

  @TruffleBoundary
  public JsonRecordFieldNotFoundException(String fieldName, Node location) {
    super(String.format("'%s': not found", fieldName), location);
  }

  @TruffleBoundary
  public JsonRecordFieldNotFoundException(String fieldName, Throwable cause, Node location) {
    super(String.format("'%s': not found", fieldName), cause, location);
  }
}
