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

package com.rawlabs.snapi.truffle.runtime.ast.io.csv.reader.parser;

public class RawTruffleCsvParserSettings {

  public final char delimiter;
  public final boolean useQuote;
  public final char quoteChar;
  public final char escapeChar;
  public final int headerLines;
  public final String[] nulls;
  public final String[] nans;
  public final String dateFormat;
  public final String timeFormat;
  public final String timestampFormat;

  public RawTruffleCsvParserSettings(
      char delimiter,
      boolean useQuote,
      char quoteChar,
      char escapeChar,
      int headerLines,
      String[] nulls,
      String[] nans,
      String dateFormat,
      String timeFormat,
      String timestampFormat) {
    this.delimiter = delimiter;
    this.useQuote = useQuote;
    this.quoteChar = quoteChar;
    this.escapeChar = escapeChar;
    this.headerLines = headerLines;
    this.nulls = nulls;
    this.nans = nans;
    this.dateFormat = dateFormat;
    this.timeFormat = timeFormat;
    this.timestampFormat = timestampFormat;
  }
}
