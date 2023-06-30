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

import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.*;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.util.ArrayList;

@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "List.FromUnsafe")
@NodeChild("list")
@NodeField(name = "resultType", type = Rql2Type.class)
public abstract class ListFromUnsafe extends ExpressionNode {

    protected abstract Rql2Type getResultType();

    @Specialization(guards = {"isByteKind(getResultType())"}, limit = "3")
    protected ByteList doByte(Object iterable,
                              @CachedLibrary("iterable") IterableLibrary iterables,
                              @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Byte> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((byte) generators.next(generator));
            }
            byte[] list = new byte[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new ByteList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }

    @Specialization(guards = {"isShortKind(getResultType())"}, limit = "3")
    protected ShortList doShort(Object iterable,
                                @CachedLibrary("iterable") IterableLibrary iterables,
                                @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Short> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((short) generators.next(generator));
            }
            short[] list = new short[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new ShortList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }

    @Specialization(guards = {"isIntKind(getResultType())"}, limit = "3")
    protected IntList doInt(Object iterable,
                            @CachedLibrary("iterable") IterableLibrary iterables,
                            @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Integer> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((int) generators.next(generator));
            }
            int[] list = new int[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new IntList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }

    @Specialization(guards = {"isLongKind(getResultType())"}, limit = "3")
    protected LongList doLong(Object iterable,
                              @CachedLibrary("iterable") IterableLibrary iterables,
                              @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Long> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((long) generators.next(generator));
            }
            long[] list = new long[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new LongList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }

    }

    @Specialization(guards = {"isFloatKind(getResultType())"}, limit = "3")
    protected FloatList doFloat(Object iterable,
                                @CachedLibrary("iterable") IterableLibrary iterables,
                                @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Float> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((float) generators.next(generator));
            }
            float[] list = new float[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new FloatList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }

    }

    @Specialization(guards = {"isDoubleKind(getResultType())"}, limit = "3")
    protected DoubleList doDouble(Object iterable,
                                  @CachedLibrary("iterable") IterableLibrary iterables,
                                  @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Double> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((double) generators.next(generator));
            }
            double[] list = new double[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new DoubleList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }

    @Specialization(guards = {"isBooleanKind(getResultType())"}, limit = "3")
    protected BooleanList doBoolean(Object iterable,
                                    @CachedLibrary("iterable") IterableLibrary iterables,
                                    @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Boolean> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((boolean) generators.next(generator));
            }
            boolean[] list = new boolean[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new BooleanList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }

    @Specialization(guards = {"isStringKind(getResultType())"}, limit = "3")
    protected StringList doString(Object iterable,
                                  @CachedLibrary("iterable") IterableLibrary iterables,
                                  @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<String> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add((String) generators.next(generator));
            }
            String[] list = new String[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new StringList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }

    @Specialization(limit = "3")
    protected ObjectList doObject(Object iterable,
                                  @CachedLibrary("iterable") IterableLibrary iterables,
                                  @CachedLibrary(limit = "1") GeneratorLibrary generators) {
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            ArrayList<Object> llist = new ArrayList<>();
            while (generators.hasNext(generator)) {
                llist.add(generators.next(generator));
            }
            Object[] list = new Object[llist.size()];
            for (int i = 0; i < list.length; i++) {
                list[i] = llist.get(i);
            }
            return new ObjectList(list);
        } catch (Exception ex) {
            throw new RawTruffleRuntimeException(ex.getMessage());
        } finally {
            generators.close(generator);
        }
    }
}
