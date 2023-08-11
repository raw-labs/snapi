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

package raw.runtime.truffle.runtime.list;

import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

@GenerateLibrary
public abstract class ListLibrary extends Library {
    static final LibraryFactory<ListLibrary> FACTORY = LibraryFactory.resolve(ListLibrary.class);

    public static LibraryFactory<ListLibrary> getFactory() {
        return FACTORY;
    }

    public static ListLibrary getUncached() {
        return FACTORY.getUncached();
    }

    public boolean isList(Object receiver) {
        return false;
    }

    public abstract Object getInnerList(Object receiver);

    public abstract Object get(Object receiver, long index);

    public abstract boolean isElementReadable(Object receiver, int index);

    public abstract long size(Object receiver);

    public abstract Object toIterable(Object receiver);

    public abstract Object sort(Object receiver);

}
