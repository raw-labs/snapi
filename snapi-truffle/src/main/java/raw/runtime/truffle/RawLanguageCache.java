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

import java.util.concurrent.ConcurrentHashMap;
import raw.compiler.base.CompilerContext;
import raw.creds.api.CredentialsService;
import raw.creds.api.CredentialsServiceProvider;
import raw.inferrer.api.InferrerService;
import raw.inferrer.api.InferrerServiceProvider;
import raw.sources.api.SourceContext;
import raw.utils.AuthenticatedUser;
import raw.utils.RawSettings;
import scala.Some;

public class RawLanguageCache {

  private final ClassLoader classLoader = RawLanguage.class.getClassLoader();

  private final ConcurrentHashMap<RawSettings, CredentialsService> credentialsCache =
      new ConcurrentHashMap<>();

  // This will be initialized on first use and not here statically.
  // That's because for the test suite we want the ability to define it dynamically depending on RAW
  // settings.
  public CredentialsService credentialsService;

  private final ConcurrentHashMap<AuthenticatedUser, Value> map = new ConcurrentHashMap<>();

  private static class Value {
    private final CompilerContext compilerContext;
    private final SourceContext sourceContext;
    private final InferrerService inferrer;

    Value(CompilerContext compilerContext, SourceContext sourceContext, InferrerService inferrer) {
      this.compilerContext = compilerContext;
      this.sourceContext = sourceContext;
      this.inferrer = inferrer;
    }

    public CompilerContext getCompilerContext() {
      return compilerContext;
    }

    public SourceContext getSourceContext() {
      return sourceContext;
    }

    public InferrerService getInferrer() {
      return inferrer;
    }
  }

  private Value get(AuthenticatedUser user, RawSettings rawSettings) {
    CredentialsService credentialsService =
        credentialsCache.computeIfAbsent(
            rawSettings, k -> CredentialsServiceProvider.apply(classLoader, rawSettings));
    return map.computeIfAbsent(
        user,
        k -> {
          SourceContext sourceContext =
              new SourceContext(user, credentialsService, rawSettings, new Some<>(classLoader));
          InferrerService inferrer = InferrerServiceProvider.apply(classLoader, sourceContext);
          CompilerContext compilerContext =
              new CompilerContext(
                  "rql2-truffle",
                  user,
                  inferrer,
                  sourceContext,
                  new Some<>(classLoader),
                  rawSettings);
          return new Value(compilerContext, sourceContext, inferrer);
        });
  }

  public SourceContext getSourceContext(AuthenticatedUser user, RawSettings rawSettings) {
    return get(user, rawSettings).getSourceContext();
  }

  public CompilerContext getCompilerContext(AuthenticatedUser user, RawSettings rawSettings) {
    return get(user, rawSettings).getCompilerContext();
  }

  public InferrerService getInferrer(AuthenticatedUser user, RawSettings rawSettings) {
    return get(user, rawSettings).getInferrer();
  }

  public void close() {
    // Close all inferrer services and credential services.
    map.values()
        .forEach(
            v -> {
              v.getInferrer().stop();
              v.getSourceContext().credentialsService().stop();
            });
    map.clear();
    if (credentialsService != null) {
      credentialsService.stop();
      credentialsService = null;
    }
  }
}
