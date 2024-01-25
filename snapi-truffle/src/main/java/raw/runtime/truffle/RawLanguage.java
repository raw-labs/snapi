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

package raw.runtime.truffle;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.Shape;
import java.util.*;
import java.util.stream.Collectors;
import org.graalvm.options.OptionDescriptors;
import raw.client.api.*;
import raw.compiler.base.CompilerContext;
import raw.compiler.base.InitPhase;
import raw.compiler.base.Phase;
import raw.compiler.base.source.Type;
import raw.compiler.common.PhaseDescriptor;
import raw.compiler.common.source.SourceProgram;
import raw.compiler.rql2.*;
import raw.compiler.rql2.source.InternalSourcePrettyPrinter;
import raw.compiler.rql2.source.Rql2Program;
import raw.compiler.snapi.truffle.compiler.TruffleEmit;
import raw.inferrer.api.InferrerService;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleValidationException;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.sources.api.SourceContext;
import raw.utils.AuthenticatedUser;
import raw.utils.RawSettings;
import scala.collection.JavaConverters;

@TruffleLanguage.Registration(
    id = RawLanguage.ID,
    name = "RQL",
    version = RawLanguage.VERSION,
    defaultMimeType = RawLanguage.MIME_TYPE,
    characterMimeTypes = RawLanguage.MIME_TYPE)
@ProvidedTags({
  StandardTags.CallTag.class,
  StandardTags.StatementTag.class,
  StandardTags.RootTag.class,
  StandardTags.RootBodyTag.class,
  StandardTags.ExpressionTag.class,
  StandardTags.ReadVariableTag.class,
  StandardTags.WriteVariableTag.class
})
public final class RawLanguage extends TruffleLanguage<RawContext> {

  public static final String ID = "rql";
  public static final String VERSION = "0.10";
  public static final String MIME_TYPE = "application/x-rql";

  private static final RawLanguageCache languageCache = new RawLanguageCache();

  private final Shape initialRecordShape = Shape.newBuilder().build();

  @Override
  protected final RawContext createContext(Env env) {
    return new RawContext(this, env);
  }

  @Override
  protected void finalizeContext(RawContext context) {
    context.close();
  }

  private static final LanguageReference<RawLanguage> REFERENCE =
      LanguageReference.create(RawLanguage.class);

  public static RawLanguage get(Node node) {
    return REFERENCE.get(node);
  }

  private final InteropLibrary bindings = InteropLibrary.getFactory().createDispatched(1);

  // FIXME (msb): Why is this here?
  public RecordObject createRecord() {
    return new RecordObject(initialRecordShape);
  }

  @Override
  protected OptionDescriptors getOptionDescriptors() {
    return RawOptions.OPTION_DESCRIPTORS;
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws Exception {
    RawContext context = RawContext.get(null);

    ProgramEnvironment programEnvironment = context.getProgramEnvironment();
    RuntimeContext runtimeContext =
        new RuntimeContext(context.getSourceContext(), getRawSettings(), programEnvironment);
    ProgramContext programContext =
        new Rql2ProgramContext(runtimeContext, getCompilerContext(context.getUser()));

    String source = request.getSource().getCharacters().toString();

    // Parse and validate
    // If we are in staged compiler mode, use the internal parser.
    boolean frontend = true;
    if (context
        .getEnv()
        .getOptions()
        .get(RawOptions.STAGED_COMPILER_KEY)
        .equalsIgnoreCase("true")) {
      frontend = false;
    }
    TreeWithPositions tree = new TreeWithPositions(source, false, frontend, programContext);
    if (tree.valid()) {
      Rql2Program inputProgram = (Rql2Program) tree.root();
      SourceProgram outputProgram = transpile(inputProgram, programContext);
      Entrypoint entrypoint = TruffleEmit.doEmit(outputProgram, this, programContext);
      RootNode rootNode = (RootNode) entrypoint.target();
      JavaConverters.asJavaCollection(inputProgram.methods())
          .forEach(
              m -> {
                try {
                  bindings.writeMember(
                      context.getPolyglotBindings(),
                      "@type:" + m.i().idn(),
                      InternalSourcePrettyPrinter.format(tree.analyzer().idnType(m.i())));
                } catch (UnsupportedMessageException
                    | UnknownIdentifierException
                    | UnsupportedTypeException e) {
                  throw new RuntimeException(e);
                }
              });
      if (tree.rootType().isDefined()) {
        Type outputType = tree.rootType().get();
        bindings.writeMember(
            context.getPolyglotBindings(), "@type", InternalSourcePrettyPrinter.format(outputType));
      } else {
        if (bindings.isMemberExisting(context.getPolyglotBindings(), "@type"))
          bindings.removeMember(context.getPolyglotBindings(), "@type");
      }
      return rootNode.getCallTarget();
    } else {
      throw new RawTruffleValidationException(JavaConverters.seqAsJavaList(tree.errors()));
    }
  }

  private static final List<PhaseDescriptor> phases =
      Arrays.asList(
          new PhaseDescriptor(
              "SugarExtensionDesugarer",
              (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) SugarExtensionDesugarer.class),
          new PhaseDescriptor(
              "(Sugar)SugarExtensionDesugarer",
              (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) SugarExtensionDesugarer.class),
          new PhaseDescriptor(
              "ListProjDesugarer",
              (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) ListProjDesugarer.class),
          new PhaseDescriptor(
              "Propagation",
              (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) Propagation.class),
          new PhaseDescriptor(
              "ImplicitCasts",
              (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) ImplicitCasts.class),
          new PhaseDescriptor(
              "FlatMapOptimizer",
              (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) FlatMapOptimizer.class)
          //          new PhaseDescriptor(
          //              "ClosureOptimizer",
          //              (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>)
          //                  (Class<?>) ClosureOptimizer.class)
          );

  SourceProgram transpile(SourceProgram root, ProgramContext programContext) {
    if (phases.isEmpty()) {
      // No phases in compiler
      return root;
    } else {
      Phase<SourceProgram> pipeline = buildPipeline(new InitPhase(root), programContext);
      assert pipeline.hasNext() : "Compiler pipeline didn't produce any output tree.";
      SourceProgram outputProgram = pipeline.next();
      assert (!pipeline.hasNext()) : "Compiler pipeline produced more than one output tree.";
      return outputProgram;
    }
  }

  private Phase<SourceProgram> buildPipeline(
      Phase<SourceProgram> init, ProgramContext programContext) {
    Phase<SourceProgram> cur = init;
    List<String> phaseNames =
        phases.stream().map(PhaseDescriptor::name).collect(Collectors.toList());

    long distinctCount = phaseNames.stream().distinct().count();
    assert distinctCount == phaseNames.size()
        : "Phases have repeated names! Distinct names: "
            + phaseNames.stream().distinct().collect(Collectors.toList())
            + " All names: "
            + phaseNames;

    for (PhaseDescriptor phaseDescriptor : phases) {
      //      String name = phaseDescriptor.name();
      Phase<SourceProgram> instance = phaseDescriptor.instance(cur, programContext);
      cur = instance;
    }
    return cur;
  }

  // This method returns what is available in the bindings of the context.
  // We return the function registry as a polyglot 'hasMembers' object (members
  // are the function names, that resolve to the function objects).
  @Override
  protected Object getScope(RawContext context) {
    return context.getFunctionRegistry().asPolyglot();
  }

  public SourceContext getSourceContext(AuthenticatedUser user) {
    return languageCache.getSourceContext(user);
  }

  public CompilerContext getCompilerContext(AuthenticatedUser user) {
    return languageCache.getCompilerContext(user);
  }

  public InferrerService getInferrer(AuthenticatedUser user) {
    return languageCache.getInferrer(user);
  }

  public RawSettings getRawSettings() {
    return languageCache.rawSettings;
  }

  public static void dropCaches() {
    languageCache.reset();
  }
}
