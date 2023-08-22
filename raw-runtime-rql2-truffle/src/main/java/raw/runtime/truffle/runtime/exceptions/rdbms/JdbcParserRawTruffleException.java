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

package raw.runtime.truffle.runtime.exceptions.rdbms;

import com.oracle.truffle.api.nodes.Node;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public class JdbcParserRawTruffleException extends RawTruffleRuntimeException {

    public JdbcParserRawTruffleException(String message, Throwable e, Node location) {
        super(String.format("failed to read value: %s", message), e, location);
    }
}
