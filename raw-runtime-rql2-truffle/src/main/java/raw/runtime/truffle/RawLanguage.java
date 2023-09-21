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
import com.typesafe.config.ConfigFactory;
import raw.api.AuthenticatedUser;
import raw.compiler.common.CompilerService;
import raw.compiler.base.ProgramContext;
import raw.compiler.truffle.TruffleEntrypoint;
import raw.config.RawSettings;
import raw.runtime.ProgramEnvironment;
import raw.runtime.truffle.runtime.record.RecordObject;
import scala.Option;
import scala.Some;
import scala.collection.immutable.Set;
import scala.collection.immutable.Set$;
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.Tuple2;

@TruffleLanguage.Registration(
    id = RawLanguage.ID,
    name = "RQL",
    version = RawLanguage.VERSION,
    defaultMimeType = RawLanguage.MIME_TYPE,
    characterMimeTypes = RawLanguage.MIME_TYPE,
    contextPolicy = TruffleLanguage.ContextPolicy.SHARED)
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

  public static RawContext getCurrentContext() {
    return getCurrentContext(RawLanguage.class);
  }

  private static final LanguageReference<RawLanguage> REFERENCE =
      LanguageReference.create(RawLanguage.class);

  public static RawLanguage get(Node node) {
    return REFERENCE.get(node);
  }

  public RecordObject createRecord() {
    return new RecordObject();
  }

  @Override
  protected CallTarget parse(ParsingRequest request) throws Exception {
    Map<String, String> emptyMap = Map$.MODULE$.empty();
    Map<String, String> updatedMap = emptyMap.$plus(new Tuple2<>("output-format", ""));

    String source = request.getSource().getCharacters().toString();
    RawSettings rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());
//    SourceContext sourceContext = new SourceContext(null, null, rawSettings);
//    LocalInferrerService localInferrer = new LocalInferrerService(sourceContext);
    AuthenticatedUser user = null;
    CompilerService compilerService = new CompilerService(rawSettings);
    raw.compiler.common.Compiler compiler = compilerService.getCompiler(user, new Some("rql2-truffle"));
    ProgramEnvironment programEnvironment = new ProgramEnvironment(new Some("rql2-truffle"), (Set<String>) Set$.MODULE$.empty(), updatedMap,Option.empty());
    ProgramContext programContext =  compilerService.getProgramContext(compiler, source, Option.empty(), programEnvironment);
    TruffleEntrypoint truffleEntrypoint = (TruffleEntrypoint) compiler.compile(source, this, programContext).right().get();
    RawLanguage.getCurrentContext().setRuntimeContext(programContext.runtimeContext());
//    return Truffle.getRuntime().createCallTarget(truffleEntrypoint.node());
//  return Truffle.getRuntime().createDirectCallNode(truffleEntrypoint.node().getCallTarget());
    return truffleEntrypoint.node().getCallTarget();
  }
}

