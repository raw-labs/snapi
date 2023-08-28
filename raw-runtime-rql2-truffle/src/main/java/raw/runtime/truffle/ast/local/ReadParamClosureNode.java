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

package raw.runtime.truffle.ast.local;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import raw.runtime.truffle.ExpressionNode;

public class ReadParamClosureNode extends ExpressionNode {

    private final int depth;
    private final int index;

    public ReadParamClosureNode(int depth, int index) {
        this.depth = depth;
        this.index = index;
    }

    @ExplodeLoop
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Frame currentFrame = frame;
        for (int i = 0; i < depth; i++) {
            currentFrame = (Frame) currentFrame.getArguments()[0];
        }
        return currentFrame.getArguments()[index + 1];
    }
}
