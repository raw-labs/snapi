package raw.runtime.truffle.runtime.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.GenerateUncached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.*;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import raw.compiler.rql2.source.*;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.tryable_nullable.Nullable;
import raw.runtime.truffle.tryable_nullable.Tryable;
import scala.collection.immutable.Vector;

public class KryoNodes {
  @NodeInfo(shortName = "Kryo.Read")
  @GenerateUncached
  public abstract static class KryoReadNode extends Node {

    public abstract Object execute(RawLanguage language, Input input, Rql2TypeWithProperties t);

    @Specialization(guards = {"isTryable(t)"})
    static Object doTryable(
        RawLanguage language, Input input, Rql2TypeWithProperties t, @Cached KryoReadNode kryo) {
      boolean isSuccess = input.readBoolean();
      if (isSuccess) {
        Rql2TypeWithProperties successType =
            (Rql2TypeWithProperties) t.cloneAndRemoveProp(new Rql2IsTryableTypeProperty());
        return kryo.execute(language, input, successType);
      } else {
        String error = input.readString();
        return new ErrorObject(error);
      }
    }

    @Specialization(guards = {"isNullable(t)"})
    static Object doNullable(
        RawLanguage language, Input input, Rql2TypeWithProperties t, @Cached KryoReadNode kryo) {
      boolean isDefined = input.readBoolean();
      if (isDefined) {
        Rql2TypeWithProperties innerType =
            (Rql2TypeWithProperties) t.cloneAndRemoveProp(new Rql2IsNullableTypeProperty());
        return kryo.execute(language, input, innerType);
      } else {
        return NullObject.INSTANCE;
      }
    }

    @Specialization(guards = {"isListKind(t)"})
    static ObjectList doList(
        RawLanguage language, Input input, Rql2TypeWithProperties t, @Cached KryoReadNode kryo) {
      Rql2ListType listType = (Rql2ListType) t;
      Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) listType.innerType();
      int size = input.readInt();
      Object[] values = new Object[size];
      for (int i = 0; i < size; i++) {
        values[i] = kryo.execute(language, input, innerType);
      }
      return new ObjectList(values);
    }

    @Specialization(guards = {"isIterableKind(t)"})
    static Object doIterable(
        RawLanguage language, Input input, Rql2TypeWithProperties t, @Cached KryoReadNode kryo) {
      Rql2IterableType iterableType = (Rql2IterableType) t;
      Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) iterableType.innerType();
      int size = input.readInt();
      Object[] values = new Object[size];
      for (int i = 0; i < size; i++) {
        values[i] = kryo.execute(language, input, innerType);
      }
      return new ObjectList(values).toIterable();
    }

    @Specialization(guards = {"isRecordKind(t)"})
    static RecordObject doRecord(
        RawLanguage language,
        Input input,
        Rql2TypeWithProperties t,
        @Cached KryoReadNode kryo,
        @CachedLibrary(limit = "2") InteropLibrary records) {
      Rql2RecordType recordType = (Rql2RecordType) t;
      RecordObject record = language.createRecord();
      recordType
          .atts()
          .forall(
              att -> {
                Rql2TypeWithProperties attType = (Rql2TypeWithProperties) att.tipe();
                Object value = kryo.execute(language, input, attType);
                try {
                  records.writeMember(record, att.idn(), value);
                } catch (UnsupportedMessageException
                    | UnknownIdentifierException
                    | UnsupportedTypeException e) {
                  throw new RawTruffleInternalErrorException(e);
                }
                return true;
              });
      return record;
    }

    @Specialization(guards = {"isIntervalKind(t)"})
    static IntervalObject doInterval(RawLanguage language, Input input, Rql2TypeWithProperties t) {
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
    static TimeObject doTime(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      int hours = input.readInt();
      int minutes = input.readInt();
      int seconds = input.readInt();
      int millis = input.readInt();
      return new TimeObject(
          LocalTime.of(hours, minutes, seconds, (int) TimeUnit.MILLISECONDS.toNanos(millis)));
    }

    @Specialization(guards = {"isDateKind(t)"})
    static DateObject doDate(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      int year = input.readInt();
      int month = input.readInt();
      int day = input.readInt();
      return new DateObject(LocalDate.of(year, month, day));
    }

    @Specialization(guards = {"isTimestampKind(t)"})
    static TimestampObject doTimestamp(
        RawLanguage language, Input input, Rql2TypeWithProperties t) {
      int year = input.readInt();
      int month = input.readInt();
      int day = input.readInt();
      int hours = input.readInt();
      int minutes = input.readInt();
      int seconds = input.readInt();
      int millis = input.readInt();
      return new TimestampObject(
          LocalDateTime.of(
              year,
              month,
              day,
              hours,
              minutes,
              seconds,
              (int) TimeUnit.MILLISECONDS.toNanos(millis)));
    }

    @Specialization(guards = {"isBooleanKind(t)"})
    static boolean doBoolean(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readBoolean();
    }

    @Specialization(guards = {"isStringKind(t)"})
    static String doString(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readString();
    }

    @Specialization(guards = {"isDecimalKind(t)"})
    static DecimalObject doDecimal(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return new DecimalObject(new BigDecimal(input.readString()));
    }

    @Specialization(guards = {"isDoubleKind(t)"})
    static double doDouble(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readDouble();
    }

    @Specialization(guards = {"isFloatKind(t)"})
    static float doFloat(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readFloat();
    }

    @Specialization(guards = {"isLongKind(t)"})
    static long doLong(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readLong();
    }

    @Specialization(guards = {"isIntKind(t)"})
    static int doInt(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readInt();
    }

    @Specialization(guards = {"isShortKind(t)"})
    static short doShort(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readShort();
    }

    @Specialization(guards = {"isByteKind(t)"})
    static byte doByte(RawLanguage language, Input input, Rql2TypeWithProperties t) {
      return input.readByte();
    }
  }

  @NodeInfo(shortName = "Kryo.Write")
  @GenerateUncached
  public abstract static class KryoWriteNode extends Node {

    private static final Rql2TypeProperty isTryable = new Rql2IsTryableTypeProperty();
    private static final Rql2TypeProperty isNullable = new Rql2IsNullableTypeProperty();

    public abstract Object execute(Output output, Rql2TypeWithProperties type, Object maybeTryable);

    @Specialization(
        guards = {"isTryable(type)"},
        limit = "1")
    static void doTryable(
        Output output,
        Rql2TypeWithProperties type,
        Object maybeTryable,
        @Cached KryoWriteNode kryo) {
      boolean isSuccess = Tryable.isSuccess(maybeTryable);
      output.writeBoolean(isSuccess);
      if (isSuccess) {
        kryo.execute(
            output, (Rql2TypeWithProperties) type.cloneAndRemoveProp(isTryable), maybeTryable);
      } else {
        ErrorObject error = (ErrorObject) maybeTryable;
        output.writeString(error.getMessage());
      }
    }

    @Specialization(
        guards = {"isNullable(type)"},
        limit = "1")
    static void doNullable(
        Output output,
        Rql2TypeWithProperties type,
        Object maybeOption,
        @Cached KryoWriteNode kryo) {
      boolean isDefined = Nullable.isNotNull(maybeOption);
      output.writeBoolean(isDefined);
      if (isDefined) {
        kryo.execute(
            output, (Rql2TypeWithProperties) type.cloneAndRemoveProp(isNullable), maybeOption);
      }
    }

    @Specialization(
        guards = {"isListKind(type)"},
        limit = "1")
    static void doList(
        Output output,
        Rql2TypeWithProperties type,
        Object o,
        @Cached KryoWriteNode kryo,
        @CachedLibrary("o") ListLibrary lists) {
      output.writeInt((int) lists.size(o));
      Rql2TypeWithProperties elementType =
          (Rql2TypeWithProperties) ((Rql2ListType) type).innerType();
      Object[] contents = (Object[]) lists.getInnerList(o);
      for (Object content : contents) {
        kryo.execute(output, elementType, content);
      }
    }

    @Specialization(
        guards = {"isIterableKind(type)"},
        limit = "1")
    static void doIterable(
        Output output,
        Rql2TypeWithProperties type,
        Object o,
        @Cached KryoWriteNode kryo,
        @Cached IterableNodes.GetGeneratorNode iterators,
        @CachedLibrary(limit = "2") GeneratorLibrary generators) {
      Rql2TypeWithProperties elementType =
          (Rql2TypeWithProperties) ((Rql2IterableType) type).innerType();
      Object generator = iterators.execute(o);
      generators.init(generator);
      ArrayList<Object> contents = new ArrayList<>();
      while (generators.hasNext(generator)) {
        Object content = generators.next(generator);
        contents.add(content);
      }
      generators.close(generator);
      output.writeInt(contents.size());
      for (Object content : contents) {
        kryo.execute(output, elementType, content);
      }
    }

    @Specialization(
        guards = {"isRecordKind(type)"},
        limit = "1")
    static void doRecord(
        Output output,
        Rql2RecordType type,
        Object o,
        @Cached KryoWriteNode kryo,
        @CachedLibrary("o") InteropLibrary recordLibrary,
        @CachedLibrary(limit = "2") InteropLibrary arrayLibrary) {
      try {
        Object keys = recordLibrary.getMembers(o);
        long length = arrayLibrary.getArraySize(keys);
        Vector<Rql2AttrType> atts = type.atts();
        for (int i = 0; i < length; i++) {
          String member = (String) arrayLibrary.readArrayElement(keys, i);
          Object field = recordLibrary.readMember(o, member);
          kryo.execute(output, (Rql2TypeWithProperties) atts.apply(i).tipe(), field);
        }
      } catch (UnsupportedMessageException
          | InvalidArrayIndexException
          | UnknownIdentifierException e) {
        throw new RawTruffleInternalErrorException(e);
      }
    }

    @Specialization(guards = {"isDateKind(type)"})
    static void doDate(Output output, Rql2TypeWithProperties type, DateObject o) {
      LocalDate date = o.getDate();
      output.writeInt(date.getYear());
      output.writeInt(date.getMonthValue());
      output.writeInt(date.getDayOfMonth());
    }

    @Specialization(guards = {"isTimeKind(type)"})
    static void doTime(Output output, Rql2TypeWithProperties type, TimeObject o) {
      LocalTime time = o.getTime();
      output.writeInt(time.getHour());
      output.writeInt(time.getMinute());
      output.writeInt(time.getSecond());
      output.writeInt(time.getNano() / 1000000);
    }

    @Specialization(guards = {"isTimestampKind(type)"})
    static void doTimestamp(Output output, Rql2TypeWithProperties type, TimestampObject o) {
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
    static void doInterval(Output output, Rql2TypeWithProperties type, IntervalObject o) {
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
    static void doByte(Output output, Rql2TypeWithProperties type, byte o) {
      output.writeByte(o);
    }

    @Specialization(guards = {"isShortKind(type)"})
    static void doShort(Output output, Rql2TypeWithProperties type, short o) {
      output.writeShort(o);
    }

    @Specialization(guards = {"isIntKind(type)"})
    static void doInt(Output output, Rql2TypeWithProperties type, int o) {
      output.writeInt(o);
    }

    @Specialization(guards = {"isLongKind(type)"})
    static void doLong(Output output, Rql2TypeWithProperties type, long o) {
      output.writeLong(o);
    }

    @Specialization(guards = {"isFloatKind(type)"})
    static void doFloat(Output output, Rql2TypeWithProperties type, float o) {
      output.writeFloat(o);
    }

    @Specialization
    static void doDouble(Output output, Rql2TypeWithProperties type, double o) {
      output.writeDouble(o);
    }

    @Specialization
    static void doDecimal(Output output, Rql2TypeWithProperties type, DecimalObject o) {
      output.writeString(o.getBigDecimal().toString());
    }

    @Specialization
    static void doString(Output output, Rql2TypeWithProperties type, String o) {
      output.writeString(o);
    }

    @Specialization
    static void doBool(Output output, Rql2TypeWithProperties type, boolean o) {
      output.writeBoolean(o);
    }
  }
}
