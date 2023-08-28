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

package raw.runtime.truffle.utils;

import raw.compiler.rql2.source.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import scala.collection.immutable.Vector;

public class KryoFootPrint {

    private static Rql2TypeProperty nullable = new Rql2IsNullableTypeProperty();
    private static Rql2TypeProperty tryable = new Rql2IsTryableTypeProperty();

    public static int of(Rql2TypeWithProperties type) {
        if (type.props().contains(tryable)) {
            return 1 + of((Rql2TypeWithProperties) type.cloneAndRemoveProp(tryable));
        } else if (type.props().contains(nullable)) {
            return 1 + of((Rql2TypeWithProperties) type.cloneAndRemoveProp(nullable));
        } else if (type instanceof Rql2BoolType || type instanceof Rql2ByteType) {
            return 1;
        } else if (type instanceof Rql2ShortType) {
            return 2;
        } else if (type instanceof Rql2IntType || type instanceof Rql2FloatType) {
            return 4;
        } else if (type instanceof Rql2LongType || type instanceof Rql2DoubleType) {
            return 8;
        } else if (type instanceof Rql2DecimalType) {
            return 32;
        } else if (type instanceof Rql2StringType) {
            return 32;
        } else if (type instanceof Rql2BinaryType) {
            return 256;
        } else if (type instanceof Rql2TimeType
                || type instanceof Rql2IntervalType
                || type instanceof Rql2DateType
                || type instanceof Rql2TimestampType) {
            return 16;
        } else if (type instanceof Rql2ListType) {
            return 4 + 30 * of((Rql2TypeWithProperties) ((Rql2ListType) (type)).innerType());
        } else if (type instanceof Rql2IterableType) {
            // same as ListType
            return 4 + 30 * of((Rql2TypeWithProperties) ((Rql2IterableType) (type)).innerType());
        } else if (type instanceof Rql2UndefinedType) {
            return 0;
        } else if (type instanceof Rql2RecordType) {
            Vector<Rql2AttrType> atts = ((Rql2RecordType) (type)).atts();
            int n = atts.size();
            int size = 0;
            for (int i = 0; i < n; i++) size += of((Rql2TypeWithProperties) atts.apply(i).tipe());
            return size;
        } else {
            throw new RawTruffleRuntimeException("Unknown type: " + type);
        }
    }
}
