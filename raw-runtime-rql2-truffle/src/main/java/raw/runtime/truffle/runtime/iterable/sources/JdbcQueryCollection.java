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

package raw.runtime.truffle.runtime.iterable.sources;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import com.oracle.truffle.api.nodes.DirectCallNode;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.exceptions.rdbms.JdbcExceptionHandler;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.JdbcQueryComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.primitives.LocationObject;

@ExportLibrary(IterableLibrary.class)
public class JdbcQueryCollection {

    private final LocationObject dbLocation;
    private final String query;
    private final DirectCallNode rowParser;

    private final RuntimeContext context;
    private final JdbcExceptionHandler exceptionHandler;

    public JdbcQueryCollection(
            LocationObject dbLocation,
            String query,
            RuntimeContext context,
            DirectCallNode rowParser,
            JdbcExceptionHandler exceptionHandler) {
        this.dbLocation = dbLocation;
        this.query = query;
        this.rowParser = rowParser;
        this.context = context;
        this.exceptionHandler = exceptionHandler;
    }

    @ExportMessage
    boolean isIterable() {
        return true;
    }

    @ExportMessage
    Object getGenerator() {
        return new CollectionAbstractGenerator(
                new JdbcQueryComputeNext(dbLocation, query, context, rowParser, exceptionHandler));
    }
}
