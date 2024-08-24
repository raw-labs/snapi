/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.truffle.runtime.kryo;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.snapi.source.*;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.TypeGuards;
import com.rawlabs.snapi.truffle.ast.tryable_nullable.TryableNullableNodes;
import com.rawlabs.snapi.truffle.runtime.generator.collection.GeneratorNodes;
import com.rawlabs.snapi.truffle.runtime.iterable.IterableNodes;
import com.rawlabs.snapi.truffle.runtime.list.ListNodes;
import com.rawlabs.snapi.truffle.runtime.list.ObjectList;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodes;
import com.rawlabs.snapi.truffle.runtime.record.RecordNodesFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import scala.collection.immutable.Vector;

public class KryoNodes {
  @NodeInfo(shortName = "Kryo.Read")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(TypeGuards.class)
  public abstract static class KryoReadNode extends Node {

    public abstract Object execute(Node node, Input input, SnapiTypeWithProperties t);

    @Specialization(guards = {"isTryable(t)"})
    @CompilerDirectives.TruffleBoundary
    static Object doTryable(
        Node node,
        Input input,
        SnapiTypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoReadNode kryo) {
      boolean isSuccess = input.readBoolean();
      if (isSuccess) {
        SnapiTypeWithProperties successType =
            (SnapiTypeWithProperties) t.cloneAndRemoveProp(new SnapiIsTryableTypeProperty());
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
        SnapiTypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoReadNode kryo) {
      boolean isDefined = input.readBoolean();
      if (isDefined) {
        SnapiTypeWithProperties innerType =
            (SnapiTypeWithProperties) t.cloneAndRemoveProp(new SnapiIsNullableTypeProperty());
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
        SnapiTypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoReadNode kryo) {
      SnapiListType listType = (SnapiListType) t;
      SnapiTypeWithProperties innerType = (SnapiTypeWithProperties) listType.innerType();
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
        SnapiTypeWithProperties t,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) @Cached.Exclusive KryoReadNode kryo) {
      SnapiIterableType iterableType = (SnapiIterableType) t;
      SnapiTypeWithProperties innerType = (SnapiTypeWithProperties) iterableType.innerType();
      int size = input.readInt();
      Object[] values = new Object[size];
      for (int i = 0; i < size; i++) {
        values[i] = kryo.execute(node, input, innerType);
      }
      return new ObjectList(values).toIterable();
    }

    public static RecordNodes.AddPropNode[] createAddProps(int size) {
      RecordNodes.AddPropNode[] addProps = new RecordNodes.AddPropNode[size];
      for (int i = 0; i < size; i++) {
        addProps[i] = RecordNodesFactory.AddPropNodeGen.create();
      }
      return addProps;
    }

    public static KryoReadNode[] createKryoRead(int size) {
      KryoReadNode[] kryoRead = new KryoReadNode[size];
      for (int i = 0; i < size; i++) {
        kryoRead[i] = KryoNodesFactory.KryoReadNodeGen.create();
      }
      return kryoRead;
    }

    public static boolean hasDuplicateKeys(SnapiRecordType t) {
      Vector<SnapiAttrType> atts = t.atts();
      List<Object> list = new ArrayList<>();
      for (int i = 0; i < atts.size(); i++) {
        list.add(atts.apply(i).idn());
      }
      return list.size() != list.stream().distinct().count();
    }

    public static SnapiLanguage getSnapiLanguage(Node node) {
      return SnapiLanguage.get(node);
    }

    @Specialization(guards = {"isRecordKind(t)"})
    @ExplodeLoop
    static Object doRecord(
        Node node,
        Input input,
        SnapiRecordType t,
        @Bind("$node") Node thisNode,
        @Cached(value = "hasDuplicateKeys(t)", allowUncached = true) boolean hasDuplicateKeys,
        @Cached(value = "getSnapiLanguage(thisNode)", allowUncached = true) SnapiLanguage language,
        @Cached(value = "createAddProps(t.atts().size())", allowUncached = true)
            RecordNodes.AddPropNode[] addPropNode,
        @Cached(value = "addPropNode.length", allowUncached = true) int size,
        @Cached(value = "createKryoRead(size)", allowUncached = true) KryoReadNode[] kryo) {
      Object record = language.createPureRecord();
      for (int i = 0; i < size; i++) {
        SnapiTypeWithProperties attType = getTipe(t, i);
        Object value = kryo[i].execute(thisNode, input, attType);
        addPropNode[i].execute(thisNode, record, getIdn(t, i), value, hasDuplicateKeys);
      }
      return record;
    }

    @CompilerDirectives.TruffleBoundary
    public static SnapiTypeWithProperties getTipe(SnapiRecordType t, int index) {
      return (SnapiTypeWithProperties) t.atts().apply(index).tipe();
    }

    @CompilerDirectives.TruffleBoundary
    public static String getIdn(SnapiRecordType t, int index) {
      return t.atts().apply(index).idn();
    }

    @Specialization(guards = {"isIntervalKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static IntervalObject doInterval(Node node, Input input, SnapiTypeWithProperties t) {
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
    static TimeObject doTime(Node node, Input input, SnapiTypeWithProperties t) {
      int hours = input.readInt();
      int minutes = input.readInt();
      int seconds = input.readInt();
      int millis = input.readInt();
      return new TimeObject(
          LocalTime.of(hours, minutes, seconds, (int) TimeUnit.MILLISECONDS.toNanos(millis)));
    }

    @Specialization(guards = {"isDateKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static DateObject doDate(Node node, Input input, SnapiTypeWithProperties t) {
      int year = input.readInt();
      int month = input.readInt();
      int day = input.readInt();
      return new DateObject(LocalDate.of(year, month, day));
    }

    @Specialization(guards = {"isTimestampKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static TimestampObject doTimestamp(Node node, Input input, SnapiTypeWithProperties t) {
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
    static boolean doBoolean(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readBoolean();
    }

    @Specialization(guards = {"isStringKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static String doString(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readString();
    }

    @Specialization(guards = {"isDecimalKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static DecimalObject doDecimal(Node node, Input input, SnapiTypeWithProperties t) {
      return new DecimalObject(new BigDecimal(input.readString()));
    }

    @Specialization(guards = {"isDoubleKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static double doDouble(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readDouble();
    }

    @Specialization(guards = {"isFloatKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static float doFloat(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readFloat();
    }

    @Specialization(guards = {"isLongKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static long doLong(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readLong();
    }

    @Specialization(guards = {"isIntKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static int doInt(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readInt();
    }

    @Specialization(guards = {"isShortKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static short doShort(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readShort();
    }

    @Specialization(guards = {"isByteKind(t)"})
    @CompilerDirectives.TruffleBoundary
    static byte doByte(Node node, Input input, SnapiTypeWithProperties t) {
      return input.readByte();
    }
  }

  @NodeInfo(shortName = "Kryo.Write")
  @GenerateUncached
  @ImportStatic(TypeGuards.class)
  @GenerateInline
  public abstract static class KryoWriteNode extends Node {

    private static final SnapiTypeProperty isTryable = new SnapiIsTryableTypeProperty();
    private static final SnapiTypeProperty isNullable = new SnapiIsNullableTypeProperty();

    public abstract void execute(
        Node node, Output output, SnapiTypeWithProperties type, Object maybeTryable);

    @Specialization(guards = "isTryable(type)")
    @CompilerDirectives.TruffleBoundary
    static void doTryable(
        Node node,
        Output output,
        SnapiTypeWithProperties type,
        Object maybeTryable,
        @Bind("$node") Node thisNode,
        @Cached TryableNullableNodes.IsErrorNode isErrorNode,
        @Cached(inline = false) @Cached.Exclusive KryoWriteNode kryo) {
      boolean isSuccess = !isErrorNode.execute(thisNode, maybeTryable);
      output.writeBoolean(isSuccess);
      if (isSuccess) {
        kryo.execute(
            thisNode,
            output,
            (SnapiTypeWithProperties) type.cloneAndRemoveProp(isTryable),
            maybeTryable);
      } else {
        ErrorObject error = (ErrorObject) maybeTryable;
        output.writeString(error.getMessage());
      }
    }

    @Specialization(guards = "isNullable(type)")
    @CompilerDirectives.TruffleBoundary
    static void doNullable(
        Node node,
        Output output,
        SnapiTypeWithProperties type,
        Object maybeOption,
        @Bind("$node") Node thisNode,
        @Cached TryableNullableNodes.IsNullNode isNullNode,
        @Cached(inline = false) @Cached.Exclusive KryoWriteNode kryo) {
      boolean isDefined = !isNullNode.execute(thisNode, maybeOption);
      output.writeBoolean(isDefined);
      if (isDefined) {
        kryo.execute(
            thisNode,
            output,
            (SnapiTypeWithProperties) type.cloneAndRemoveProp(isNullable),
            maybeOption);
      }
    }

    @Specialization(guards = "isListKind(type)")
    @CompilerDirectives.TruffleBoundary
    static void doList(
        Node node,
        Output output,
        SnapiTypeWithProperties type,
        Object o,
        @Bind("$node") Node thisNode,
        @Cached ListNodes.SizeNode sizeNode,
        @Cached ListNodes.GetNode getNode,
        @Cached(inline = false) @Cached.Exclusive KryoWriteNode kryo) {
      int size = (int) sizeNode.execute(thisNode, o);
      output.writeInt(size);
      SnapiTypeWithProperties elementType =
          (SnapiTypeWithProperties) ((SnapiListType) type).innerType();
      for (int i = 0; i < size; i++) {
        Object item = getNode.execute(thisNode, o, i);
        kryo.execute(thisNode, output, elementType, item);
      }
    }

    @Specialization(guards = "isIterableKind(type)")
    @CompilerDirectives.TruffleBoundary
    static void doIterable(
        Node node,
        Output output,
        SnapiTypeWithProperties type,
        Object o,
        @Bind("$node") Node thisNode,
        @Cached GeneratorNodes.GeneratorInitNode generatorInitNode,
        @Cached GeneratorNodes.GeneratorHasNextNode generatorHasNextNode,
        @Cached GeneratorNodes.GeneratorNextNode generatorNextNode,
        @Cached GeneratorNodes.GeneratorCloseNode generatorCloseNode,
        @Cached(inline = false) @Cached.Exclusive KryoWriteNode kryo,
        @Cached(inline = false) IterableNodes.GetGeneratorNode getGeneratorNode) {
      SnapiTypeWithProperties elementType =
          (SnapiTypeWithProperties) ((SnapiIterableType) type).innerType();
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

    public static RecordNodes.GetValueNode[] createGetValue(int size) {
      RecordNodes.GetValueNode[] getValueNodes = new RecordNodes.GetValueNode[size];
      for (int i = 0; i < size; i++) {
        getValueNodes[i] = RecordNodesFactory.GetValueNodeGen.create();
      }
      return getValueNodes;
    }

    public static KryoWriteNode[] createKryoWrite(int size) {
      KryoWriteNode[] kryoWrite = new KryoWriteNode[size];
      for (int i = 0; i < size; i++) {
        kryoWrite[i] = KryoNodesFactory.KryoWriteNodeGen.create();
      }
      return kryoWrite;
    }

    @Specialization(guards = {"isRecordKind(type)"})
    @ExplodeLoop
    static void doRecord(
        Node node,
        Output output,
        SnapiRecordType type,
        Object o,
        @Bind("$node") Node thisNode,
        @Cached RecordNodes.GetKeysNode getKeysNode,
        @Cached(value = "getKeysNode.execute(thisNode, o)", dimensions = 1, allowUncached = true)
            Object[] keys,
        @Cached("keys.length") int size,
        @Cached(value = "createKryoWrite(size)", allowUncached = true) KryoWriteNode[] kryo,
        @Cached(value = "createGetValue(size)", allowUncached = true)
            RecordNodes.GetValueNode[] getValueNode) {
      for (int i = 0; i < size; i++) {
        Object field = getValueNode[i].execute(thisNode, o, keys[i]);
        kryo[i].execute(thisNode, output, getTipe(type, i), field);
      }
    }

    @CompilerDirectives.TruffleBoundary
    public static SnapiTypeWithProperties getTipe(SnapiRecordType t, int index) {
      return (SnapiTypeWithProperties) t.atts().apply(index).tipe();
    }

    @Specialization(guards = {"isDateKind(type)"})
    @CompilerDirectives.TruffleBoundary
    static void doDate(Node node, Output output, SnapiTypeWithProperties type, DateObject o) {
      LocalDate date = o.getDate();
      output.writeInt(date.getYear());
      output.writeInt(date.getMonthValue());
      output.writeInt(date.getDayOfMonth());
    }

    @Specialization(guards = {"isTimeKind(type)"})
    @CompilerDirectives.TruffleBoundary
    static void doTime(Node node, Output output, SnapiTypeWithProperties type, TimeObject o) {
      LocalTime time = o.getTime();
      output.writeInt(time.getHour());
      output.writeInt(time.getMinute());
      output.writeInt(time.getSecond());
      output.writeInt(time.getNano() / 1000000);
    }

    @Specialization(guards = {"isTimestampKind(type)"})
    @CompilerDirectives.TruffleBoundary
    static void doTimestamp(
        Node node, Output output, SnapiTypeWithProperties type, TimestampObject o) {
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
    @CompilerDirectives.TruffleBoundary
    static void doInterval(
        Node node, Output output, SnapiTypeWithProperties type, IntervalObject o) {
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
    @CompilerDirectives.TruffleBoundary
    static void doByte(Node node, Output output, SnapiTypeWithProperties type, byte o) {
      output.writeByte(o);
    }

    @Specialization(guards = {"isShortKind(type)"})
    @CompilerDirectives.TruffleBoundary
    static void doShort(Node node, Output output, SnapiTypeWithProperties type, short o) {
      output.writeShort(o);
    }

    @Specialization(guards = {"isIntKind(type)"})
    @CompilerDirectives.TruffleBoundary
    static void doInt(Node node, Output output, SnapiTypeWithProperties type, int o) {
      output.writeInt(o);
    }

    @Specialization(guards = {"isLongKind(type)"})
    @CompilerDirectives.TruffleBoundary
    static void doLong(Node node, Output output, SnapiTypeWithProperties type, long o) {
      output.writeLong(o);
    }

    @Specialization(guards = {"isFloatKind(type)"})
    @CompilerDirectives.TruffleBoundary
    static void doFloat(Node node, Output output, SnapiTypeWithProperties type, float o) {
      output.writeFloat(o);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static void doDouble(Node node, Output output, SnapiTypeWithProperties type, double o) {
      output.writeDouble(o);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static void doDecimal(Node node, Output output, SnapiTypeWithProperties type, DecimalObject o) {
      output.writeString(o.getBigDecimal().toString());
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static void doString(Node node, Output output, SnapiTypeWithProperties type, String o) {
      output.writeString(o);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    static void doBool(Node node, Output output, SnapiTypeWithProperties type, boolean o) {
      output.writeBoolean(o);
    }
  }
}
