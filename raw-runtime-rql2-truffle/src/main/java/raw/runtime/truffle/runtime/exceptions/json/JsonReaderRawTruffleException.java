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

package raw.runtime.truffle.runtime.exceptions.json;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.utils.TruffleCharInputStream;

public class JsonReaderRawTruffleException extends RawTruffleRuntimeException {

    public JsonReaderRawTruffleException() {
        super("failed to read JSON");
    }

    public JsonReaderRawTruffleException(String message) {
        super(message);
    }

    public JsonReaderRawTruffleException(String message, Node location) {
        super(message, location);
    }

    public JsonReaderRawTruffleException(Node location) {
        super("failed to read JSON", location);
    }

    public JsonReaderRawTruffleException(JsonParser parser, TruffleCharInputStream stream) {
        super(createMessage("failed to read JSON", parser, stream));
    }

    public JsonReaderRawTruffleException(String message, TruffleCharInputStream stream) {
        super(createMessage(message, null, stream));
    }

    public JsonReaderRawTruffleException(String message, JsonParser parser, TruffleCharInputStream stream) {
        super(createMessage(message, parser, stream));
    }

    private static String createMessage(String message, JsonParser parser, TruffleCharInputStream stream) {
        StringBuilder sb = new StringBuilder();
        sb.append("failed to read JSON");
        if (parser != null) {
            sb.append(String.format(" (line %d column %d)", parser.getCurrentLocation().getLineNr(), parser.getCurrentLocation().getColumnNr()));
        }
        if (stream != null) {
            sb.append(String.format(" (%s): ", stream.positionDescription()));
        }
        sb.append(message);
        return sb.toString();
    }
}
