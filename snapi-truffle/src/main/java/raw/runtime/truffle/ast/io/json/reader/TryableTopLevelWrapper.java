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

package raw.runtime.truffle.ast.io.json.reader;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

// This node is a top level wrapper node that catches the Initialization of a child parser failures
@NodeInfo(shortName = "TryableParseJsonWrapper")
public class TryableTopLevelWrapper extends ExpressionNode {

  @Child private ExpressionNode reader;

  public TryableTopLevelWrapper(ExpressionNode reader) {
    this.reader = reader;
  }

  public Object executeGeneric(VirtualFrame frame) {
    try {
      return reader.executeGeneric(frame);
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    }
  }
}
