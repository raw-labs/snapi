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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions;

import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.compiler.snapi.truffle.runtime.utils.TruffleCharInputStream;

public class ReadLinesRawTruffleException extends RawTruffleRuntimeException {
  public ReadLinesRawTruffleException(
      String message, TruffleCharInputStream stream, Throwable cause, Node location) {
    super(createMessage(message, stream), cause, location);
  }

  private static String createMessage(String message, TruffleCharInputStream stream) {
    StringBuilder sb = new StringBuilder();
    sb.append("failed to read lines");
    if (stream.positionDescription() != null) {
      sb.append(" (");
      sb.append(stream.positionDescription());
      sb.append(")");
    }
    sb.append(": ");
    sb.append(message);
    return sb.toString();
  }
}
