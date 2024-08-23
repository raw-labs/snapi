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

package com.rawlabs.snapi.truffle.runtime.runtime.function;

import com.oracle.truffle.api.interop.TruffleObject;

public final class FunctionRegistry {

  private final FunctionRegistryObject registry;

  public FunctionRegistry() {
    registry = new FunctionRegistryObject();
  }

  public Object get(String name) {
    return registry.get(name);
  }

  public void register(String name, Object closure) {
    registry.put(name, closure);
  }

  public TruffleObject asPolyglot() {
    return registry;
  }
}
