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

package raw.runtime.truffle.runtime.exceptions.csv;

import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.utils.RawTruffleCharStream;

public class CsvExpectedNothingException extends CsvParserRawTruffleException {
  public CsvExpectedNothingException(
      String token, RawTruffleCsvParser p, RawTruffleCharStream stream, Node location) {
    super(String.format("unexpected value found, token '%s'", token), p, stream, location);
  }
}
