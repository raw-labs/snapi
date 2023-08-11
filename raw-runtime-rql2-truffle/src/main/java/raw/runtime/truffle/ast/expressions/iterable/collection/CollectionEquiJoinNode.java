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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.operations.EquiJoinCollection;

@NodeInfo(shortName = "Collection.EquiJoin")
public class CollectionEquiJoinNode extends ExpressionNode {
    private final ExpressionNode left;
    private final ExpressionNode right;
    private final ExpressionNode leftKeyFun;
    private final ExpressionNode rightKeyFun;
    private final Rql2TypeWithProperties leftValueType;
    private final Rql2TypeWithProperties rightValueType;
    private final Rql2TypeWithProperties keyType;
    private final ExpressionNode remapFun;

    public CollectionEquiJoinNode(ExpressionNode left, ExpressionNode right, ExpressionNode leftKeyFun, ExpressionNode rightKeyFun, Rql2TypeWithProperties keyType, Rql2TypeWithProperties leftValueType, Rql2TypeWithProperties rightValueType, ExpressionNode remapFun) {
        this.remapFun = remapFun;
        this.keyType = keyType;
        // left
        this.left = left;
        this.leftKeyFun = leftKeyFun;
        this.leftValueType = leftValueType;
        // right
        this.right = right;
        this.rightKeyFun = rightKeyFun;
        this.rightValueType = rightValueType;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object leftIterable = left.executeGeneric(frame);
        Closure leftKeyF = (Closure)leftKeyFun.executeGeneric(frame);
        Object rightIterable = right.executeGeneric(frame);
        Closure rightKeyF = (Closure)rightKeyFun.executeGeneric(frame);
        Closure remapF = (Closure)remapFun.executeGeneric(frame);
        return new EquiJoinCollection(
            leftIterable, leftKeyF, leftValueType,
            rightIterable, rightKeyF, rightValueType,
            keyType, remapF,
            RawLanguage.get(this), RawContext.get(this).getRuntimeContext());
    }

}
