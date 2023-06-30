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

package raw.runtime.truffle.ast.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.record.RecordObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RecordWriteJsonNode extends StatementNode {

    @Children
    private DirectCallNode[] childDirectCalls;

    @Child
    private InteropLibrary interops = InteropLibrary.getFactory().createDispatched(2);

    private final HashMap<String, Integer> fieldNamesMap;

    public RecordWriteJsonNode(ProgramStatementNode[] childProgramStatementNode, HashMap<String, Integer> fieldNamesMap) {
        this.childDirectCalls = new DirectCallNode[childProgramStatementNode.length];
        for (int i = 0; i < childProgramStatementNode.length; i++) {
            this.childDirectCalls[i] = DirectCallNode.create(childProgramStatementNode[i].getCallTarget());
        }
        this.fieldNamesMap = fieldNamesMap;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        try {
            Object[] args = frame.getArguments();
            RecordObject record = (RecordObject) args[0];
            JsonGenerator gen = (JsonGenerator) args[1];
            Object keys = interops.getMembers(record);
            long length = interops.getArraySize(keys);
            String member;
            Object item;
            writeStartObject(gen);
            for (int i = 0; i < length; i++) {
                member = (String) interops.readArrayElement(keys, i);
                item = interops.readMember(record, member);
                writeFieldName(member, gen);
                childDirectCalls[fieldNamesMap.get(member)].call(item, gen);
            }
            writeEndObject(gen);

        } catch (UnsupportedMessageException | RuntimeException | InvalidArrayIndexException |
                 UnknownIdentifierException e) {
            throw new JsonWriterRawTruffleException(e.getMessage(), this);
        }
    }

    @CompilerDirectives.TruffleBoundary
    private void writeStartObject(JsonGenerator gen) {
        try {
            gen.writeStartObject();
        } catch (IOException e) {
            throw new RawTruffleRuntimeException(e.getMessage());
        }
    }

    @CompilerDirectives.TruffleBoundary
    private void writeEndObject(JsonGenerator gen) {
        try {
            gen.writeEndObject();
        } catch (IOException e) {
            throw new RawTruffleRuntimeException(e.getMessage());
        }
    }

    @CompilerDirectives.TruffleBoundary
    private void writeFieldName(String member, JsonGenerator gen) {
        try {
            gen.writeFieldName(member);
        } catch (IOException e) {
            throw new RawTruffleRuntimeException(e.getMessage());
        }
    }
}
