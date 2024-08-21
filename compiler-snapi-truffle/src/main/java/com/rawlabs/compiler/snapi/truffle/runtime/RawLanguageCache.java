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

package com.rawlabs.compiler.snapi.truffle.runtime;

import com.oracle.truffle.api.CompilerDirectives;
import com.rawlabs.compiler.snapi.base.CompilerContext;
import com.rawlabs.compiler.snapi.inferrer.api.InferrerService;
import com.rawlabs.compiler.snapi.inferrer.api.InferrerServiceProvider;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.core.RawUid;
import com.rawlabs.utils.core.RawUtils;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import scala.runtime.BoxedUnit;

public class RawLanguageCache {

  private final ClassLoader classLoader = RawLanguage.class.getClassLoader();

  private final Object activeContextsLock = new Object();
  private final Set<RawContext> activeContexts = new HashSet<RawContext>();

  private final ConcurrentHashMap<RawUid, Value> map = new ConcurrentHashMap<>();

  private static class Value {
    private final CompilerContext compilerContext;
    private final InferrerService inferrer;

    Value(CompilerContext compilerContext, InferrerService inferrer) {
      this.compilerContext = compilerContext;
      this.inferrer = inferrer;
    }

    public CompilerContext getCompilerContext() {
      return compilerContext;
    }

    public InferrerService getInferrer() {
      return inferrer;
    }
  }

  @CompilerDirectives.TruffleBoundary
  private Value get(RawUid user, RawSettings rawSettings) {
    return map.computeIfAbsent(
        user,
        k -> {
          InferrerService inferrer = InferrerServiceProvider.apply(rawSettings);
          CompilerContext compilerContext =
              new CompilerContext("rql2-truffle", user, inferrer, rawSettings);
          return new Value(compilerContext, inferrer);
        });
  }

  public CompilerContext getCompilerContext(RawUid user, RawSettings rawSettings) {
    return get(user, rawSettings).getCompilerContext();
  }

  public InferrerService getInferrer(RawUid user, RawSettings rawSettings) {
    return get(user, rawSettings).getInferrer();
  }

  @CompilerDirectives.TruffleBoundary
  public void incrementContext(RawContext context) {
    synchronized (activeContextsLock) {
      activeContexts.add(context);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public void releaseContext(RawContext context) {
    synchronized (activeContextsLock) {
      activeContexts.remove(context);
      if (activeContexts.isEmpty()) {
        // Close all inferrer services.
        map.values()
            .forEach(
                v -> {
                  RawUtils.withSuppressNonFatalException(
                      () -> {
                        v.getInferrer().stop();
                        return BoxedUnit.UNIT;
                      },
                      true);
                });
        map.clear();
      }
    }
  }
}
