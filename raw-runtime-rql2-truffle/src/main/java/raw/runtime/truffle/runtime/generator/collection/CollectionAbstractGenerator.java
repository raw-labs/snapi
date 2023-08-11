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

package raw.runtime.truffle.runtime.generator.collection;

import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;

// Similar to AbstractIterator implementation
// When either next or hasNext is called, the computeNext method is called
// Then the result is stored in the next field until it is consumed by next
@ExportLibrary(GeneratorLibrary.class)
public class CollectionAbstractGenerator {

    private Object next = null;

    private boolean isTerminated = false;

    final Object computeNext;

    public CollectionAbstractGenerator(Object computeNext) {
        this.computeNext = computeNext;
    }

    @ExportMessage
    boolean isGenerator() {
        return true;
    }

    @ExportMessage
    void init(@CachedLibrary("this.computeNext") ComputeNextLibrary computeNextLibrary) {
        computeNextLibrary.init(this.computeNext);
    }

    @ExportMessage
    void close(@CachedLibrary("this.computeNext") ComputeNextLibrary computeNextLibrary) {
        computeNextLibrary.close(this.computeNext);
    }

    @ExportMessage
    Object next(@CachedLibrary("this.computeNext") ComputeNextLibrary computeNextLibrary) {
        if (isTerminated) {
            throw new BreakException();
        }
        if (next == null) {
            try {
                next = computeNextLibrary.computeNext(computeNext);
            } catch (BreakException e) { // case end of data
                this.isTerminated = true;
                throw e;
            } catch (RawTruffleRuntimeException e) { // case runtime exception
                next = e;
            }
        } else if (next instanceof RawTruffleRuntimeException) { // if hasNext returned a runtime error
            this.isTerminated = true;
            throw (RawTruffleRuntimeException) next;
        }
        Object result = next;
        next = null;
        return result;
    }

    @ExportMessage
    boolean hasNext(@CachedLibrary("this.computeNext") ComputeNextLibrary computeNextLibrary) {
        if (isTerminated) {
            return false;
        } else if (next == null) {
            try {
                next = computeNextLibrary.computeNext(computeNext);
            } catch (BreakException e) {
                this.isTerminated = true;
                return false;
            } catch (RawTruffleRuntimeException e) { // store the runtime error
                next = e;
            }
        }
        return true;
    }


}
