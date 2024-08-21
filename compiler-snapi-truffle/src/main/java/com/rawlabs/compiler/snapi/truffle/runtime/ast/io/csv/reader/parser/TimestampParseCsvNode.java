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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.io.csv.reader.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.TimestampObject;

@NodeInfo(shortName = "TimestampParseCsv")
public class TimestampParseCsvNode extends ExpressionNode {

  public TimestampObject executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    RawTruffleCsvParser parser = (RawTruffleCsvParser) args[0];
    return parser.getTimestamp(this);
  }
}
