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
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import org.graalvm.options.OptionDescriptors;
import raw.client.api.*;
import raw.compiler.InitPhase;
import raw.compiler.base.Phase;
import raw.compiler.common.PhaseDescriptor;
import raw.compiler.common.source.SourceProgram;
import raw.compiler.rql2.*;
import raw.compiler.scala2.Scala2CompilerContext;
import raw.compiler.snapi.truffle.compiler.TruffleEmit;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleValidationException;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.utils.RawSettings;
import scala.Some;
import scala.collection.JavaConverters;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    protected final RawContext createContext(Env env) {
        return new RawContext(this, env);
    }

    private static final LanguageReference<RawLanguage> REFERENCE =
            LanguageReference.create(RawLanguage.class);

    public static RawLanguage get(Node node) {
        return REFERENCE.get(node);
    }

    // FIXME (msb): Why is this here?
    public RecordObject createRecord() {
        return new RecordObject();
    }

    @Override
    protected OptionDescriptors getOptionDescriptors() {
        return RawOptions.OPTION_DESCRIPTORS;
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        ClassLoader classLoader = RawLanguage.class.getClassLoader();

        RawContext context = RawContext.get(null);
        RawSettings rawSettings = context.getRawSettings();
        ProgramEnvironment programEnvironment = context.getProgramEnvironment();

        String source = request.getSource().getCharacters().toString();

        // Parse and validate
        RuntimeContext runtimeContext = new RuntimeContext(context.getSourceContext(), rawSettings, programEnvironment);
        Scala2CompilerContext compilerContext = new Scala2CompilerContext("rql2-truffle", context.getUser(), context.getSourceContext(), context.getInferrer(), new Some<>(classLoader), null, rawSettings);
        ProgramContext programContext = new ProgramContext(runtimeContext, compilerContext);
        try {
            // If we are in staged compiler mode, use the internal parser.
            boolean frontend = true;
            boolean ensureTree = false;
            if (context.getEnv().getOptions().get(RawOptions.STAGED_COMPILER_KEY).equalsIgnoreCase("true")) {
                frontend = false;
                ensureTree = true;
            }
            TreeWithPositions tree = new TreeWithPositions(source, ensureTree, frontend, programContext);
            if (tree.valid()) {
                SourceProgram inputProgram = tree.root();
                SourceProgram outputProgram = transpile(inputProgram, programContext);
                Entrypoint entrypoint = TruffleEmit.doEmit(outputProgram, this, programContext);
                RootNode rootNode = (RootNode) entrypoint.target();
                return rootNode.getCallTarget();
            } else {
                throw new RawTruffleValidationException(JavaConverters.seqAsJavaList(tree.errors()));
            }
        } catch (RuntimeException e) {
            if (e.getCause() instanceof CompilerParserException) {
                CompilerParserException ex = (CompilerParserException) e.getCause();
                throw new RawTruffleValidationException(
                        java.util.Collections.singletonList(
                                new ErrorMessage(
                                        ex.getMessage(),
                                        JavaConverters.asScalaBufferConverter(
                                                java.util.Collections.singletonList(
                                                        new ErrorRange(ex.position(), ex.position())
                                                )
                                        ).asScala().toList()
                                )
                        )
                );
            } else {
                throw e;
            }
        }
    }

    private static final List<PhaseDescriptor> phases =
        Arrays.asList(
            new PhaseDescriptor(
                "SugarExtensionDesugarer",
                (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>) (Class<?>) SugarExtensionDesugarer.class
            ),
            new PhaseDescriptor(
            "(Sugar)SugarExtensionDesugarer",
                (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>) (Class<?>) SugarExtensionDesugarer.class
            ),
            new PhaseDescriptor(
            "ListProjDesugarer",
                (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>) (Class<?>) ListProjDesugarer.class
            ),
            new PhaseDescriptor(
                    "Propagation",
                    (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>) (Class<?>) Propagation.class
            ),
            new PhaseDescriptor(
                    "ImplicitCasts",
                    (Class<raw.compiler.base.PipelinedPhase<SourceProgram>>) (Class<?>) ImplicitCasts.class
            )
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

    private Phase<SourceProgram> buildPipeline(Phase<SourceProgram> init, ProgramContext programContext) {
        Phase<SourceProgram> cur = init;
        List<String> phaseNames = phases.stream().map(PhaseDescriptor::name).collect(Collectors.toList());

        long distinctCount = phaseNames.stream().distinct().count();
        assert distinctCount == phaseNames.size() : "Phases have repeated names! Distinct names: " +
                phaseNames.stream().distinct().collect(Collectors.toList()) + " All names: " + phaseNames;

        for (PhaseDescriptor phaseDescriptor : phases) {
//      String name = phaseDescriptor.name();
            Phase<SourceProgram> instance = phaseDescriptor.instance(cur, programContext);
            cur = instance;
        }
        return cur;
    }

    @Override
    protected Object getScope(RawContext context) {
        return super.getScope(context);
    }

}
