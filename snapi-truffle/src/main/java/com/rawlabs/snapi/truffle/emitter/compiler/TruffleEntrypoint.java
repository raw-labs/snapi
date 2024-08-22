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

package com.rawlabs.snapi.truffle.emitter.compiler;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import com.rawlabs.compiler.Entrypoint;

public class TruffleEntrypoint implements Entrypoint {

  RootNode rootNode;

  FrameDescriptor frameDescriptor;

  public TruffleEntrypoint(RootNode rootNode, FrameDescriptor frameDescriptor) {
    this.rootNode = rootNode;
    this.frameDescriptor = frameDescriptor;
  }

  @Override
  public RootNode target() {
    return rootNode;
  }
}
