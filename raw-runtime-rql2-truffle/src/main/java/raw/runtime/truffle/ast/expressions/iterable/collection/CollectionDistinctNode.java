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

package raw.runtime.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.operations.DistinctCollection;

@NodeInfo(shortName = "Collection.Distinct")
@NodeChild("input")
@NodeField(name = "valueType", type = Rql2TypeWithProperties.class)
public abstract class CollectionDistinctNode extends ExpressionNode {

  protected abstract Rql2TypeWithProperties getValueType();

  @Specialization
  protected Object doDistinct(Object iterable) {
    return new DistinctCollection(
        iterable, getValueType(), RawLanguage.get(this), RawContext.get(this).getRuntimeContext());
  }
}
