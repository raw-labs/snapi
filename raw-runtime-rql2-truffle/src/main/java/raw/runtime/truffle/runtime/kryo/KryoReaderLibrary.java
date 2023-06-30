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

package raw.runtime.truffle.runtime.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;
import raw.compiler.rql2.source.Rql2TypeWithProperties;

@GenerateLibrary
public abstract class KryoReaderLibrary extends Library {
    public abstract Object read(Object receiver, Input input, Rql2TypeWithProperties t);
    static final LibraryFactory<KryoReaderLibrary> FACTORY = LibraryFactory.resolve(KryoReaderLibrary.class);
    public static KryoReaderLibrary getUncached() {
        return FACTORY.getUncached();
    }

}
