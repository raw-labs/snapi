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

package raw.runtime.truffle.runtime.option;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;

@ExportLibrary(OptionLibrary.class)
public final class ObjectOption {

    private Object value;

    private boolean isDefined;

    public ObjectOption() {
        this.isDefined = false;
    }

    public ObjectOption(Object value) {
        this.isDefined = true;
        this.value = value;
    }

    @ExportMessage
    boolean isOption() {
        return true;
    }

    @ExportMessage
    public Object get() {
        return value;
    }

    @ExportMessage
    public void set(Object value) {
        this.value = value;
        this.isDefined = true;
    }

    @ExportMessage
    public boolean isDefined() {
        return isDefined;
    }

//    /* Generator interface */
//
//    @ExportMessage
//    boolean isGenerator() {
//        return true;
//    }
//
//    @ExportMessage
//    void init() {
//    }
//
//    @ExportMessage
//    void close() {
//    }
//
//    @ExportMessage
//    Object next() {
//        if (value == null) {
//            throw new BreakException();
//        }
//        return value;
//    }
//
//    @ExportMessage
//    boolean hasNext() {
//        return value != null;
//    }

    /* Option interface */

//    @ExportMessage
//    public boolean isOption() {
//        return true;
//    }
//
//    @ExportMessage
//    public Object get() {
//        return value;
//    }
//
//    @ExportMessage
//    public void set(Object value) {
//        this.value = value;
//    }
//
//    @ExportMessage
//    public boolean isDefined() {
//        return value != null;
//    }

}
