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

package raw.runtime.truffle.runtime.exceptions.csv;

import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParser;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.utils.RawTruffleCharStream;

public class CsvReaderRawTruffleException extends RawTruffleRuntimeException {

    public CsvReaderRawTruffleException(String message, RawTruffleCsvParser parser, RawTruffleCharStream stream) {
        super(createMessage(message, parser, stream));
    }

    public CsvReaderRawTruffleException(RawTruffleCharStream stream, Throwable cause) {
        super(createMessage(cause.getMessage(), null, stream), cause, null);
    }

    public CsvReaderRawTruffleException(RawTruffleCharStream stream, Throwable cause, Node location) {
        super(createMessage(cause.getMessage(), null, stream), cause, location);
    }

    private static String createMessage(String message, RawTruffleCsvParser parser, RawTruffleCharStream stream) {
        StringBuilder sb = new StringBuilder();
        sb.append("failed to read CSV");
        if (parser != null) {
            sb.append(String.format(" (line %d column %d)", parser.currentTokenLine(), parser.currentTokenColumn()));
        }
        if (stream != null) {
            String position = stream.positionDescription();
            if (position != null) {
                sb.append(String.format(" (%s)", position));
            }
            sb.append(": ");
        }
        sb.append(message);
        return sb.toString();
    }

}
