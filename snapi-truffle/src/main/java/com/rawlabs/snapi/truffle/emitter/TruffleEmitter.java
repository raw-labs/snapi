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

package com.rawlabs.snapi.truffle.emitter;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.rawlabs.snapi.frontend.rql2.source.Exp;
import com.rawlabs.snapi.frontend.rql2.source.Rql2Method;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.expressions.function.ClosureNode;

public abstract class TruffleEmitter {
  protected abstract void addScope();

  protected abstract FrameDescriptor dropScope();

  public abstract ExpressionNode recurseExp(Exp in);

  public abstract ClosureNode recurseLambda(TruffleBuildBody truffleBuildBody);

  public abstract FrameDescriptor.Builder getFrameDescriptorBuilder();

  public abstract Rql2Language getLanguage();

  protected abstract StatementNode emitMethod(Rql2Method m);
}
