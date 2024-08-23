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

package com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser;

public class TruffleXmlParserSettings {

  protected final String dateFormat;
  protected final String timeFormat;
  protected final String timestampFormat;

  public TruffleXmlParserSettings(String dateFormat, String timeFormat, String timestampFormat) {
    this.dateFormat = dateFormat;
    this.timeFormat = timeFormat;
    this.timestampFormat = timestampFormat;
  }
}
