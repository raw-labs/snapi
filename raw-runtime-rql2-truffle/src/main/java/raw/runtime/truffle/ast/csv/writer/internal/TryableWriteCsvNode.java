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

package raw.runtime.truffle.ast.csv.writer.internal;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.csv.CsvWriterRawTruffleException;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;

import java.io.IOException;

@NodeInfo(shortName = "TryableWriteCsv")
public class TryableWriteCsvNode extends StatementNode {

    @Child
    private DirectCallNode valueWriter;

    @Child
    private TryableLibrary tryables = TryableLibrary.getFactory().createDispatched(3);

    public TryableWriteCsvNode(ProgramStatementNode valueWriter) {
        this.valueWriter = DirectCallNode.create(valueWriter.getCallTarget());
    }


    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        Object tryable = args[0];
        CsvGenerator generator = (CsvGenerator) args[1];
        if (tryables.isSuccess(tryable)) {
            valueWriter.call(tryables.success(tryable), generator);
        } else {
            doWriteError(tryables.failure(tryable), generator);
        }
    }

    @CompilerDirectives.TruffleBoundary
    private void doWriteError(String message, CsvGenerator gen) {
        try {
            gen.writeString(message);
        } catch (IOException e) {
            throw new CsvWriterRawTruffleException(e.getMessage(), e, this);
        }
    }
}
