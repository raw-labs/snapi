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

package com.rawlabs.snapi.truffle.emitter.writers;

import com.rawlabs.snapi.frontend.rql2.source.Rql2IsNullableTypeProperty;
import com.rawlabs.snapi.frontend.rql2.source.Rql2IsTryableTypeProperty;

public class CompilerScalaConsts {
  public static final Rql2IsTryableTypeProperty tryable = Rql2IsTryableTypeProperty.apply();
  public static final Rql2IsNullableTypeProperty nullable = Rql2IsNullableTypeProperty.apply();
}
