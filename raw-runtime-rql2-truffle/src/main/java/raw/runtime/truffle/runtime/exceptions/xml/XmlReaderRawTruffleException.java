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

package raw.runtime.truffle.runtime.exceptions.xml;

import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public class XmlReaderRawTruffleException extends RawTruffleRuntimeException {

  public XmlReaderRawTruffleException(String message) {
    super(message);
  }

  public XmlReaderRawTruffleException(String message, Node location) {
    super(message);
  }

  public XmlReaderRawTruffleException(String message, Throwable cause, Node location) {
    super(message, cause, location);
  }
}
