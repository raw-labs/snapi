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
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.CsvReadFromStringComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@ExportLibrary(IterableLibrary.class)
public class CsvFromStringCollection {

    private final String str;
    private final DirectCallNode rowParser;

    private final RawTruffleCsvParserSettings settings;

    public CsvFromStringCollection(
            String str, DirectCallNode rowParser, RawTruffleCsvParserSettings settings) {
        this.str = str;
        this.rowParser = rowParser;
        this.settings = settings;
    }

    @ExportMessage
    boolean isIterable() {
        return true;
    }

    @ExportMessage
    Object getGenerator() {
        return new CollectionAbstractGenerator(
                new CsvReadFromStringComputeNext(str, rowParser, settings));
    }
}
