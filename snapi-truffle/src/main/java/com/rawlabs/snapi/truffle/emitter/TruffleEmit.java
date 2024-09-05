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
import com.oracle.truffle.api.nodes.RootNode;
import com.rawlabs.compiler.Entrypoint;
import com.rawlabs.snapi.frontend.snapi.ProgramContext;
import com.rawlabs.snapi.frontend.snapi.Tree;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.frontend.snapi.source.Exp;
import com.rawlabs.snapi.frontend.snapi.source.SourceProgram;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.ast.StatementNode;
import com.rawlabs.snapi.truffle.ast.controlflow.ExpBlockNode;
import com.rawlabs.snapi.truffle.lineage.SnapiLineageGenerator;
import scala.collection.JavaConverters;

public class TruffleEmit {
  public static Entrypoint doEmit(
      SourceProgram program,
      SnapiLanguage language,
      com.rawlabs.snapi.frontend.base.ProgramContext programContext) {
    ProgramContext ctx = (com.rawlabs.snapi.frontend.snapi.ProgramContext) programContext;
    Tree tree = new Tree(program, true, ctx);

    String result = testLineageEmitter(tree, ctx);
    System.out.println("=======================================");
    System.out.println(result);
    System.out.println("=======================================");

    SnapiTruffleEmitter emitter = new SnapiTruffleEmitter(tree, language, ctx);
    SnapiProgram prog = (SnapiProgram) tree.root();

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

  private static String testLineageEmitter(Tree tree, ProgramContext ctx) {
    try {
      SnapiLineageGenerator generator = new SnapiLineageGenerator(tree, ctx);
      SnapiProgram prog = (SnapiProgram) tree.root();
      Exp bodyExp = (prog.me().isDefined()) ? prog.me().get() : new IntConst("0");
      JavaConverters.asJavaCollection(prog.methods()).forEach(generator::emitMethod);
      generator.recurseExp(bodyExp);
      return generator.getResult();
    } catch (Exception e) {
      return e.getMessage();
    }
  }
}
