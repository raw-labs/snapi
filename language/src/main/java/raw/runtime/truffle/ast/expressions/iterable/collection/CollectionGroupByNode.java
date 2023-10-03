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
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.iterable.operations.GroupByCollection;

@NodeInfo(shortName = "Collection.GroupBy")
@NodeChild("iterable")
@NodeChild("keyFun")
@NodeField(name = "keyType", type = Rql2TypeWithProperties.class)
@NodeField(name = "rowType", type = Rql2TypeWithProperties.class)
public abstract class CollectionGroupByNode extends ExpressionNode {

  protected abstract Rql2TypeWithProperties getKeyType();

  protected abstract Rql2TypeWithProperties getRowType();

  @Specialization
  protected Object doGroup(Object iterable, Closure keyFun) {
    return new GroupByCollection(
        iterable,
        keyFun,
        getKeyType(),
        getRowType(),
        RawLanguage.get(this),
        RawContext.get(this).getSourceContext());
  }
}
