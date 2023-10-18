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
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.tryable.ErrorTryable;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

// This node is a top level wrapper node that catches the Initialization of a child parser failures
@NodeInfo(shortName = "TryableParseJsonWrapper")
public class TryableTopLevelWrapper extends ExpressionNode {

  @Child private ExpressionNode reader;

  @Child
  private TryableNullableNodes.BoxTryableNode boxTryable =
      TryableNullableNodesFactory.BoxTryableNodeGen.create();

  @Child private TryableLibrary tryables = TryableLibrary.getFactory().createDispatched(1);

  public TryableTopLevelWrapper(ExpressionNode reader) {
    this.reader = reader;
  }

  public Object executeGeneric(VirtualFrame frame) {
    try {
      Object result = reader.executeGeneric(frame);
      if (tryables.isTryable(result)) {
        return result;
      }
      return boxTryable.execute(result);
    } catch (RawTruffleRuntimeException ex) {
      return ErrorTryable.BuildFailure(ex.getMessage());
    }
  }
}