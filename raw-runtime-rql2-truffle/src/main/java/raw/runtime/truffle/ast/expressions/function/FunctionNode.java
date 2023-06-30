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

package raw.runtime.truffle.ast.expressions.function;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.function.Function;

public class FunctionNode extends ExpressionNode {

    private final RawLanguage language;
    @Node.Child
    private  ExpressionNode bodyNode;
    private final FrameDescriptor frameDescriptor;

    public FunctionNode(RawLanguage language, FrameDescriptor frameDescriptor, ExpressionNode bodyNode) {
        this.language = language;
        this.frameDescriptor = frameDescriptor;
        this.bodyNode = bodyNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        ProgramExpressionNode rootNode = new ProgramExpressionNode(language, frameDescriptor, bodyNode);
        return new Function(rootNode.getCallTarget());
    }
}
