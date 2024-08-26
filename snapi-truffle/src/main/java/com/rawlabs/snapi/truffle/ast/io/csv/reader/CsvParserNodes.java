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

package com.rawlabs.snapi.truffle.ast.io.csv.reader;

import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.parser.TruffleCsvParser;
import com.rawlabs.snapi.truffle.ast.io.csv.reader.parser.TruffleCsvParserSettings;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharStream;

public class CsvParserNodes {

  @NodeInfo(shortName = "Parser.Initialize")
  @GenerateUncached
  @GenerateInline
  public abstract static class InitCsvParserNode extends Node {

    public abstract TruffleCsvParser execute(
        Node node, Object value, TruffleCsvParserSettings settings);

    @Specialization
    TruffleCsvParser initParserFromStream(
        Node node, TruffleCharStream stream, TruffleCsvParserSettings settings) {
      return new TruffleCsvParser(stream, settings);
    }
  }

  @NodeInfo(shortName = "Parser.Close")
  @GenerateUncached
  @GenerateInline
  public abstract static class CloseCsvParserNode extends Node {

    public abstract void execute(Node node, TruffleCsvParser parser);

    @Specialization
    void closeParserSilently(Node node, TruffleCsvParser parser) {
      if (parser != null) {
        parser.close();
      }
    }
  }
}
