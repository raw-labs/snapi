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

package raw.runtime.truffle.runtime.exceptions.json;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;

public class JsonParserRawTruffleException extends RawTruffleRuntimeException {
  public JsonParserRawTruffleException(String message) {
    super(message);
  }

  public JsonParserRawTruffleException(String message, Node location) {
    super(message);
  }

  public JsonParserRawTruffleException(String message, Throwable cause, Node location) {
    super(message, cause, location);
  }
}
