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

package raw.runtime.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JsonPrintNode extends ExpressionNode {

    @Child
    private ExpressionNode valueExp;

    @Child
    private DirectCallNode childDirectCall;

    public JsonPrintNode(ExpressionNode valueExp, RootNode jsonWriterRootNode) {
        this.valueExp = valueExp;
        this.childDirectCall = DirectCallNode.create(jsonWriterRootNode.getCallTarget());
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        Object result = valueExp.executeGeneric(virtualFrame);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             JsonGenerator gen = createGenerator(stream)) {
            childDirectCall.call(result, gen);
            gen.flush();
            return stream.toString();
        } catch (IOException e) {
            throw new RawTruffleRuntimeException(e.getMessage());
        }
    }

    @CompilerDirectives.TruffleBoundary
    private JsonGenerator createGenerator(OutputStream os) {
        try {
            JsonFactory jsonFactory = new JsonFactory();
            jsonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
            return jsonFactory.createGenerator(os, JsonEncoding.UTF8);
        } catch (IOException ex) {
            throw new RawTruffleRuntimeException(ex, this);
        }
    }


}
