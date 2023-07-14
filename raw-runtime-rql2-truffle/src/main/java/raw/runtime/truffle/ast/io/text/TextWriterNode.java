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

package raw.runtime.truffle.ast.io.text;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@NodeInfo(shortName = "Text.Write")
public class TextWriterNode extends StatementNode {

    @Child
    private ExpressionNode stringNode;

    @Child
    private DirectCallNode innerWriter;

    public TextWriterNode(ExpressionNode stringNode, ProgramStatementNode innerWriter) {
        this.innerWriter = DirectCallNode.create(innerWriter.getCallTarget());
        this.stringNode = stringNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        try (OutputStream os = RawContext.get(this).getOutput()) {
            String str = stringNode.executeString(frame);
            innerWriter.call(str.getBytes(StandardCharsets.UTF_8), os);
        } catch (IOException | UnexpectedResultException e) {
            throw new RawTruffleRuntimeException(e.getMessage());
        }
    }
}
