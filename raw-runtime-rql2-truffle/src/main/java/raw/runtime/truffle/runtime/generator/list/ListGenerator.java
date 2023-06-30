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

package raw.runtime.truffle.runtime.generator.list;

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.ListLibrary;

@ExportLibrary(GeneratorLibrary.class)
public class ListGenerator {

    final Object list;
    private int position = 0;

    public ListGenerator(Object list) {
        this.list = list;
    }

    @ExportMessage
    boolean isGenerator() {
        return true;
    }

    @ExportMessage
    void init() {
    }

    @ExportMessage
    void close() {
    }

    @ExportMessage
    public boolean hasNext(@CachedLibrary("this.list") ListLibrary lists) {
        return this.position < lists.size(list);
    }

    @ExportMessage
    public Object next(@CachedLibrary("this.list") ListLibrary lists) {
        Object item = lists.get(list, position);
        this.position++;
        return item;
    }

}
