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
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.runtime.primitives.DateObject;

import java.time.format.DateTimeFormatter;

@NodeInfo(shortName = "DateWriteCsv")
public class DateWriteCsvNode extends StatementNode {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        DateObject value = (DateObject) args[0];
        CsvGenerator generator = (CsvGenerator) args[1];
        doWrite(value, generator);
    }

    @CompilerDirectives.TruffleBoundary
    private void doWrite(DateObject value, CsvGenerator gen) {
        try {
            gen.writeString(formatter.format(value.getDate()));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
