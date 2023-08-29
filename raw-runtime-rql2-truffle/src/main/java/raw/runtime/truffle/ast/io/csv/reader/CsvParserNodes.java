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

package raw.runtime.truffle.ast.io.csv.reader;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.utils.RawTruffleCharStream;

public class CsvParserNodes {

  @NodeInfo(shortName = "Parser.Initialize")
  @GenerateUncached
  public abstract static class InitCsvParserNode extends Node {

    public abstract RawTruffleCsvParser execute(Object value, RawTruffleCsvParserSettings settings);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    RawTruffleCsvParser initParserFromStream(
        RawTruffleCharStream stream, RawTruffleCsvParserSettings settings) {
      return new RawTruffleCsvParser(stream, settings);
    }
  }

  @NodeInfo(shortName = "Parser.Close")
  @GenerateUncached
  public abstract static class CloseCsvParserNode extends Node {

    public abstract void execute(RawTruffleCsvParser parser);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    void closeParserSilently(RawTruffleCsvParser parser) {
      if (parser != null) {
        parser.close();
      }
    }
  }
}
