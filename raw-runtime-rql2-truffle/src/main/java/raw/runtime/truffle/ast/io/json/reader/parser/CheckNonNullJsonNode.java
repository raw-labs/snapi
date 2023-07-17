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

package raw.runtime.truffle.ast.io.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.ProgramExpressionNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;

@NodeInfo(shortName = "CheckNonNullJson")
public class CheckNonNullJsonNode extends ExpressionNode {

    @Child
    private DirectCallNode childDirectCall;

    public CheckNonNullJsonNode(ProgramExpressionNode childProgramStatementNode) {
        this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
    }

    public Object executeGeneric(VirtualFrame frame) {
        JsonParser parser = (JsonParser) frame.getArguments()[0];
        doCheck(parser);
        return childDirectCall.call(parser);
    }

    @CompilerDirectives.TruffleBoundary
    private void doCheck(JsonParser parser) {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            throw new JsonParserRawTruffleException("null value found", this);
        }
    }
}
