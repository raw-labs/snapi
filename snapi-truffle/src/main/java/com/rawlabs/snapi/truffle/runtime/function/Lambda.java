/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.function;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.list.StringList;

// A function runtime object that doesn't have any default arguments and doesn't capture any free
// variables.
@ExportLibrary(InteropLibrary.class)
public class Lambda implements TruffleObject {
  private final RootCallTarget callTarget;

  public Lambda(RootCallTarget callTarget) {
    this.callTarget = callTarget;
  }

  public RootCallTarget getCallTarget() {
    return callTarget;
  }

  // interop execution
  @ExportMessage
  abstract static class Execute {
    @Specialization
    protected static Object doDirect(
        Lambda lambda,
        Object[] arguments,
        @Bind("$node") Node thisNode,
        @Cached(inline = true) LambdaExecuteManyNode executeManyNode) {
      return executeManyNode.execute(thisNode, lambda, arguments);
    }
  }

  @ExportMessage
  boolean isExecutable() {
    return true;
  }

  @ExportMessage
  final boolean hasMembers() {
    return true;
  }

  @ExportMessage
  final boolean isMemberInvocable(String member) {
    return false;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  final Object getMembers(boolean includeInternal) {
    return new StringList(new String[0]);
  }

  @ExportMessage
  final Object invokeMember(String member, Object... arguments)
      throws UnknownIdentifierException, ArityException {
    throw UnknownIdentifierException.create(member);
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
      return callNode.call(new Object[] {null});
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node, Lambda lambda, @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(lambda.getCallTarget(), new Object[] {null});
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
      return callNode.call(null, argument);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        Lambda lambda,
        Object argument,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(lambda.getCallTarget(), null, argument);
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
      return callNode.call(null, argument1, argument2);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        Lambda lambda,
        Object argument1,
        Object argument2,
        @Cached(inline = false) IndirectCallNode callNode) {
      return callNode.call(lambda.getCallTarget(), null, argument1, argument2);
    }
  }

  @NodeInfo(shortName = "Lambda.ExecuteMany")
  @GenerateUncached
  @GenerateInline
  public abstract static class LambdaExecuteManyNode extends Node {

    public abstract Object execute(Node node, Lambda lambda, Object[] arguments);

    @Specialization(guards = "lambda.getCallTarget() == cachedTarget", limit = "3")
    protected static Object doDirect(
        Node node,
        Lambda lambda,
        Object[] arguments,
        @Cached("lambda.getCallTarget()") RootCallTarget cachedTarget,
        @Cached("create(cachedTarget)") DirectCallNode callNode) {
      Object[] finalArgs = new Object[arguments.length + 1];
      finalArgs[0] = null;
      System.arraycopy(arguments, 0, finalArgs, 1, arguments.length);
      return callNode.call(finalArgs);
    }

    @Specialization(replaces = "doDirect")
    protected static Object doIndirect(
        Node node,
        Lambda lambda,
        Object[] arguments,
        @Cached(inline = false) IndirectCallNode callNode) {
      Object[] finalArgs = new Object[arguments.length + 1];
      finalArgs[0] = null;
      System.arraycopy(arguments, 0, finalArgs, 1, arguments.length);
      return callNode.call(lambda.getCallTarget(), finalArgs);
    }
  }
}
