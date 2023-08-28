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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.function.Closure;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.iterable.OffHeapListGroupByKey;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.operators.CompareOperator;
import raw.runtime.truffle.runtime.operators.OperatorLibrary;
import raw.runtime.truffle.runtime.record.RecordObject;

import java.util.ArrayList;

@NodeInfo(shortName = "List.GroupBy")
@NodeChild("input")
@NodeChild("keyFun")
@NodeField(name = "keyType", type = Rql2TypeWithProperties.class)
@NodeField(name = "rowType", type = Rql2TypeWithProperties.class)
public abstract class ListGroupByNode extends ExpressionNode {

    protected abstract Rql2TypeWithProperties getKeyType();

    protected abstract Rql2TypeWithProperties getRowType();

    private final CompareOperator compare = new CompareOperator();
    private final OperatorLibrary operators = OperatorLibrary.getFactory().create(compare);

    private int compareKey(Object key1, Object key2) {
        return (int) operators.doOperation(compare, key1, key2);
    }

    static final int LIB_LIMIT = 2;

    @Specialization(limit = "3")
    protected Object doGroup(
            Object input,
            Closure keyFun,
            @CachedLibrary("input") ListLibrary lists,
            @CachedLibrary(limit = "LIB_LIMIT") IterableLibrary iterables,
            @CachedLibrary(limit = "LIB_LIMIT") GeneratorLibrary generators) {
        Object iterable = lists.toIterable(input);
        RuntimeContext context = RawContext.get(this).getRuntimeContext();
        OffHeapListGroupByKey map =
                new OffHeapListGroupByKey(
                        this::compareKey,
                        getKeyType(),
                        getRowType(),
                        RawLanguage.get(this),
                        context);
        Object generator = iterables.getGenerator(iterable);
        try {
            generators.init(generator);
            while (generators.hasNext(generator)) {
                Object v = generators.next(generator);
                Object key = keyFun.call(v);
                map.put(key, v);
            }
        } finally {
            generators.close(generator);
        }
        ArrayList<RecordObject> items = new ArrayList<>();
        Object mapGenerator = map.generator();
        try {
            generators.init(mapGenerator);
            while (generators.hasNext(mapGenerator)) {
                RecordObject record = (RecordObject) generators.next(mapGenerator);
                items.add(record);
            }
        } finally {
            generators.close(mapGenerator);
        }
        return new ObjectList(items.toArray());
    }
}
