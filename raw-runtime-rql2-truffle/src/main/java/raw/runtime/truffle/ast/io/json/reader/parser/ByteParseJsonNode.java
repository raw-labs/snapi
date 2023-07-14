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
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;

import java.io.IOException;

@NodeInfo(shortName = "ByteParseJson")
public class ByteParseJsonNode extends ExpressionNode {

    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];
        return doParse(parser);
    }

    @CompilerDirectives.TruffleBoundary
    private byte doParse(JsonParser parser) {
        try {
            byte v = parser.getByteValue();
            parser.nextToken();
            return v;
        } catch (IOException e) {
            throw new JsonParserRawTruffleException(e.getMessage(), this);
        }
    }
}
