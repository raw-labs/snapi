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

package raw.runtime.truffle.ast.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.expressions.builtin.temporals.DateTimeFormatCache;
import raw.runtime.truffle.runtime.exceptions.json.JsonParserRawTruffleException;
import raw.runtime.truffle.runtime.primitives.TimestampObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NodeInfo(shortName = "TimestampParseJson")
@NodeChild(value = "format", type = ExpressionNode.class)
public class TimestampParseJsonNode extends ExpressionNode {

    @Child
    private ExpressionNode format;

    public TimestampParseJsonNode(ExpressionNode format) {
        this.format = format;
    }

    public Object executeGeneric(VirtualFrame frame) {
        try {
            Object[] args = frame.getArguments();
            JsonParser parser = (JsonParser) args[0];
            String format = this.format.executeString(frame);
            return doParse(parser, format);
        } catch (UnexpectedResultException e) {
            throw new JsonParserRawTruffleException(e.getMessage(), this);
        }
    }

    @CompilerDirectives.TruffleBoundary
    private TimestampObject doParse(JsonParser parser, String format) {
        try {
            String text = parser.getText();
            TimestampObject timestamp = new TimestampObject(LocalDateTime.parse(text, DateTimeFormatCache.get(format)));
            parser.nextToken();
            return timestamp;
        } catch (IOException e) {
            throw new JsonParserRawTruffleException(e.getMessage(), this);
        }
    }
}
