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

package com.rawlabs.snapi.truffle.emitter.builtin.collection_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.extensions.builtin.TransformCollectionEntry;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.Rql2Language;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.iterable.collection.CollectionTransformNodeGen;
import java.util.List;

public class TruffleTransformCollectionEntry extends TransformCollectionEntry
    implements TruffleEntryExtension {

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, Rql2Language rawLanguage) {
    return CollectionTransformNodeGen.create(args.get(0).exprNode(), args.get(1).exprNode());
  }
}
