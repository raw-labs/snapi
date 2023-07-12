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

package raw.runtime.truffle.ast.expressions.iterable.list;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.ast.expressions.function.FunctionExecuteOperations;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.*;


@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.Transform")
@NodeChild("list")
@NodeChild("function")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListTransformNode extends ExpressionNode {

    static final int LIB_LIMIT = 2;

    protected abstract Rql2Type getResultType();

    @Specialization(guards = {"isByteKind(getResultType())"}, limit = "3")
    protected ByteList doByte(VirtualFrame frame, Object list,
                              Closure function,
                              @CachedLibrary("list") ListLibrary lists,
                              @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                              @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators,
                              @Cached("create()") FunctionExecuteOperations.FuncExecuteNode funcExecute) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        byte[] values = new byte[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (byte) funcExecute.execute(frame, function, argumentValues);
            cnt++;
        }
        return new ByteList(values);
    }

    @Specialization(guards = {"isShortKind(getResultType())"}, limit = "3")
    protected ShortList doShort(Object list,
                                Closure function,
                                @CachedLibrary("list") ListLibrary lists,
                                @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                                @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        short[] values = new short[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (short) function.call(argumentValues);
            cnt++;
        }
        return new ShortList(values);
    }

    @Specialization(guards = {"isIntKind(getResultType())"}, limit = "3")
    protected IntList doInt(Object list,
                            Closure function,
                            @CachedLibrary("list") ListLibrary lists,
                            @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                            @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        int[] values = new int[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (int) function.call(argumentValues);
            cnt++;
        }
        return new IntList(values);
    }

    @Specialization(guards = {"isLongKind(getResultType())"}, limit = "3")
    protected LongList doLong(Object list,
                              Closure function,
                              @CachedLibrary("list") ListLibrary lists,
                              @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                              @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        long[] values = new long[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (long) function.call(argumentValues);
            cnt++;
        }
        return new LongList(values);
    }

    @Specialization(guards = {"isFloatKind(getResultType())"}, limit = "3")
    protected FloatList doFloat(Object list,
                                Closure function,
                                @CachedLibrary("list") ListLibrary lists,
                                @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                                @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        float[] values = new float[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (float) function.call(argumentValues);
            cnt++;
        }
        return new FloatList(values);
    }

    @Specialization(guards = {"isDoubleKind(getResultType())"}, limit = "3")
    protected DoubleList doDouble(Object list,
                                  Closure function,
                                  @CachedLibrary("list") ListLibrary lists,
                                  @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                                  @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        double[] values = new double[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (double) function.call(argumentValues);
            cnt++;
        }
        return new DoubleList(values);
    }

    @Specialization(guards = {"isBooleanKind(getResultType())"}, limit = "3")
    protected BooleanList doBoolean(Object list,
                                    Closure function,
                                    @CachedLibrary("list") ListLibrary lists,
                                    @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                                    @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        boolean[] values = new boolean[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (boolean) function.call(argumentValues);
            cnt++;
        }
        return new BooleanList(values);
    }

    @Specialization(guards = {"isStringKind(getResultType())"}, limit = "3")
    protected StringList doString(Object list,
                                  Closure function,
                                  @CachedLibrary("list") ListLibrary lists,
                                  @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                                  @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        String[] values = new String[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = (String) function.call(argumentValues);
            cnt++;
        }
        return new StringList(values);
    }

    @Specialization(limit = "3")
    protected ObjectList doObject(Object list,
                                  Closure function,
                                  @CachedLibrary("list") ListLibrary lists,
                                  @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
                                  @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(list);
        Object generator = iterables.getGenerator(iterable);
        Object[] values = new Object[(int) lists.size(list)];
        int cnt = 0;
        Object[] argumentValues = new Object[1];
        while (generators.hasNext(generator)) {
            argumentValues[0] = generators.next(generator);
            values[cnt] = function.call(argumentValues);
            cnt++;
        }
        return new ObjectList(values);
    }

}
