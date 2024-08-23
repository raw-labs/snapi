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

package com.rawlabs.snapi.truffle.runtime.exceptions.csv;

import com.oracle.truffle.api.nodes.Node;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.parser.TruffleCsvParser;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharStream;

public class CsvExpectedNothingException extends CsvParserTruffleException {
  public CsvExpectedNothingException(
      String token, TruffleCsvParser p, TruffleCharStream stream, Node location) {
    super(String.format("unexpected value found, token '%s'", token), p, stream, location);
  }
}
