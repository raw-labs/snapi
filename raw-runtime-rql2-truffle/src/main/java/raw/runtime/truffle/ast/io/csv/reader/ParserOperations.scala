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

package raw.runtime.truffle.ast.io.csv.reader

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.TruffleLogger
import com.oracle.truffle.api.dsl.GenerateUncached
import com.oracle.truffle.api.dsl.Specialization
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.NodeInfo
import raw.runtime.truffle.RawLanguage
import raw.runtime.truffle.ast.io.csv.reader.parser.{RawTruffleCsvParser, RawTruffleCsvParserSettings}
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException
import raw.runtime.truffle.utils.{RawTruffleStringCharStream, TruffleCharInputStream}

object ParserOperations {
  private val LOG = TruffleLogger.getLogger(RawLanguage.ID, classOf[RawTruffleRuntimeException])
  @NodeInfo(shortName = "Parser.Initialize") @GenerateUncached abstract class InitCsvParserNode extends Node {
    def execute(value: AnyRef, delimiter: Char, nulls: Array[String], nans: Array[String]): RawTruffleCsvParser
    @Specialization @CompilerDirectives.TruffleBoundary
    private[reader] def initParserFromString(str: String, settings: RawTruffleCsvParserSettings) =
      new RawTruffleCsvParser(new RawTruffleStringCharStream(str), settings)
    @Specialization @CompilerDirectives.TruffleBoundary
    private[reader] def initParserFromStream(stream: TruffleCharInputStream, settings: RawTruffleCsvParserSettings) =
      new RawTruffleCsvParser(stream, settings)
  }
  @NodeInfo(shortName = "Parser.Close") @GenerateUncached abstract class CloseCsvParserNode extends Node {
    def execute(parser: RawTruffleCsvParser): Unit
    @Specialization @CompilerDirectives.TruffleBoundary
    private[reader] def closeParserSilently(parser: RawTruffleCsvParser): Unit = { if (parser != null) parser.close() }
  }
}
