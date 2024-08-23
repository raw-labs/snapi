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

package com.rawlabs.snapi.truffle.ast.expressions.iterable.collection;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.Rql2Language;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.iterable.operations.ZipCollection;

@NodeInfo(shortName = "Collection.Zip")
@NodeChild("parent1")
@NodeChild("parent2")
public abstract class CollectionZipNode extends ExpressionNode {
  @Specialization
  protected Object doZip(Object iterable1, Object iterable2) {
    Rql2Language language = Rql2Language.get(this);
    return new ZipCollection(iterable1, iterable2, language);
  }
}
