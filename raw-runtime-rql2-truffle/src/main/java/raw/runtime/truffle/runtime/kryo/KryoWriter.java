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

import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.dsl.ImportStatic;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.tryable.TryableLibrary;
import scala.collection.immutable.Vector;

@ExportLibrary(KryoWriterLibrary.class)
public final class KryoWriter {

  @ImportStatic(value = TypeGuards.class)
  @ExportMessage
  static class Write {

    private static final Rql2TypeProperty isTryable = new Rql2IsTryableTypeProperty();
    private static final Rql2TypeProperty isNullable = new Rql2IsNullableTypeProperty();

    @Specialization(
        guards = {"isTryable(type)"},
        limit = "1")
    static void doTryable(
        KryoWriter receiver,
        Output output,
        Rql2TypeWithProperties type,
        Object tryable,
        @CachedLibrary("tryable") TryableLibrary tryables,
        @CachedLibrary("receiver") KryoWriterLibrary kryo) {
      boolean isSuccess = tryables.isSuccess(tryable);
      output.writeBoolean(isSuccess);
      if (isSuccess) {
        Object value = tryables.success(tryable);
        kryo.write(
            receiver, output, (Rql2TypeWithProperties) type.cloneAndRemoveProp(isTryable), value);
      } else {
        String error = tryables.failure(tryable);
        output.writeString(error);
      }
    }

    @Specialization(
        guards = {"isNullable(type)"},
        limit = "1")
    static void doNullable(
        KryoWriter receiver,
        Output output,
        Rql2TypeWithProperties type,
        Object option,
        @CachedLibrary("option") OptionLibrary options,
        @CachedLibrary("receiver") KryoWriterLibrary kryo) {
      boolean isDefined = options.isDefined(option);
      output.writeBoolean(isDefined);
      if (isDefined) {
        Object value = options.get(option);
        kryo.write(
            receiver, output, (Rql2TypeWithProperties) type.cloneAndRemoveProp(isNullable), value);
      }
    }

    @Specialization(
        guards = {"isListKind(type)"},
        limit = "1")
    static void doList(
        KryoWriter receiver,
        Output output,
        Rql2TypeWithProperties type,
        Object o,
        @CachedLibrary("receiver") KryoWriterLibrary writerLibrary,
        @CachedLibrary("o") ListLibrary lists) {
      output.writeInt((int) lists.size(o));
      Rql2TypeWithProperties elementType =
          (Rql2TypeWithProperties) ((Rql2ListType) type).innerType();
      Object[] contents = (Object[]) lists.getInnerList(o);
      for (Object content : contents) {
        writerLibrary.write(receiver, output, elementType, content);
      }
    }

    @Specialization(
        guards = {"isIterableKind(type)"},
        limit = "1")
    static void doIterable(
        KryoWriter receiver,
        Output output,
        Rql2TypeWithProperties type,
        Object o,
        @CachedLibrary("receiver") KryoWriterLibrary writerLibrary,
        @CachedLibrary("o") IterableLibrary iterators,
        @CachedLibrary(limit = "2") GeneratorLibrary generators) {
      Rql2TypeWithProperties elementType =
          (Rql2TypeWithProperties) ((Rql2IterableType) type).innerType();
      Object generator = iterators.getGenerator(o);
      generators.init(generator);
      ArrayList<Object> contents = new ArrayList<>();
      while (generators.hasNext(generator)) {
        Object content = generators.next(generator);
        contents.add(content);
      }
      generators.close(generator);
      output.writeInt(contents.size());
      for (Object content : contents) {
        writerLibrary.write(receiver, output, elementType, content);
      }
    }

    @Specialization(
        guards = {"isRecordKind(type)"},
        limit = "1")
    static void doRecord(
        KryoWriter receiver,
        Output output,
        Rql2RecordType type,
        Object o,
        @CachedLibrary("receiver") KryoWriterLibrary writerLibrary,
        @CachedLibrary("o") InteropLibrary recordLibrary,
        @CachedLibrary(limit = "2") InteropLibrary arrayLibrary) {
      try {
        Object keys = recordLibrary.getMembers(o);
        long length = arrayLibrary.getArraySize(keys);
        Vector<Rql2AttrType> atts = type.atts();
        for (int i = 0; i < length; i++) {
          String member = (String) arrayLibrary.readArrayElement(keys, i);
          Object field = recordLibrary.readMember(o, member);
          writerLibrary.write(
              receiver, output, (Rql2TypeWithProperties) atts.apply(i).tipe(), field);
        }
      } catch (UnsupportedMessageException
          | InvalidArrayIndexException
          | UnknownIdentifierException e) {
        throw new RawTruffleInternalErrorException(e);
      }
    }

    @Specialization(guards = {"isDateKind(type)"})
    static void doDate(
        KryoWriter receiver, Output output, Rql2TypeWithProperties type, DateObject o) {
      LocalDate date = o.getDate();
      output.writeInt(date.getYear());
      output.writeInt(date.getMonthValue());
      output.writeInt(date.getDayOfMonth());
    }

    @Specialization(guards = {"isTimeKind(type)"})
    static void doTime(
        KryoWriter receiver, Output output, Rql2TypeWithProperties type, TimeObject o) {
      LocalTime time = o.getTime();
      output.writeInt(time.getHour());
      output.writeInt(time.getMinute());
      output.writeInt(time.getSecond());
      output.writeInt(time.getNano() / 1000000);
    }

    @Specialization(guards = {"isTimestampKind(type)"})
    static void doTimestamp(
        KryoWriter receiver, Output output, Rql2TypeWithProperties type, TimestampObject o) {
      LocalDateTime timestamp = o.getTimestamp();
      output.writeInt(timestamp.getYear());
      output.writeInt(timestamp.getMonthValue());
      output.writeInt(timestamp.getDayOfMonth());
      output.writeInt(timestamp.getHour());
      output.writeInt(timestamp.getMinute());
      output.writeInt(timestamp.getSecond());
      output.writeInt(timestamp.getNano() / 1000000);
    }

    @Specialization(guards = {"isIntervalKind(type)"})
    static void doInterval(
        KryoWriter receiver, Output output, Rql2TypeWithProperties type, IntervalObject o) {
      output.writeInt(o.getYears());
      output.writeInt(o.getMonths());
      output.writeInt(o.getWeeks());
      output.writeInt(o.getDays());
      output.writeInt(o.getHours());
      output.writeInt(o.getMinutes());
      output.writeInt(o.getSeconds());
      output.writeInt(o.getMillis());
    }

    @Specialization(guards = {"isByteKind(type)"})
    static void doByte(KryoWriter receiver, Output output, Rql2TypeWithProperties type, byte o) {
      output.writeByte(o);
    }

    @Specialization(guards = {"isShortKind(type)"})
    static void doShort(KryoWriter receiver, Output output, Rql2TypeWithProperties type, short o) {
      output.writeShort(o);
    }

    @Specialization(guards = {"isIntKind(type)"})
    static void doInt(KryoWriter receiver, Output output, Rql2TypeWithProperties type, int o) {
      output.writeInt(o);
    }

    @Specialization(guards = {"isLongKind(type)"})
    static void doLong(KryoWriter receiver, Output output, Rql2TypeWithProperties type, long o) {
      output.writeLong(o);
    }

    @Specialization(guards = {"isFloatKind(type)"})
    static void doFloat(KryoWriter receiver, Output output, Rql2TypeWithProperties type, float o) {
      output.writeFloat(o);
    }

    @Specialization
    static void doDouble(
        KryoWriter receiver, Output output, Rql2TypeWithProperties type, double o) {
      output.writeDouble(o);
    }

    @Specialization
    static void doDecimal(
        KryoWriter receiver, Output output, Rql2TypeWithProperties type, DecimalObject o) {
      output.writeString(o.getBigDecimal().toString());
    }

    @Specialization
    static void doString(
        KryoWriter receiver, Output output, Rql2TypeWithProperties type, String o) {
      output.writeString(o);
    }

    @Specialization
    static void doBool(KryoWriter receiver, Output output, Rql2TypeWithProperties type, boolean o) {
      output.writeBoolean(o);
    }
  }
}
