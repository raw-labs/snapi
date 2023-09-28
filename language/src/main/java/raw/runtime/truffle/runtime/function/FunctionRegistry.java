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

package raw.runtime.truffle.runtime.function;

import com.oracle.truffle.api.RootCallTarget;

public final class FunctionRegistry {

  private final FunctionObject functionObject = new FunctionObject();

  public Function register(String name, String[] argNames, RootCallTarget callTarget) {
    Function result = functionObject.functions.get(name);
    assert result == null : "Function " + name + " already defined";
    result = new Function(callTarget, argNames);
    functionObject.functions.put(name, result);
    return result;
  }

  public Function getFunction(String name) {
    Function f = functionObject.functions.get(name);
    assert f != null : "Function " + name + " not found";
    return f;
  }
}
