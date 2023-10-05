///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.compiler.snapi.truffle.compiler;
//
//import com.oracle.truffle.api.Truffle;
//import com.oracle.truffle.api.nodes.DirectCallNode;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//import org.graalvm.polyglot.Context;
//import raw.compiler.CompilerException;
//import raw.compiler.ErrorMessage;
//import raw.compiler.ProgramOutputWriter;
//import raw.compiler.base.*;
//import raw.compiler.base.source.BaseNode;
//import raw.compiler.base.source.Type;
//import raw.compiler.common.source.*;
//import raw.compiler.rql2.Compiler;
//import raw.compiler.rql2.builtin.EnvironmentPackageBuilder;
//import raw.compiler.rql2.source.*;
//import raw.compiler.truffle.TruffleCompiler;
//import raw.runtime.*;
//import raw.runtime.interpreter.Value;
//import raw.runtime.truffle.RawLanguage;
//import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
//import scala.*;
//import scala.collection.JavaConverters;
//import scala.collection.immutable.HashSet;
//import scala.collection.immutable.Vector;
//import scala.util.Either;
//import scala.util.Right;
//
//public class Rql2TruffleCompiler extends Compiler
//    implements TruffleCompiler<SourceNode, SourceProgram, Exp> {
//  private static final String WINDOWS_LINE_ENDING = "raw.compiler.windows-line-ending";
//
//  CompilerContext compilerContext;
//
//  public Rql2TruffleCompiler(CompilerContext compilerContext) {
//    super(compilerContext);
//  }
//
//  public void compile() {
//    throw new UnsupportedOperationException("Not implemented");
//  }
//
//  @Override
//  public Value doEval(
//      BaseTree<SourceNode, SourceProgram, Exp> tree, ProgramContext programContext) {
//    // FIXME (msb): This should pretty print the tree, then call Context.eval.
//    //              We shouldn't have access to doCompile and hence not do be rawLanguageAsNull as
//    // null, which is broken!
//    try (Context context =
//        Context.newBuilder(RawLanguage.ID)
//            .environment("RAW_USER", programContext.runtimeContext().environment().user().uid())
//            .environment("RAW_TRACE_ID", programContext.runtimeContext().environment().user().uid())
//            .environment(
//                "RAW_SCOPES", programContext.runtimeContext().environment().scopes().mkString(","))
//            .build(); ) {
//      context.initialize(RawLanguage.ID);
//      context.enter();
//      try {
//        TruffleEntrypoint entrypoint = (TruffleEntrypoint) doCompile(tree, null);
//        DirectCallNode target =
//            Truffle.getRuntime().createDirectCallNode(entrypoint.target().getCallTarget());
//        Object res = target.call();
//        return convertAnyToValue(res, tree.rootType().get());
//      } finally {
//        context.leave();
//        context.close();
//      }
//    }
//  }
//
//  @Override
//  public Either<scala.collection.immutable.List<ErrorMessage>, SourceProgram> parseAndValidate(
//      String source, Option<String> maybeDecl, ProgramContext programContext) {
//    return buildInputTree(source, programContext)
//        .right()
//        .flatMap(
//            tree -> {
//              Rql2Program rql2Program = (Rql2Program) tree.root();
//
//              if (!maybeDecl.isDefined()) {
//                if (programContext.runtimeContext().maybeArguments().nonEmpty()) {
//                  // arguments are given while no method to execute is
//                  // provided.
//                  throw new RawTruffleInternalErrorException(
//                      new CompilerException("arguments found", null));
//                }
//                if (rql2Program.me().isEmpty()) {
//                  // no method, but also no bottom expression. This isn't
//                  // executable.
//                  throw new RawTruffleInternalErrorException(
//                      new CompilerException("no expression found", null));
//                }
//                Exp e = rql2Program.me().get();
//                return Right.apply(Rql2Program.apply(rql2Program.methods(), Some.apply(e)));
//              } else {
//                if (tree.description().expDecls().get(maybeDecl.get()).isDefined()) {
//                  scala.collection.immutable.List<TreeDeclDescription> descriptions =
//                      tree.description().expDecls().get(maybeDecl.get()).get();
//                  if (descriptions.size() > 1)
//                    throw new RawTruffleInternalErrorException(
//                        new CompilerException("multiple declarations found", null));
//
//                  TreeDeclDescription description = descriptions.head();
//
//                  Vector<TreeParamDescription> paramsScala = description.params().get();
//                  List<TreeParamDescription> params =
//                      scala.collection.JavaConverters.seqAsJavaList(paramsScala);
//
//                  Set<String> mandatory =
//                      params.stream()
//                          .filter(TreeParamDescription::required)
//                          .map(TreeParamDescription::idn)
//                          .collect(Collectors.toSet());
//                  Tuple2<String, ParamValue>[] args =
//                      programContext.runtimeContext().maybeArguments().get();
//
//                  List<FunAppArg> programArgs =
//                      Arrays.stream(args)
//                          .map(
//                              x -> {
//                                Exp e =
//                                    switch (x._2) {
//                                      case ParamNull ignored -> NullConst.apply();
//                                      case ParamByte ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2ByteType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamShort ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2ShortType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamInt ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2IntType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamLong ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2LongType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamFloat ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2FloatType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamDouble ignored -> EnvironmentPackageBuilder
//                                          .Parameter.apply(
//                                          Rql2DoubleType.apply(new HashSet<>()),
//                                          StringConst.apply(x._1));
//                                      case ParamDecimal ignored -> EnvironmentPackageBuilder
//                                          .Parameter.apply(
//                                          Rql2DecimalType.apply(new HashSet<>()),
//                                          StringConst.apply(x._1));
//                                      case ParamBool ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2BoolType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamString ignored -> EnvironmentPackageBuilder
//                                          .Parameter.apply(
//                                          Rql2StringType.apply(new HashSet<>()),
//                                          StringConst.apply(x._1));
//                                      case ParamDate ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2DateType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamTime ignored -> EnvironmentPackageBuilder.Parameter
//                                          .apply(
//                                              Rql2TimeType.apply(new HashSet<>()),
//                                              StringConst.apply(x._1));
//                                      case ParamTimestamp ignored -> EnvironmentPackageBuilder
//                                          .Parameter.apply(
//                                          Rql2TimestampType.apply(new HashSet<>()),
//                                          StringConst.apply(x._1));
//                                      case ParamInterval ignored -> EnvironmentPackageBuilder
//                                          .Parameter.apply(
//                                          Rql2IntervalType.apply(new HashSet<>()),
//                                          StringConst.apply(x._1));
//                                      default -> throw new IllegalStateException(
//                                          "Unexpected value: " + x._2);
//                                    };
//
//                                return FunAppArg.apply(
//                                    e,
//                                    mandatory.contains(x._1) ? Some.apply(x._1) : Option.empty());
//                              })
//                          .toList();
//
//                  return Right.apply(
//                      Rql2Program.apply(
//                          rql2Program.methods(),
//                          Some.apply(
//                              FunApp.apply(
//                                  IdnExp.apply(IdnUse.apply(maybeDecl.get())),
//                                  JavaConverters.asScalaBuffer(programArgs).toVector()))));
//                } else {
//                  throw new RawTruffleInternalErrorException(
//                      new CompilerException("declaration not found", null));
//                }
//              }
//            });
//  }
//
//  private Value convertAnyToValue(Object v, Type type) {
//    return null;
//    //    return switch (type) {
//    //      case Rql2TypeWithProperties t && t.props().contains(Rql2IsTryableTypeProperty.apply())
//    // ->{
//    //          TryableLibrary t
//    //      }
//    //      case Rql2ShortType ignored -> new Value.Short((short) v);
//    //      case Rql2IntType ignored -> new Value.Int((int) v);
//    //      case Rql2LongType ignored -> new Value.Long((long) v);
//    //      case Rql2FloatType ignored -> new Value.Float((float) v);
//    //      case Rql2DoubleType ignored -> new Value.Double((double) v);
//    //      case Rql2DecimalType ignored -> new Value.Decimal((java.math.BigDecimal) v);
//    //      case Rql2BoolType ignored -> new Value.Bool((boolean) v);
//    //      case Rql2StringType ignored -> new Value.String((String) v);
//    //      case Rql2DateType ignored -> new Value.Date((java.time.LocalDate) v);
//    //      case Rql2TimeType ignored -> new Value.Time((java.time.LocalTime) v);
//    //      case Rql2TimestampType ignored -> new Value.Timestamp((java.time.LocalDateTime) v);
//    //      case Rql2IntervalType ignored -> new Value.Interval((java.time.Duration) v);
//    //      default -> throw new IllegalStateException("Unexpected value: " + t);
//    //    };
//  }
//
//  @Override
//  public String prettyPrintOutput(BaseNode node) {
//    return SourcePrettyPrinter$.MODULE$.format(node);
//  }
//
//  @Override
//  public ProgramOutputWriter execute(Entrypoint entrypoint, ProgramContext programContext) {
//    return null;
//  }
//
//  @Override
//  public Entrypoint doEmit(
//      String signature,
//      SourceProgram program,
//      Object rawLanguageAsAny,
//      ProgramContext programContext) {
//    return null;
//  }
//}
