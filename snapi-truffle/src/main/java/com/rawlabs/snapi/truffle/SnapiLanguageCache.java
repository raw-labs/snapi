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

package com.rawlabs.snapi.truffle;

import com.oracle.truffle.api.CompilerDirectives;
import com.rawlabs.snapi.frontend.base.CompilerContext;
import com.rawlabs.snapi.frontend.inferrer.api.InferrerService;
import com.rawlabs.snapi.frontend.inferrer.api.InferrerServiceProvider;
import com.rawlabs.utils.core.RawSettings;
import com.rawlabs.utils.core.RawUid;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SnapiLanguageCache {

  private final Object activeContextsLock = new Object();
  private final Set<SnapiContext> activeContexts = new HashSet<SnapiContext>();

  private final ConcurrentHashMap<RawUid, InferrerService> inferrerServiceCache = new ConcurrentHashMap<>();

  private final ConcurrentHashMap<RawUid, CompilerContext> compilerContextsCache = new ConcurrentHashMap<>();

  @CompilerDirectives.TruffleBoundary
  public InferrerService getInferrer(RawUid user, RawSettings rawSettings) {
    return inferrerServiceCache.computeIfAbsent(user, k -> InferrerServiceProvider.apply(rawSettings));
  }

  @CompilerDirectives.TruffleBoundary
  public CompilerContext getCompilerContext(RawUid user, RawSettings rawSettings) {
    return compilerContextsCache.computeIfAbsent(user, k -> new CompilerContext(user, getInferrer(user, rawSettings), rawSettings));
  }

  @CompilerDirectives.TruffleBoundary
  public void incrementContext(SnapiContext context) {
    synchronized (activeContextsLock) {
      activeContexts.add(context);
    }
  }

  @CompilerDirectives.TruffleBoundary
  public void releaseContext(SnapiContext context) {
    synchronized (activeContextsLock) {
      activeContexts.remove(context);
      if (activeContexts.isEmpty()) {
        // Close all compiler contexts.
        compilerContextsCache.values().forEach(CompilerContext::stop);
        compilerContextsCache.clear();
      }
    }
  }
}
