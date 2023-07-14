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

package raw.runtime.truffle.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.exceptions.json.JsonWriterRawTruffleException;
import raw.runtime.truffle.runtime.primitives.IntervalObject;

import java.io.IOException;

@NodeInfo(shortName = "IntervalWriteJson")
public class IntervalWriteJsonNode extends StatementNode {

    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        this.doWrite((IntervalObject) args[0], (JsonGenerator) args[1]);
    }

    @CompilerDirectives.TruffleBoundary
    private void doWrite(IntervalObject value, JsonGenerator gen) {
        try {
            gen.writeString(value.toString());
        } catch (IOException e) {
            throw new JsonWriterRawTruffleException(e.getMessage(), this);
        }
    }
}
