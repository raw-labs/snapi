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

package raw.runtime.truffle.ast.io.csv.reader.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.csv.CsvParserRawTruffleException;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "TryableParseCsv")
public class TryableParseCsvNode extends ExpressionNode {

  @Child private DirectCallNode innerParse;

  public TryableParseCsvNode(ProgramExpressionNode innerParse) {
    this.innerParse = DirectCallNode.create(innerParse.getCallTarget());
  }

  public Object executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    RawTruffleCsvParser parser = (RawTruffleCsvParser) args[0];
    try {
      return innerParse.call(parser);
    } catch (CsvParserRawTruffleException ex) {
      return new ErrorObject(ex.getMessage());
    }
  }
}
