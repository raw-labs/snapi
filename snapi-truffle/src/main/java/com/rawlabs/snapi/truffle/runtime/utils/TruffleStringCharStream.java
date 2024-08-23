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

package com.rawlabs.snapi.truffle.runtime.utils;

import java.io.Reader;
import java.io.StringReader;

public class TruffleStringCharStream extends TruffleCharStream {

  private final String string;

  public TruffleStringCharStream(String content) {
    this.string = content;
  }

  public Reader getReader() {
    return new StringReader(string);
  }

  public String positionDescription() {
    return null;
  }
}
