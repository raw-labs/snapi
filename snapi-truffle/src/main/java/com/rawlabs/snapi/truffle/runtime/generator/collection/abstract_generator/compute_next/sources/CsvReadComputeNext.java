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

package com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.parser.TruffleCsvParser;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.parser.TruffleCsvParserSettings;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharInputStream;

public class CsvReadComputeNext {
  private final LocationObject location;
  private final RootCallTarget rowParserCallTarget;
  private final String encoding;
  private final TruffleCsvParserSettings settings;
  private TruffleCharInputStream stream;
  private TruffleCsvParser parser;

  public CsvReadComputeNext(
      LocationObject location,
      RootCallTarget rowParserCallTarget,
      String encoding,
      TruffleCsvParserSettings settings) {
    this.location = location;
    this.rowParserCallTarget = rowParserCallTarget;
    this.encoding = encoding;
    this.settings = settings;
  }

  public void setStream(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public void setParser(TruffleCsvParser parser) {
    this.parser = parser;
  }

  public LocationObject getLocation() {
    return location;
  }

  public RootCallTarget getRowParserCallTarget() {
    return rowParserCallTarget;
  }

  public String getEncoding() {
    return encoding;
  }

  public TruffleCsvParserSettings getSettings() {
    return settings;
  }

  public TruffleCharInputStream getStream() {
    return stream;
  }

  public TruffleCsvParser getParser() {
    return parser;
  }
}
