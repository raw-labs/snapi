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
import com.oracle.truffle.api.CompilerDirectives;
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
import com.rawlabs.compiler.api.*;
import com.rawlabs.compiler.api.Entrypoint;
import com.rawlabs.compiler.snapi.inferrer.api.InferrerService;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.core.RawUid;
import com.typesafe.config.ConfigFactory;
import java.util.*;
import java.util.stream.Collectors;
import org.graalvm.options.OptionDescriptors;
import com.rawlabs.compiler.snapi.base.CompilerContext;
import com.rawlabs.compiler.snapi.base.InitPhase;
import com.rawlabs.compiler.snapi.base.Phase;
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.common.PhaseDescriptor;
import com.rawlabs.compiler.snapi.common.source.SourceProgram;
import com.rawlabs.compiler.snapi.rql2.*;
import com.rawlabs.compiler.snapi.rql2.source.InternalSourcePrettyPrinter;
import com.rawlabs.compiler.snapi.rql2.source.Rql2Program;
import raw.compiler.snapi.truffle.compiler.TruffleEmit;
import raw.runtime.truffle.runtime.exceptions.RawTruffleValidationException;
import raw.runtime.truffle.runtime.record.DuplicateKeyRecord;
import raw.runtime.truffle.runtime.record.PureRecord;
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

  private static final RawSettings defaultRawSettings =
      new RawSettings(ConfigFactory.load(), ConfigFactory.empty());

  private final Shape pureRecordShape = Shape.newBuilder().build();
  private final Shape duplicateKeyRecordShape = Shape.newBuilder().build();

  // The bellow methods are used to create new instances of the record classes.
  // This instances must have common ancestor, so we create them with the same shape.
  // This is a common pattern in Truffle, due to the way the object model works.
  public PureRecord createPureRecord() {
    return new PureRecord(pureRecordShape);
  }

  public DuplicateKeyRecord createDuplicateKeyRecord() {
    return new DuplicateKeyRecord(duplicateKeyRecordShape);
  }

  @Override
  protected final RawContext createContext(Env env) {
    RawContext context = new RawContext(this, env);
    // The language cache keeps track of active contexts, so that it knows when to shutdown itself.
    languageCache.incrementContext(context);
    return context;
  }

  @Override
  protected void finalizeContext(RawContext context) {
    // The language cache keeps track of active contexts, so that it knows when to shutdown itself.
    languageCache.releaseContext(context);
  }

  private static final LanguageReference<RawLanguage> REFERENCE =
      LanguageReference.create(RawLanguage.class);

  public static RawLanguage get(Node node) {
    return REFERENCE.get(node);
  }

  private final InteropLibrary bindings = InteropLibrary.getFactory().createDispatched(1);

  @Override
  protected OptionDescriptors getOptionDescriptors() {
    return RawOptions.OPTION_DESCRIPTORS;
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws Exception {
    RawContext context = RawContext.get(null);

    ProgramContext programContext =
        new Rql2ProgramContext(
            context.getProgramEnvironment(),
            getCompilerContext(context.getUid(), context.getSettings()));

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
              (Class<com.rawlabs.compiler.snapi.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) SugarExtensionDesugarer.class),
          new PhaseDescriptor(
              "(Sugar)SugarExtensionDesugarer",
              (Class<com.rawlabs.compiler.snapi.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) SugarExtensionDesugarer.class),
          new PhaseDescriptor(
              "ListProjDesugarer",
              (Class<com.rawlabs.compiler.snapi.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) ListProjDesugarer.class),
          new PhaseDescriptor(
              "Propagation",
              (Class<com.rawlabs.compiler.snapi.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) Propagation.class),
          new PhaseDescriptor(
              "ImplicitCasts",
              (Class<com.rawlabs.compiler.snapi.base.PipelinedPhase<SourceProgram>>)
                  (Class<?>) ImplicitCasts.class));

  @CompilerDirectives.TruffleBoundary
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

  @CompilerDirectives.TruffleBoundary
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

  public CompilerContext getCompilerContext(RawUid user, RawSettings rawSettings) {
    return languageCache.getCompilerContext(user, rawSettings);
  }

  public InferrerService getInferrer(RawUid user, RawSettings rawSettings) {
    return languageCache.getInferrer(user, rawSettings);
  }

  public RawSettings getDefaultRawSettings() {
    return defaultRawSettings;
  }
}
