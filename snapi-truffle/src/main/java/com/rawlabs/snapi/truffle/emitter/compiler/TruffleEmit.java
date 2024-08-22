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
import com.rawlabs.snapi.frontend.common.source.Exp;
import com.rawlabs.snapi.frontend.common.source.SourceProgram;
import com.rawlabs.snapi.frontend.rql2.ProgramContext;
import com.rawlabs.snapi.frontend.rql2.Tree;
import com.rawlabs.snapi.frontend.rql2.source.*;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.StatementNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.controlflow.ExpBlockNode;
import scala.collection.JavaConverters;

public class TruffleEmit {
  public static Entrypoint doEmit(
      SourceProgram program,
      RawLanguage language,
      com.rawlabs.snapi.frontend.base.ProgramContext programContext) {
    ProgramContext ctx = (com.rawlabs.snapi.frontend.rql2.ProgramContext) programContext;
    Tree tree = new Tree(program, true, ctx);
    SnapiTruffleEmitter emitter = new SnapiTruffleEmitter(tree, language, ctx);
    Rql2Program prog = (Rql2Program) tree.root();

    Exp bodyExp = (prog.me().isDefined()) ? prog.me().get() : new IntConst("0");
    emitter.addScope();
    StatementNode[] functionDeclarations =
        JavaConverters.asJavaCollection(prog.methods()).stream()
            .map(emitter::emitMethod)
            .toArray(StatementNode[]::new);
    ExpressionNode body = emitter.recurseExp(bodyExp);
    ExpressionNode bodyExpNode =
        functionDeclarations.length != 0 ? new ExpBlockNode(functionDeclarations, body) : body;

    FrameDescriptor frameDescriptor = emitter.dropScope();
    RootNode rootNode;
    rootNode = new ProgramExpressionNode(language, frameDescriptor, bodyExpNode);
    return new TruffleEntrypoint(rootNode, frameDescriptor);
  }
}
