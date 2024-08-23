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

package com.rawlabs.snapi.truffle.runtime.ast;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.Rql2Language;

public final class ProgramExpressionNode extends RootNode {

  private static final Source DUMMY_SOURCE =
      Source.newBuilder(Rql2Language.ID, "", "dummy").build();

  @Child private ExpressionNode bodyNode;

  public ProgramExpressionNode(
      Rql2Language language, FrameDescriptor frameDescriptor, ExpressionNode body) {
    super(language, frameDescriptor);
    this.bodyNode = body;
    this.bodyNode.addRootTag();
  }

  @Override
  public SourceSection getSourceSection() {
    return DUMMY_SOURCE.createUnavailableSection();
  }

  @Override
  public Object execute(VirtualFrame frame) {
    return bodyNode.executeGeneric(frame);
  }

  @Override
  public String toString() {
    return bodyNode.toString();
  }
}
