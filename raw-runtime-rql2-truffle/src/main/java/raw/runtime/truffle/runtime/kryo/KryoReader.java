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
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.option.EmptyOption;
import raw.runtime.truffle.runtime.option.ObjectOption;
import raw.runtime.truffle.runtime.primitives.DateObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.TimeObject;
import raw.runtime.truffle.runtime.primitives.TimestampObject;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@ExportLibrary(KryoReaderLibrary.class)
public final class KryoReader {

    private final RawLanguage language;

    public KryoReader(RawLanguage language) {
        this.language = language;
    }

    @ImportStatic(value = TypeGuards.class)
    @ExportMessage
    static class Read {

        @Specialization(guards = {"isTryable(t)"})
        static Object doTryable(KryoReader receiver, Input input, Rql2TypeWithProperties t, @CachedLibrary("receiver") KryoReaderLibrary kryo) {
            boolean isSuccess = input.readBoolean();
            if (isSuccess) {
                Rql2TypeWithProperties successType = (Rql2TypeWithProperties) t.cloneAndRemoveProp(new Rql2IsTryableTypeProperty());
                Object value = kryo.read(receiver, input, successType);
                return ObjectTryable.BuildSuccess(value);
            } else {
                String error = input.readString();
                return ObjectTryable.BuildFailure(error);
            }
        }

        @Specialization(guards = {"isNullable(t)"})
        static Object doNullable(KryoReader receiver, Input input, Rql2TypeWithProperties t, @CachedLibrary("receiver") KryoReaderLibrary kryo) {
            boolean isDefined = input.readBoolean();
            if (isDefined) {
                Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) t.cloneAndRemoveProp(new Rql2IsNullableTypeProperty());
                Object innerValue = kryo.read(receiver, input, innerType);
                return new ObjectOption(innerValue);
            } else {
                return new EmptyOption();
            }
        }

        @Specialization(guards = {"isListKind(t)"})
        static ObjectList doList(KryoReader receiver, Input input, Rql2TypeWithProperties t,
                                 @CachedLibrary("receiver") KryoReaderLibrary kryo) {
            Rql2ListType listType = (Rql2ListType) t;
            Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) listType.innerType();
            int size = input.readInt();
            Object[] values = new Object[size];
            for (int i = 0; i < size; i++) {
                values[i] = kryo.read(receiver, input, innerType);
            }
            return new ObjectList(values);
        }

        @Specialization(guards = {"isIterableKind(t)"})
        static Object doIterable(KryoReader receiver, Input input, Rql2TypeWithProperties t,
                                     @CachedLibrary("receiver") KryoReaderLibrary kryo) {
            Rql2IterableType iterableType = (Rql2IterableType) t;
            Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) iterableType.innerType();
            int size = input.readInt();
            Object[] values = new Object[size];
            for (int i = 0; i < size; i++) {
                values[i] = kryo.read(receiver, input, innerType);
            }
            return new ObjectList(values).toIterable();
        }


        @Specialization(guards = {"isRecordKind(t)"})
        static RecordObject doRecord(KryoReader receiver, Input input, Rql2TypeWithProperties t,
                                     @CachedLibrary("receiver") KryoReaderLibrary kryo,
                                     @CachedLibrary(limit = "2") InteropLibrary records) {
            Rql2RecordType recordType = (Rql2RecordType) t;
            RecordObject record = receiver.language.createRecord();
            recordType.atts().forall(att -> {
                Rql2TypeWithProperties attType = (Rql2TypeWithProperties) att.tipe();
                Object value = kryo.read(receiver, input, attType);
                try {
                    records.writeMember(record, att.idn(), value);
                } catch (UnsupportedMessageException | UnknownIdentifierException | UnsupportedTypeException e) {
                    throw new RawTruffleInternalErrorException(e.getCause());
                }
                return true;
            });
            return record;
        }

        @Specialization(guards = {"isIntervalKind(t)"})
        static IntervalObject doInterval(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            int years = input.readInt();
            int months = input.readInt();
            int weeks = input.readInt();
            int days = input.readInt();
            int hours = input.readInt();
            int minutes = input.readInt();
            int seconds = input.readInt();
            int millis = input.readInt();
            return new IntervalObject(years, months, weeks, days, hours, minutes, seconds, millis);
        }

        @Specialization(guards = {"isTimeKind(t)"})
        static TimeObject doTime(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            int hours = input.readInt();
            int minutes = input.readInt();
            int seconds = input.readInt();
            int millis = input.readInt();
            return new TimeObject(LocalTime.of(hours, minutes, seconds, (int) TimeUnit.MILLISECONDS.toNanos(millis)));
        }

        @Specialization(guards = {"isDateKind(t)"})
        static DateObject doDate(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            int year = input.readInt();
            int month = input.readInt();
            int day = input.readInt();
            return new DateObject(LocalDate.of(year, month, day));

        }

        @Specialization(guards = {"isTimestampKind(t)"})
        static TimestampObject doTimestamp(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            int year = input.readInt();
            int month = input.readInt();
            int day = input.readInt();
            int hours = input.readInt();
            int minutes = input.readInt();
            int seconds = input.readInt();
            int millis = input.readInt();
            return new TimestampObject(LocalDateTime.of(year, month, day, hours, minutes, seconds, (int) TimeUnit.MILLISECONDS.toNanos(millis)));
        }

        @Specialization(guards = {"isBooleanKind(t)"})
        static boolean doBoolean(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readBoolean();
        }

        @Specialization(guards = {"isStringKind(t)"})
        static String doString(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readString();
        }

        @Specialization(guards = {"isDecimalKind(t)"})
        static BigDecimal doDecimal(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return new BigDecimal(input.readString());
        }

        @Specialization(guards = {"isDoubleKind(t)"})
        static double doDouble(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readDouble();
        }

        @Specialization(guards = {"isFloatKind(t)"})
        static float doFloat(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readFloat();
        }

        @Specialization(guards = {"isLongKind(t)"})
        static long doLong(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readLong();
        }

        @Specialization(guards = {"isIntKind(t)"})
        static int doInt(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readInt();
        }

        @Specialization(guards = {"isShortKind(t)"})
        static short doShort(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readShort();
        }

        @Specialization(guards = {"isByteKind(t)"})
        static byte doByte(KryoReader receiver, Input input, Rql2TypeWithProperties t) {
            return input.readByte();
        }

    }

}
