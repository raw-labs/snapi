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

package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateInline;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;

public class Lambda implements TruffleObject {

  public static final int INLINE_CACHE_SIZE = 3;
  private final RootCallTarget callTarget;
  private final VirtualFrame frame;

  public Lambda(RootCallTarget callTarget, VirtualFrame frame) {
    this.frame = frame;
    this.callTarget = callTarget;
  }

  public RootCallTarget getCallTarget() {
    return callTarget;
  }

  @NodeInfo(shortName = "Lambda.ExecuteZero")
  @GenerateUncached
  @GenerateInline
  public abstract static class LambdaExecuteZeroNode extends Node {

    public abstract Object execute(Node node, Lambda lambda);

    @Specialization(guards = "lambda.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Node node,
        Lambda lambda,
        @Cached("lambda.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(lambda.frame);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node, Lambda lambda, @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(lambda.getCallTarget(), lambda.frame);
    }
  }

  @NodeInfo(shortName = "Lambda.ExecuteOne")
  @GenerateUncached
  @GenerateInline
  public abstract static class LambdaExecuteOneNode extends Node {

    public abstract Object execute(Node node, Lambda lambda, Object argument);

    @Specialization(guards = "lambda.getCallTarget() == cachedTarget", limit = "8")
    protected static Object doDirect(
        Node node,
        Lambda lambda,
        Object argument,
        @Cached("lambda.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(lambda.frame, argument);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        Lambda lambda,
        Object argument,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(lambda.getCallTarget(), lambda.frame, argument);
    }
  }

  @NodeInfo(shortName = "Lambda.ExecuteTwo")
  @GenerateUncached
  @GenerateInline
  public abstract static class LambdaExecuteTwoNode extends Node {

    public abstract Object execute(Node node, Lambda lambda, Object argument1, Object argument2);

    @Specialization(guards = "lambda.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Node node,
        Lambda lambda,
        Object argument1,
        Object argument2,
        @Cached("lambda.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      return callNode.call(lambda.frame, argument1, argument2);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        Lambda lambda,
        Object argument1,
        Object argument2,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(lambda.getCallTarget(), lambda.frame, argument1, argument2);
    }
  }
}
