package raw.runtime.truffle.ast.expressions.builtin.type_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.compiler.rql2.source.Rql2IsNullableTypeProperty;
import raw.compiler.rql2.source.Rql2IsTryableTypeProperty;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.iterable.list.ListIterable;
import raw.runtime.truffle.runtime.iterable.operations.*;
import raw.runtime.truffle.runtime.iterable.sources.*;
import raw.runtime.truffle.runtime.list.*;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.DuplicateKeyRecord;
import raw.runtime.truffle.runtime.record.PureRecord;

import static raw.runtime.truffle.boundary.RawTruffleBoundaries.parseInt;
import static raw.runtime.truffle.runtime.primitives.TruffleTemporalFormatter.*;

public class TypeCastNodes {

  @NodeInfo(shortName = "TypeCastAny")
  @GenerateUncached
  @GenerateInline
  @ImportStatic(TypeGuards.class)
  public abstract static class TypeCastAnyRecursiveNode extends Node {

    public static final Rql2IsTryableTypeProperty tryable = Rql2IsTryableTypeProperty.apply();
    public static final Rql2IsNullableTypeProperty nullable = Rql2IsNullableTypeProperty.apply();

    public abstract Object execute(Node node, Object value, Rql2TypeWithProperties tipe);

    @Specialization(guards = {"isTryable(tipe)"})
    @CompilerDirectives.TruffleBoundary
    static Object doTryable(
        Node node,
        Object value,
        Rql2TypeWithProperties tipe,
        @Cached(inline = false) @Cached.Exclusive TypeCastAnyRecursiveNode castNode) {
      Rql2TypeWithProperties t = (Rql2TypeWithProperties) tipe.cloneAndRemoveProp(tryable);
      return castNode.execute(castNode, value, t);
    }

    @Specialization(guards = {"isNullable(tipe)"})
    @CompilerDirectives.TruffleBoundary
    static Object doNullable(
        Node node,
        Object value,
        Rql2TypeWithProperties tipe,
        @Cached(inline = false) @Cached.Exclusive TypeCastAnyRecursiveNode castNode) {
      Rql2TypeWithProperties t = (Rql2TypeWithProperties) tipe.cloneAndRemoveProp(nullable);
      return castNode.execute(castNode, value, t);
    }

    @Specialization(guards = {"isListKind(tipe)"})
    static Object doList(
        Node node,
        Object value,
        Rql2TypeWithProperties tipe,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive TypeCastNodes.TypeCastCollectionNode castListNode) {
      return castListNode.execute(thisNode, value);
    }

    @Specialization(guards = {"isIterableKind(tipe)"})
    static Object doIterable(
        Node node,
        Object value,
        Rql2TypeWithProperties tipe,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive TypeCastNodes.TypeCastCollectionNode castCollection) {
      return castCollection.execute(thisNode, value);
    }

    @Specialization(guards = {"isRecordKind(tipe)"})
    @ExplodeLoop
    static Object doRecord(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof PureRecord || value instanceof DuplicateKeyRecord) {
        return value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isIntervalKind(tipe)"})
    static IntervalObject doInterval(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof IntervalObject) {
        return (IntervalObject) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isTimeKind(tipe)"})
    static TimeObject doTime(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof TimeObject) {
        return (TimeObject) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isDateKind(tipe)"})
    static DateObject doDate(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof DateObject) {
        return (DateObject) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isTimestampKind(tipe)"})
    static TimestampObject doTimestamp(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof TimestampObject) {
        return (TimestampObject) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isBooleanKind(tipe)"})
    static boolean doBoolean(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof Boolean) {
        return (Boolean) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isStringKind(tipe)"})
    static String doString(
        Node node,
        Object value,
        Rql2TypeWithProperties tipe,
        @Cached @Cached.Exclusive TypeCastStringNode castStringNode) {
      return castStringNode.execute(node, value);
    }

    @Specialization(guards = {"isDecimalKind(tipe)"})
    static DecimalObject doDecimal(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof DecimalObject) {
        return (DecimalObject) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isDoubleKind(tipe)"})
    static double doDouble(
        Node node,
        Object value,
        Rql2TypeWithProperties tipe,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Exclusive TypeCastDoubleNode castDoubleNode) {
      return castDoubleNode.execute(thisNode, value);
    }

    @Specialization(guards = {"isFloatKind(tipe)"})
    static float doFloat(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof Float) {
        return (Float) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isLongKind(tipe)"})
    static long doLong(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof Long) {
        return (Long) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isIntKind(tipe)"})
    static int doInt(
        Node node,
        Object value,
        Rql2TypeWithProperties tipe,
        @Cached @Cached.Exclusive TypeCastIntNode castIntNode) {
      return castIntNode.execute(node, value);
    }

    @Specialization(guards = {"isShortKind(tipe)"})
    static short doShort(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof Short) {
        return (Short) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }

    @Specialization(guards = {"isByteKind(tipe)"})
    static byte doByte(Node node, Object value, Rql2TypeWithProperties tipe) {
      if (value instanceof Byte) {
        return (Byte) value;
      }
      throw new RawTruffleRuntimeException("Type cast error");
    }
  }

  @NodeInfo(shortName = "TypeCastCollection")
  @GenerateUncached
  @GenerateInline
  public abstract static class TypeCastCollectionNode extends Node {

    public abstract Object execute(Node node, Object collection);

    @Specialization
    static ListIterable exec(Node node, ListIterable collection) {
      return collection;
    }

    @Specialization
    static DistinctCollection exec(Node node, DistinctCollection collection) {
      return collection;
    }

    @Specialization
    static EquiJoinCollection exec(Node node, EquiJoinCollection collection) {
      return collection;
    }

    @Specialization
    static FilterCollection exec(Node node, FilterCollection collection) {
      return collection;
    }

    @Specialization
    static GroupByCollection exec(Node node, GroupByCollection collection) {
      return collection;
    }

    @Specialization
    static JoinCollection exec(Node node, JoinCollection collection) {
      return collection;
    }

    @Specialization
    static OrderByCollection exec(Node node, OrderByCollection collection) {
      return collection;
    }

    @Specialization
    static TakeCollection exec(Node node, TakeCollection collection) {
      return collection;
    }

    @Specialization
    static TransformCollection exec(Node node, TransformCollection collection) {
      return collection;
    }

    @Specialization
    static UnnestCollection exec(Node node, UnnestCollection collection) {
      return collection;
    }

    @Specialization
    static ZipCollection exec(Node node, ZipCollection collection) {
      return collection;
    }

    @Specialization
    static CsvCollection exec(Node node, CsvCollection collection) {
      return collection;
    }

    @Specialization
    static CsvFromStringCollection exec(Node node, CsvFromStringCollection collection) {
      return collection;
    }

    @Specialization
    static EmptyCollection exec(Node node, EmptyCollection collection) {
      return collection;
    }

    @Specialization
    static ExpressionCollection exec(Node node, ExpressionCollection collection) {
      return collection;
    }

    @Specialization
    static IntRangeCollection exec(Node node, IntRangeCollection collection) {
      return collection;
    }

    @Specialization
    static JdbcQueryCollection exec(Node node, JdbcQueryCollection collection) {
      return collection;
    }

    @Specialization
    static JsonReadCollection exec(Node node, JsonReadCollection collection) {
      return collection;
    }

    @Specialization
    static LongRangeCollection exec(Node node, LongRangeCollection collection) {
      return collection;
    }

    @Specialization
    static ReadLinesCollection exec(Node node, ReadLinesCollection collection) {
      return collection;
    }

    @Specialization
    static TimestampRangeCollection exec(Node node, TimestampRangeCollection collection) {
      return collection;
    }

    @Specialization
    static UnionCollection exec(Node node, UnionCollection collection) {
      return collection;
    }

    @Specialization
    static XmlParseCollection exec(Node node, XmlParseCollection collection) {
      return collection;
    }

    @Specialization
    static XmlReadCollection exec(Node node, XmlReadCollection collection) {
      return collection;
    }

    @Specialization
    static ListIterable exec(Node node, BooleanList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, ByteList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, DoubleList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, FloatList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, IntList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, LongList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, ObjectList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, RawArrayList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, ShortList list) {
      return list.toIterable();
    }

    @Specialization
    static ListIterable exec(Node node, StringList list) {
      return list.toIterable();
    }

    @CompilerDirectives.TruffleBoundary
    public static boolean invalidateCache(Object collection) {
      return !(collection instanceof ListIterable
          || collection instanceof DistinctCollection
          || collection instanceof EquiJoinCollection
          || collection instanceof FilterCollection
          || collection instanceof GroupByCollection
          || collection instanceof JoinCollection
          || collection instanceof OrderByCollection
          || collection instanceof TakeCollection
          || collection instanceof TransformCollection
          || collection instanceof UnnestCollection
          || collection instanceof ZipCollection
          || collection instanceof CsvCollection
          || collection instanceof CsvFromStringCollection
          || collection instanceof EmptyCollection
          || collection instanceof ExpressionCollection
          || collection instanceof IntRangeCollection
          || collection instanceof JdbcQueryCollection
          || collection instanceof JsonReadCollection
          || collection instanceof LongRangeCollection
          || collection instanceof ReadLinesCollection
          || collection instanceof TimestampRangeCollection
          || collection instanceof UnionCollection
          || collection instanceof XmlParseCollection
          || collection instanceof XmlReadCollection);
    }

    @Specialization(guards = "invalidateCache(collection)")
    static Object exec(Node node, Object collection) {
      throw new RawTruffleRuntimeException("Type cast error");
    }
  }

  @NodeInfo(shortName = "TypeCastList")
  @GenerateUncached
  @GenerateInline
  public abstract static class TypeCastListNode extends Node {

    public abstract Object execute(Node node, Object lst);

    @Specialization
    static BooleanList exec(Node node, BooleanList list) {
      return list;
    }

    @Specialization
    static ByteList exec(Node node, ByteList list) {
      return list;
    }

    @Specialization
    static DoubleList exec(Node node, DoubleList list) {
      return list;
    }

    @Specialization
    static FloatList exec(Node node, FloatList list) {
      return list;
    }

    @Specialization
    static IntList exec(Node node, IntList list) {
      return list;
    }

    @Specialization
    static LongList exec(Node node, LongList list) {
      return list;
    }

    @Specialization
    static ObjectList exec(Node node, ObjectList list) {
      return list;
    }

    @Specialization
    static RawArrayList exec(Node node, RawArrayList list) {
      return list;
    }

    @Specialization
    static ShortList exec(Node node, ShortList list) {
      return list;
    }

    @Specialization
    static StringList exec(Node node, StringList list) {
      return list;
    }

    @CompilerDirectives.TruffleBoundary
    public static boolean invalidateCache(Object list) {
      return !(list instanceof BooleanList
          || list instanceof ByteList
          || list instanceof DoubleList
          || list instanceof FloatList
          || list instanceof IntList
          || list instanceof LongList
          || list instanceof ObjectList
          || list instanceof RawArrayList
          || list instanceof ShortList
          || list instanceof StringList);
    }

    @Specialization(guards = "invalidateCache(list)")
    static Object execNonList(Node node, Object list) {
      throw new RawTruffleRuntimeException("Type cast error");
    }
  }

  // (az) To do replace all "From" nodes with "TypeCast" nodes
  // have in mind that it throws so, wrap it in a try-catch
  @NodeInfo(shortName = "TypeCastDouble")
  @GenerateUncached
  @GenerateInline
  public abstract static class TypeCastDoubleNode extends Node {

    public abstract double execute(Node node, Object argument);

    @Specialization
    protected double fromByte(Node node, byte argument) {
      return argument;
    }

    @Specialization
    protected double fromShort(Node node, short argument) {
      return argument;
    }

    @Specialization
    protected double fromInt(Node node, int argument) {
      return argument;
    }

    @Specialization
    protected double fromLong(Node node, long argument) {
      return argument;
    }

    @Specialization
    protected double fromFloat(Node node, float argument) {
      return argument;
    }

    @Specialization
    protected double fromDouble(Node node, double argument) {
      return argument;
    }

    @Specialization
    protected double fromDecimal(DecimalObject argument) {
      return argument.getBigDecimal().doubleValue();
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected double fromString(String argument) {
      try {
        return Double.parseDouble(argument);
      } catch (RuntimeException ex) {
        throw new RawTruffleRuntimeException("cannot cast '" + argument + "' to double");
      }
    }

    public static boolean invalidateCache(Object argument) {
      return !(argument instanceof Byte
          || argument instanceof Short
          || argument instanceof Integer
          || argument instanceof Long
          || argument instanceof Float
          || argument instanceof Double
          || argument instanceof DecimalObject
          || argument instanceof String);
    }

    @Specialization(guards = "invalidateCache(argument)")
    protected double fromNotValid(Object argument) {
      throw new RawTruffleRuntimeException("Type cast error");
    }
  }

  @NodeInfo(shortName = "TypeCastDouble")
  @GenerateUncached
  @GenerateInline
  public abstract static class TypeCastIntNode extends Node {

    public abstract int execute(Node node, Object argument);

    @Specialization
    protected int fromByte(Node node, byte argument) {
      return argument;
    }

    @Specialization
    protected int fromShort(Node node, short argument) {
      return argument;
    }

    @Specialization
    protected int fromInt(Node node, int argument) {
      return argument;
    }

    @Specialization
    protected int fromLong(Node node, long argument) {
      return (int) argument;
    }

    @Specialization
    protected int fromFloat(Node node, float argument) {
      return (int) argument;
    }

    @Specialization
    protected int fromDouble(Node node, double argument) {
      return (int) argument;
    }

    @Specialization
    protected int fromDecimal(Node node, DecimalObject argument) {
      return argument.getBigDecimal().intValue();
    }

    @Specialization
    protected int fromString(Node node, String argument) {
      try {
        return parseInt(argument);
      } catch (RuntimeException ex) {
        throw new RawTruffleRuntimeException("cannot cast '" + argument + "' to int");
      }
    }

    public static boolean invalidateCache(Object argument) {
      return !(argument instanceof Byte
          || argument instanceof Short
          || argument instanceof Integer
          || argument instanceof Long
          || argument instanceof Float
          || argument instanceof Double
          || argument instanceof DecimalObject
          || argument instanceof String);
    }

    @Specialization(guards = "invalidateCache(argument)")
    protected int fromNotValid(Node node, Object argument) {
      throw new RawTruffleRuntimeException("Type cast error");
    }
  }

  @NodeInfo(shortName = "TypeCastDouble")
  @GenerateUncached
  @GenerateInline
  public abstract static class TypeCastStringNode extends Node {

    public abstract String execute(Node node, Object argument);

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromByte(Node node, byte value) {
      return String.valueOf(value);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromShort(Node node, short value) {
      return String.valueOf(value);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromInt(Node node, int value) {
      return String.valueOf(value);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromLong(Node node, long value) {
      return String.valueOf(value);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromFloat(Node node, float value) {
      return String.valueOf(value);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromDouble(Node node, double value) {
      return String.valueOf(value);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromDecimal(Node node, DecimalObject value) {
      return value.getBigDecimal().toString();
    }

    @Specialization
    protected String fromBoolean(Node node, boolean value) {
      return String.valueOf(value);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromDate(Node node, DateObject value) {
      return value.getDate().format(DATE_FORMATTER);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromTime(Node node, TimeObject value) {
      return value.getTime().format(TIME_FORMATTER);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromTimestamp(Node node, TimestampObject value) {
      return value.getTimestamp().format(TIMESTAMP_FORMATTER);
    }

    @Specialization
    @CompilerDirectives.TruffleBoundary
    protected String fromInterval(Node node, IntervalObject value) {
      return value.toString();
    }

    public static boolean invalidateCache(Object argument) {
      return !(argument instanceof Byte
          || argument instanceof Short
          || argument instanceof Integer
          || argument instanceof Long
          || argument instanceof Float
          || argument instanceof Double
          || argument instanceof DecimalObject
          || argument instanceof String
          || argument instanceof Boolean
          || argument instanceof DateObject
          || argument instanceof TimeObject
          || argument instanceof TimestampObject
          || argument instanceof IntervalObject);
    }

    @Specialization(guards = "invalidateCache(argument)")
    protected String fromNotValid(Node node, Object argument) {
      throw new RawTruffleRuntimeException("Type cast error");
    }
  }
}
