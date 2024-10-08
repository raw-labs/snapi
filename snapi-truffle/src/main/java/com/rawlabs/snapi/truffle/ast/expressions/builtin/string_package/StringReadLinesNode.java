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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.generator.collection.StaticInitializers;
import com.rawlabs.snapi.truffle.runtime.iterable.sources.ReadLinesCollection;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharInputStream;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleInputStream;

@NodeInfo(shortName = "String.ReadLines")
@NodeChild("location")
@NodeChild("encoding")
@ImportStatic(StaticInitializers.class)
public abstract class StringReadLinesNode extends ExpressionNode {

  @Specialization
  static Object doExecute(LocationObject locationObject, String encoding) {
    TruffleInputStream stream = new TruffleInputStream(locationObject);
    TruffleCharInputStream charStream = new TruffleCharInputStream(stream, encoding);
    return new ReadLinesCollection(charStream);
  }
}
