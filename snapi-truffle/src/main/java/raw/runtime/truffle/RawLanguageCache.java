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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import raw.compiler.base.CompilerContext;
import raw.creds.api.CredentialsService;
import raw.creds.api.CredentialsServiceProvider;
import raw.inferrer.api.InferrerService;
import raw.inferrer.api.InferrerServiceProvider;
import raw.sources.api.SourceContext;
import raw.utils.AuthenticatedUser;
import raw.utils.RawSettings;
import raw.utils.RawUtils;
import scala.Some;
import scala.runtime.BoxedUnit;

public class RawLanguageCache {

  private final ClassLoader classLoader = RawLanguage.class.getClassLoader();

  private final Object activeContextsLock = new Object();
  private final Set<RawContext> activeContexts = new HashSet<RawContext>();

  private final ConcurrentHashMap<RawSettings, CredentialsService> credentialsCache =
      new ConcurrentHashMap<>();

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
    // Create services on-demand.
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

  public void incrementContext(RawContext context) {
    synchronized (activeContextsLock) {
      activeContexts.add(context);
    }
  }

  public void releaseContext(RawContext context) {
    synchronized (activeContextsLock) {
      activeContexts.remove(context);
      if (activeContexts.isEmpty()) {
        // Close all inferrer services and credential services.
        map.values()
            .forEach(
                v -> {
                  RawUtils.withSuppressNonFatalException(
                      () -> {
                        v.getInferrer().stop();
                        return BoxedUnit.UNIT;
                      },
                      true);
                  RawUtils.withSuppressNonFatalException(
                      () -> {
                        v.getSourceContext().credentialsService().stop();
                        return BoxedUnit.UNIT;
                      },
                      true);
                });
        map.clear();
        credentialsCache
            .values()
            .forEach(
                v -> {
                  RawUtils.withSuppressNonFatalException(
                      () -> {
                        v.stop();
                        return BoxedUnit.UNIT;
                      },
                      true);
                });
        credentialsCache.clear();
      }
    }
  }
}
