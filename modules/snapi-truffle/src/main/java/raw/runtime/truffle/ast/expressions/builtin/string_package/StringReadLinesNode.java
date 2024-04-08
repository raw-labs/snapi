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

package raw.runtime.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.generator.collection.StaticInitializers;
import raw.runtime.truffle.runtime.iterable.sources.ReadLinesCollection;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;
import raw.sources.api.SourceContext;

@NodeInfo(shortName = "String.ReadLines")
@NodeChild("location")
@NodeChild("encoding")
@ImportStatic(StaticInitializers.class)
public abstract class StringReadLinesNode extends ExpressionNode {

  @Specialization
  static Object doExecute(
      LocationObject locationObject,
      String encoding,
      @Bind("$node") Node thisNode,
      @Cached(value = "getSourceContext(thisNode)", neverDefault = true) SourceContext context) {
    TruffleInputStream stream = new TruffleInputStream(locationObject, context);
    TruffleCharInputStream charStream = new TruffleCharInputStream(stream, encoding);
    return new ReadLinesCollection(charStream);
  }
}
