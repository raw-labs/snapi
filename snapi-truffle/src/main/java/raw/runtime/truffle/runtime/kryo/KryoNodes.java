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
import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
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
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.generator.collection.GeneratorNodes;
import raw.runtime.truffle.runtime.iterable.IterableNodes;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.tryable_nullable.Nullable;
import raw.runtime.truffle.tryable_nullable.Tryable;
import scala.collection.immutable.Vector;

public class KryoNodes {
  @NodeInfo(shortName = "Kryo.Read")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(TypeGuards.class)
  public abstract static class KryoReadNode extends Node {

    public abstract Object execute(Node node, Input input, Rql2TypeWithProperties t);

    @Specialization(guards = {"isTryable(t)"})
    @CompilerDirectives.TruffleBoundary
    static Object doTryable(
        Node node,
        Input input,
        Rql2TypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoReadNode kryo) {
      boolean isSuccess = input.readBoolean();
      if (isSuccess) {
        Rql2TypeWithProperties successType =
            (Rql2TypeWithProperties) t.cloneAndRemoveProp(new Rql2IsTryableTypeProperty());
        return kryo.execute(thisNode, input, successType);
      } else {
        String error = input.readString();
        return new ErrorObject(error);
      }
    }

    @Specialization(guards = {"isNullable(t)"})
    @CompilerDirectives.TruffleBoundary
    static Object doNullable(
        Node node,
        Input input,
        Rql2TypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoReadNode kryo) {
      boolean isDefined = input.readBoolean();
      if (isDefined) {
        Rql2TypeWithProperties innerType =
            (Rql2TypeWithProperties) t.cloneAndRemoveProp(new Rql2IsNullableTypeProperty());
        return kryo.execute(thisNode, input, innerType);
      } else {
        return NullObject.INSTANCE;
      }
    }

    @Specialization(guards = {"isListKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static ObjectList doList(
        Node node,
        Input input,
        Rql2TypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("kryoRead") KryoReadNode kryo) {
      Rql2ListType listType = (Rql2ListType) t;
      Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) listType.innerType();
      int size = input.readInt();
      Object[] values = new Object[size];
      for (int i = 0; i < size; i++) {
        values[i] = kryo.execute(thisNode, input, innerType);
      }
      return new ObjectList(values);
    }

    @Specialization(guards = {"isIterableKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static Object doIterable(
        Node node,
        Input input,
        Rql2TypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("kryoRead") KryoReadNode kryo) {
      Rql2IterableType iterableType = (Rql2IterableType) t;
      Rql2TypeWithProperties innerType = (Rql2TypeWithProperties) iterableType.innerType();
      int size = input.readInt();
      Object[] values = new Object[size];
      for (int i = 0; i < size; i++) {
        values[i] = kryo.execute(node, input, innerType);
      }
      return new ObjectList(values).toIterable();
    }

    @Specialization(guards = {"isRecordKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static Object doRecord(
        Node node,
        Input input,
        Rql2TypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached RecordNodes.AddPropNode addPropNode,
        @Cached(inline = false) @Cached.Shared("kryoRead") KryoReadNode kryo) {
      Rql2RecordType recordType = (Rql2RecordType) t;
      Object record = RawLanguage.get(thisNode).createPureRecord();
      recordType
          .atts()
          .forall(
              att -> {
                Rql2TypeWithProperties attType = (Rql2TypeWithProperties) att.tipe();
                Object value = kryo.execute(thisNode, input, attType);
                addPropNode.execute(thisNode, record, att.idn(), value);
                return true;
              });
      return record;
    }

    @Specialization(guards = {"isIntervalKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static IntervalObject doInterval(Node node, Input input, Rql2TypeWithProperties t) {
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
    @CompilerDirectives.TruffleBoundary
    static TimeObject doTime(Node node, Input input, Rql2TypeWithProperties t) {
      int hours = input.readInt();
      int minutes = input.readInt();
      int seconds = input.readInt();
      int millis = input.readInt();
      return new TimeObject(
          LocalTime.of(hours, minutes, seconds, (int) TimeUnit.MILLISECONDS.toNanos(millis)));
    }

    @Specialization(guards = {"isDateKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static DateObject doDate(Node node, Input input, Rql2TypeWithProperties t) {
      int year = input.readInt();
      int month = input.readInt();
      int day = input.readInt();
      return new DateObject(LocalDate.of(year, month, day));
    }

    @Specialization(guards = {"isTimestampKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static TimestampObject doTimestamp(Node node, Input input, Rql2TypeWithProperties t) {
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
    @CompilerDirectives.TruffleBoundary
    static boolean doBoolean(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readBoolean();
    }

    @Specialization(guards = {"isStringKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static String doString(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readString();
    }

    @Specialization(guards = {"isDecimalKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static DecimalObject doDecimal(Node node, Input input, Rql2TypeWithProperties t) {
      return new DecimalObject(new BigDecimal(input.readString()));
    }

    @Specialization(guards = {"isDoubleKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static double doDouble(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readDouble();
    }

    @Specialization(guards = {"isFloatKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static float doFloat(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readFloat();
    }

    @Specialization(guards = {"isLongKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static long doLong(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readLong();
    }

    @Specialization(guards = {"isIntKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static int doInt(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readInt();
    }

    @Specialization(guards = {"isShortKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static short doShort(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readShort();
    }

    @Specialization(guards = {"isByteKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static byte doByte(Node node, Input input, Rql2TypeWithProperties t) {
      return input.readByte();
    }
  }

  @NodeInfo(shortName = "Kryo.Write")
  @GenerateUncached
  @ImportStatic(TypeGuards.class)
  @GenerateInline
  public abstract static class KryoWriteNode extends Node {

    private static final Rql2TypeProperty isTryable = new Rql2IsTryableTypeProperty();
    private static final Rql2TypeProperty isNullable = new Rql2IsNullableTypeProperty();

    public abstract void execute(
        Node node, Output output, Rql2TypeWithProperties type, Object maybeTryable);

    @Specialization(guards = "isTryable(type)")
    static void doTryable(
        Node node,
        Output output,
        Rql2TypeWithProperties type,
        Object maybeTryable,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoWriteNode kryo) {
      boolean isSuccess = Tryable.isSuccess(maybeTryable);
      output.writeBoolean(isSuccess);
      if (isSuccess) {
        kryo.execute(
            thisNode,
            output,
            (Rql2TypeWithProperties) type.cloneAndRemoveProp(isTryable),
            maybeTryable);
      } else {
        ErrorObject error = (ErrorObject) maybeTryable;
        output.writeString(error.getMessage());
      }
    }

    @Specialization(guards = "isNullable(type)")
    static void doNullable(
        Node node,
        Output output,
        Rql2TypeWithProperties type,
        Object maybeOption,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoWriteNode kryo) {
      boolean isDefined = Nullable.isNotNull(maybeOption);
      output.writeBoolean(isDefined);
      if (isDefined) {
        kryo.execute(
            thisNode,
            output,
            (Rql2TypeWithProperties) type.cloneAndRemoveProp(isNullable),
            maybeOption);
      }
    }

    @Specialization(guards = "isListKind(type)")
    static void doList(
        Node node,
        Output output,
        Rql2TypeWithProperties type,
        Object o,
        @Bind("$node") Node thisNode,
        @Cached ListNodes.SizeNode sizeNode,
        @Cached ListNodes.GetNode getNode,
        @Cached(inline = false) @Cached.Shared("kryo") KryoWriteNode kryo) {
      int size = (int) sizeNode.execute(thisNode, o);
      output.writeInt(size);
      Rql2TypeWithProperties elementType =
          (Rql2TypeWithProperties) ((Rql2ListType) type).innerType();
      for (int i = 0; i < size; i++) {
        Object item = getNode.execute(thisNode, o, i);
        kryo.execute(thisNode, output, elementType, item);
      }
    }

    @Specialization(guards = "isIterableKind(type)")
    static void doIterable(
        Node node,
        Output output,
        Rql2TypeWithProperties type,
        Object o,
        @Bind("$node") Node thisNode,
        @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
        @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
        @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode,
        @Cached(inline = false) @Cached.Shared("kryo") KryoWriteNode kryo,
        @Cached(inline = false) IterableNodes.GetGeneratorNode getGeneratorNode) {
      Rql2TypeWithProperties elementType =
          (Rql2TypeWithProperties) ((Rql2IterableType) type).innerType();
      Object generator = getGeneratorNode.execute(thisNode, o);
      try {
        generatorInitNode.execute(thisNode, generator);
        ArrayList<Object> contents = new ArrayList<>();
        while (generatorHasNextNode.execute(thisNode, generator)) {
          Object content = generatorNextNode.execute(thisNode, generator);
          contents.add(content);
        }
        output.writeInt(contents.size());
        for (Object content : contents) {
          kryo.execute(thisNode, output, elementType, content);
        }
      } finally {
        generatorCloseNode.execute(thisNode, generator);
      }
    }

    @Specialization(guards = {"isRecordKind(type)"})
    static void doRecord(
        Node node,
        Output output,
        Rql2RecordType type,
        Object o,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Shared("kryo") KryoWriteNode kryo,
        @Cached RecordNodes.GetKeysNode getKeysNode,
        @Cached RecordNodes.GetValueNode getValueNode) {
      Object[] keys = getKeysNode.execute(thisNode, o);
      Vector<Rql2AttrType> atts = type.atts();
      for (int i = 0; i < keys.length; i++) {
        Object field = getValueNode.execute(thisNode, o, (String) keys[i]);
        kryo.execute(thisNode, output, (Rql2TypeWithProperties) atts.apply(i).tipe(), field);
      }
    }

    @Specialization(guards = {"isDateKind(type)"})
    static void doDate(Node node, Output output, Rql2TypeWithProperties type, DateObject o) {
      LocalDate date = o.getDate();
      output.writeInt(date.getYear());
      output.writeInt(date.getMonthValue());
      output.writeInt(date.getDayOfMonth());
    }

    @Specialization(guards = {"isTimeKind(type)"})
    static void doTime(Node node, Output output, Rql2TypeWithProperties type, TimeObject o) {
      LocalTime time = o.getTime();
      output.writeInt(time.getHour());
      output.writeInt(time.getMinute());
      output.writeInt(time.getSecond());
      output.writeInt(time.getNano() / 1000000);
    }

    @Specialization(guards = {"isTimestampKind(type)"})
    static void doTimestamp(
        Node node, Output output, Rql2TypeWithProperties type, TimestampObject o) {
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
        Node node, Output output, Rql2TypeWithProperties type, IntervalObject o) {
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
    static void doByte(Node node, Output output, Rql2TypeWithProperties type, byte o) {
      output.writeByte(o);
    }

    @Specialization(guards = {"isShortKind(type)"})
    static void doShort(Node node, Output output, Rql2TypeWithProperties type, short o) {
      output.writeShort(o);
    }

    @Specialization(guards = {"isIntKind(type)"})
    static void doInt(Node node, Output output, Rql2TypeWithProperties type, int o) {
      output.writeInt(o);
    }

    @Specialization(guards = {"isLongKind(type)"})
    static void doLong(Node node, Output output, Rql2TypeWithProperties type, long o) {
      output.writeLong(o);
    }

    @Specialization(guards = {"isFloatKind(type)"})
    static void doFloat(Node node, Output output, Rql2TypeWithProperties type, float o) {
      output.writeFloat(o);
    }

    @Specialization
    static void doDouble(Node node, Output output, Rql2TypeWithProperties type, double o) {
      output.writeDouble(o);
    }

    @Specialization
    static void doDecimal(Node node, Output output, Rql2TypeWithProperties type, DecimalObject o) {
      output.writeString(o.getBigDecimal().toString());
    }

    @Specialization
    static void doString(Node node, Output output, Rql2TypeWithProperties type, String o) {
      output.writeString(o);
    }

    @Specialization
    static void doBool(Node node, Output output, Rql2TypeWithProperties type, boolean o) {
      output.writeBoolean(o);
    }
  }
}
