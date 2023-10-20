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

package raw.compiler.snapi.truffle.compiler;

//import static raw.runtime.truffle.RawOptions.OUTPUT_FORMAT;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import raw.compiler.base.source.Type;
import raw.compiler.common.source.Exp;
import raw.compiler.common.source.SourceProgram;
import raw.compiler.rql2.ProgramContext;
import raw.compiler.rql2.Tree;
//import raw.compiler.rql2.builtin.BinaryPackage;
//import raw.compiler.rql2.builtin.CsvPackage;
//import raw.compiler.rql2.builtin.JsonPackage;
//import raw.compiler.rql2.builtin.StringPackage;
import raw.compiler.rql2.source.*;
//import raw.compiler.rql2output.truffle.builtin.CsvWriter;
//import raw.compiler.rql2output.truffle.builtin.JsonWriter;
//import raw.compiler.rql2output.truffle.builtin.TruffleBinaryWriter;
import raw.client.api.Entrypoint;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
//import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.ast.controlflow.ExpBlockNode;
//import raw.runtime.truffle.ast.io.binary.BinaryWriterNode;
//import raw.runtime.truffle.ast.io.csv.writer.CsvIterableWriterNode;
//import raw.runtime.truffle.ast.io.csv.writer.CsvListWriterNode;
//import raw.runtime.truffle.ast.io.json.writer.JsonWriterNodeGen;
//import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
//import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
//import scala.Option;
import scala.collection.JavaConverters;

public class TruffleEmit {
  private static final String WINDOWS_LINE_ENDING = "raw.compiler.windows-line-ending";

  public static Entrypoint doEmit(
      SourceProgram program,
      RawLanguage language,
      raw.compiler.base.ProgramContext programContext) {
    ProgramContext ctx = (raw.compiler.rql2.ProgramContext) programContext;
    Tree tree = new Tree(program, true, ctx);
    SnapiTruffleEmitter emitter = new SnapiTruffleEmitter(tree, language, ctx);
    Rql2Program prog = (Rql2Program) tree.root();
//    Type dataType = tree.analyzer().tipe(prog.me().get());
//    String outputFormat =
//        ctx.runtimeContext()
//            .environment()
//            .options()
//            .getOrElse(
//                "output-format",
//                () -> ctx.compilerContext().settings().getString(OUTPUT_FORMAT, true));
//
//    switch (outputFormat) {
//      case "csv":
//        if (!CsvPackage.outputWriteSupport(dataType))
//          throw new RawTruffleRuntimeException("unsupported type");
//        break;
//      case "json":
//        if (!JsonPackage.outputWriteSupport(dataType))
//          throw new RawTruffleRuntimeException("unsupported type");
//        break;
//      case "text":
//        if (!StringPackage.outputWriteSupport(dataType))
//          throw new RawTruffleRuntimeException("unsupported type");
//        break;
//      case "binary":
//        if (!BinaryPackage.outputWriteSupport(dataType))
//          throw new RawTruffleRuntimeException("unsupported type");
//        break;
//      case null:
//      case "":
//        break;
//      default:
//        throw new RawTruffleRuntimeException("unknown output format");
//    }

    assert prog.me().isDefined();

    Exp bodyExp = prog.me().get();
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

//    boolean windowsLineEnding;
//    assert outputFormat != null;
//    switch (outputFormat) {
//      case "csv" -> {
//        Option<String> lineEnding =
//            programContext.runtimeContext().environment().options().get("windows-line-ending");
//        if (lineEnding.isDefined()) {
//          windowsLineEnding = lineEnding.get().equals("true");
//        } else {
//          windowsLineEnding = programContext.settings().getBoolean(WINDOWS_LINE_ENDING);
//        }
//        String lineSeparator = windowsLineEnding ? "\r\n" : "\n";
//        rootNode =
//            switch (dataType) {
//              case Rql2IterableType iterable -> {
//                Rql2RecordType record = (Rql2RecordType) iterable.innerType();
//                assert record.props().isEmpty();
//                assert iterable.props().isEmpty();
//                yield new ProgramStatementNode(
//                    language,
//                    frameDescriptor,
//                    new CsvIterableWriterNode(
//                        bodyExpNode,
//                        CsvWriter.getCsvWriter(
//                            JavaConverters.asJavaCollection(record.atts()).stream()
//                                .map(Rql2AttrType::tipe)
//                                .toArray(Type[]::new),
//                            language),
//                        JavaConverters.asJavaCollection(record.atts()).stream()
//                            .map(Rql2AttrType::idn)
//                            .toArray(String[]::new),
//                        lineSeparator));
//              }
//              case Rql2ListType list -> {
//                Rql2RecordType record = (Rql2RecordType) list.innerType();
//                assert record.props().isEmpty();
//                assert list.props().isEmpty();
//                yield new ProgramStatementNode(
//                    language,
//                    frameDescriptor,
//                    new CsvListWriterNode(
//                        bodyExpNode,
//                        CsvWriter.getCsvWriter(
//                            JavaConverters.asJavaCollection(record.atts()).stream()
//                                .map(Rql2AttrType::tipe)
//                                .toArray(Type[]::new),
//                            language),
//                        JavaConverters.asJavaCollection(record.atts()).stream()
//                            .map(Rql2AttrType::idn)
//                            .toArray(String[]::new),
//                        lineSeparator));
//              }
//              default -> throw new RawTruffleInternalErrorException();
//            };
//      }
//      case "json" -> rootNode =
//          new ProgramStatementNode(
//              language,
//              frameDescriptor,
//              JsonWriterNodeGen.create(
//                  bodyExpNode, JsonWriter.recurse((Rql2TypeWithProperties) dataType, language)));
//      case "binary" -> {
//        ProgramStatementNode writer =
//            TruffleBinaryWriter.getBinaryWriterNode(
//                (Rql2BinaryType) dataType, language, frameDescriptor);
//        rootNode =
//            new ProgramStatementNode(
//                language, frameDescriptor, new BinaryWriterNode(bodyExpNode, writer));
//      }
//      case "text" -> {
//        ProgramStatementNode writer =
//            TruffleBinaryWriter.getBinaryWriterNode(
//                (Rql2StringType) dataType, language, frameDescriptor);
//        rootNode =
//            new ProgramStatementNode(
//                language, frameDescriptor, new BinaryWriterNode(bodyExpNode, writer));
//      }
//      default -> rootNode = new ProgramExpressionNode(language, frameDescriptor, bodyExpNode);
//    }

    rootNode = new ProgramExpressionNode(language, frameDescriptor, bodyExpNode);

    return new TruffleEntrypoint(rootNode, frameDescriptor);
  }
}
