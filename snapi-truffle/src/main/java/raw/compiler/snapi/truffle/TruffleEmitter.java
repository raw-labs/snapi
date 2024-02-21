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

package raw.compiler.snapi.truffle;

import com.oracle.truffle.api.frame.FrameDescriptor;
import raw.compiler.common.source.Exp;
import raw.compiler.rql2.source.Rql2Method;
import raw.compiler.snapi.truffle.compiler.TruffleBuildBody;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.expressions.function.ClosureNode;

public abstract class TruffleEmitter {
  protected abstract void addScope();

  protected abstract FrameDescriptor dropScope();

  public abstract ExpressionNode recurseExp(Exp in);

  public abstract ClosureNode recurseLambda(TruffleBuildBody truffleBuildBody);

  public abstract FrameDescriptor.Builder getFrameDescriptorBuilder();

  protected abstract RawLanguage getLanguage();

  protected abstract StatementNode emitMethod(Rql2Method m);
}
