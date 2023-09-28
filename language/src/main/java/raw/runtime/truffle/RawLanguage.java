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
import com.typesafe.config.ConfigFactory;
import raw.compiler.ErrorMessage;
import raw.compiler.api.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.utils.AuthenticatedUser;
import raw.utils.RawSettings;
import raw.runtime.Entrypoint;
import raw.runtime.ProgramEnvironment;
import raw.runtime.truffle.runtime.record.RecordObject;
import scala.Function1;
import scala.Option;
import scala.collection.immutable.*;
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

//  public static RawContext getCurrentContext() {
//    return getCurrentContext(RawLanguage.class);
//  }

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
  protected CallTarget parse(ParsingRequest request) throws Exception {
    String source = request.getSource().getCharacters().toString();

    RawSettings rawSettings = new RawSettings(ConfigFactory.load(), ConfigFactory.empty());
    AuthenticatedUser authenticatedUser = null;
    CompilerService compilerService = CompilerServiceProvider.apply(rawSettings);

      
    request.getArgumentNames()



    getEnvironment()

    this.

    Map<String, String> emptyMap = Map$.MODULE$.empty();
    fix this
          this vs RawContext
            it is damn confusing
            it is confusing that this ALREADY needs the arguments
            but it does, right?

    Map<String, String> updatedMap = emptyMap.$plus(new Tuple2<>("output-format", ""));
    ProgramEnvironment programEnvironment = new ProgramEnvironment(authenticatedUser,  (Set<String>) Set$.MODULE$.empty(), updatedMap, Option.empty());

    CompilationResponse compilationResponse = compilerService.compile(source, Option.empty(), programEnvironment,  this);
    if (compilationResponse instanceof CompilationFailure) {
      // TODO (msb): Return all errors, not just head.
      String result = ((CompilationFailure) compilationResponse).errors().head().toString();
      throw new RawTruffleRuntimeException(result);
    }
    Entrypoint entrypoint = ((CompilationSuccess) compilationResponse).entrypoint();
    RootNode rootNode = (RootNode) entrypoint.target();
    return rootNode.getCallTarget();
  }
}
