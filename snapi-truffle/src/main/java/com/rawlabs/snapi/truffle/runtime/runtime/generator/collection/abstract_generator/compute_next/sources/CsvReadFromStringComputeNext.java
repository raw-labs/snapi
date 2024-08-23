/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.rawlabs.snapi.truffle.runtime.ast.io.csv.reader.parser.TruffleCsvParser;
import com.rawlabs.snapi.truffle.runtime.ast.io.csv.reader.parser.TruffleCsvParserSettings;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharStream;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleStringCharStream;

public class CsvReadFromStringComputeNext {
  private final TruffleCharStream stream;
  private final RootCallTarget rowParserCallTarget;
  private final TruffleCsvParserSettings settings;
  private TruffleCsvParser parser;

  public CsvReadFromStringComputeNext(
      String str, RootCallTarget rowParserCallTarget, TruffleCsvParserSettings settings) {
    this.rowParserCallTarget = rowParserCallTarget;
    this.settings = settings;
    this.stream = new TruffleStringCharStream(str);
  }

  public void setParser(TruffleCsvParser parser) {
    this.parser = parser;
  }

  public TruffleCharStream getStream() {
    return stream;
  }

  public RootCallTarget getRowParserCallTarget() {
    return rowParserCallTarget;
  }

  public TruffleCsvParserSettings getSettings() {
    return settings;
  }

  public TruffleCsvParser getParser() {
    return parser;
  }
}
