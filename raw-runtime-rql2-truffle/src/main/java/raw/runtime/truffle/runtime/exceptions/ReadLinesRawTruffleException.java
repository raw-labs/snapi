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

package raw.runtime.truffle.runtime.exceptions;

import raw.runtime.truffle.utils.TruffleCharInputStream;

public class ReadLinesRawTruffleException extends RawTruffleRuntimeException {
    public ReadLinesRawTruffleException(String message, TruffleCharInputStream stream) {
        super(createMessage(message, stream));
    }

    private static String createMessage(String message, TruffleCharInputStream stream) {
        StringBuilder sb = new StringBuilder();
        sb.append("failed to read lines");
        if (stream.positionDescription() != null) {
            sb.append(" (");
            sb.append(stream.positionDescription());
            sb.append(")");
        }
        sb.append(": ");
        sb.append(message);
        return sb.toString();
    }
}
