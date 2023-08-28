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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.generator.collection.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection.compute_next.sources.ExpressionComputeNext;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;

/*
there will be a filter iterable node expression
takes two arguments
builds the function
  ah, that's where the frame is at?

that node is where the parent is
it gets built cached library et al
so then I have a generator object


 */

@ExportLibrary(IterableLibrary.class)
public final class ExpressionCollection {

    final ExpressionNode[] exps;

    final VirtualFrame frame;

    public ExpressionCollection(ExpressionNode[] exps, VirtualFrame frame) {
        this.exps = exps;
        this.frame = frame;
    }

    @ExportMessage
    boolean isIterable() {
        return true;
    }

    @ExportMessage
    Object getGenerator() {
        return new CollectionAbstractGenerator(new ExpressionComputeNext(exps, frame));
    }

    //    @ExportMessage
    //    static class GetGenerator {
    //
    //        @Specialization
    //        static Object doGetGenerator(Object receiver,
    //                                     @CachedLibrary("receiver") IterableLibrary iterables) {
    //            Object generator = iterables.getGenerator(receiver);
    //
    //            new FilterGenerator(generator);
    ////            // TODO (msb): Where to place this!!!
    ////             GeneratorLibrary library = GeneratorLibrary.getFactory().create()
    // .createDispatched(1);
    ////
    ////             library.init(generator);
    //// IntMap y = new IntMap();
    ////
    ////             wait.. or do I create here a ...
    ////            FilterGenerator, passing the generator object I just got as the parent?
    ////
    //// yes. create a FilterGenerator class. New instance.
    ////            that receives the input from above.
    ////
    ////
    //        }
    ////        @Specialization(guards = "range.index >= range.length")
    ////        static Object doSequenceEnd(RangeGenerator range) {
    ////            throw new BreakException();
    ////        }
    ////
    ////        @Specialization(guards = {"range.index < range.length",
    ////            "range.start == cachedStart"}, limit = "1")
    ////        static Object doSequenceCached(RangeGenerator range,
    ////                                       @Cached("range.start") int cachedStart) {
    ////            return cachedStart + range.index++;
    ////        }
    ////
    ////        @Specialization(replaces = "doSequenceCached")
    ////        static Object doSequenceUncached(RangeGenerator range) {
    ////            return doSequenceCached(range, range.start);
    ////        }
    //    }

}
