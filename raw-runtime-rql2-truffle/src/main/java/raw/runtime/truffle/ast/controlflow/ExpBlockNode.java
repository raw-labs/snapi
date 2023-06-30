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

package raw.runtime.truffle.ast.controlflow;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.BlockNode;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ExpressionNode;

public final class ExpBlockNode extends ExpressionNode implements BlockNode.ElementExecutor<StatementNode> {

    @Child
    private BlockNode<StatementNode> block;

    @Child
    private ExpressionNode expNode;

    public ExpBlockNode(StatementNode[] stmtNodes, ExpressionNode expNode) {
        this.block = BlockNode.create(stmtNodes, this);
        this.expNode = expNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        // This assertion illustrates that the array length is really a constant during compilation.
        CompilerAsserts.compilationConstant(block.getElements().length);

        this.block.executeVoid(virtualFrame, BlockNode.NO_ARGUMENT);

        return expNode.executeGeneric(virtualFrame);
    }

    @Override
    public void executeVoid(VirtualFrame frame, StatementNode node, int index, int argument) {
        node.executeVoid(frame);
    }

}
