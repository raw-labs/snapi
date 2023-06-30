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


import com.oracle.truffle.api.nodes.Node;

public class JsonOrTypeException extends JsonParserRawTruffleException {
    public JsonOrTypeException(String[] messages, Node location) {
        super(createMessage(messages), location);
    }

    private static String createMessage(String[] messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("failed to parse or type:\n");

        for (int i = 0; i < messages.length; i++) {
            sb.append(String.format("\t %d: %s", i, messages[i].replaceAll("\n", "\n\t")));
        }

        return sb.toString();
    }


}
