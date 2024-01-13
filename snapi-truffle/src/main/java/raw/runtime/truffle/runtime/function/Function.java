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
import com.oracle.truffle.api.interop.TruffleObject;
import java.util.HashMap;
import java.util.Map;

public final class Function implements TruffleObject {

  private final String name;

  private final RootCallTarget rootCallTarget;

  public final String[] argNames;

  public final Map<Integer, Map<String, Object>> namedArgsCache = new HashMap<>();

  public Function(RootCallTarget rootCallTarget, String[] argNames) {
    this.name = rootCallTarget.getRootNode().getName();
    this.rootCallTarget = rootCallTarget;
    this.argNames = argNames;
  }

  public String getName() {
    return name;
  }

  public RootCallTarget getCallTarget() {
    return rootCallTarget;
  }

  public String[] getArgNames() {
    return argNames;
  }
}
