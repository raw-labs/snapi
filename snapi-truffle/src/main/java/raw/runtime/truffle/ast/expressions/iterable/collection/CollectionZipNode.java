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
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.iterable_old.operations.ZipCollection;

@NodeInfo(shortName = "Collection.Zip")
@NodeChild("parent1")
@NodeChild("parent2")
public abstract class CollectionZipNode extends ExpressionNode {
  @Specialization
  protected Object doZip(Object iterable1, Object iterable2) {
    RawLanguage language = RawLanguage.get(this);
    return new ZipCollection(iterable1, iterable2, language);
  }
}
