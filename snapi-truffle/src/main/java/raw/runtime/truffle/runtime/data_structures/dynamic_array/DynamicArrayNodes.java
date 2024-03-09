// package raw.runtime.truffle.runtime.data_structures.dynamic_array;
//
// import com.oracle.truffle.api.dsl.*;
// import com.oracle.truffle.api.library.CachedLibrary;
// import com.oracle.truffle.api.nodes.Node;
// import com.oracle.truffle.api.nodes.NodeInfo;
// import com.oracle.truffle.api.nodes.UnexpectedResultException;
// import com.oracle.truffle.api.object.DynamicObjectLibrary;
// import raw.runtime.truffle.runtime.data_structures.PropertyType;
// import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
//
// import static raw.runtime.truffle.runtime.data_structures.PropertyType.*;
//
// public class DynamicArrayNodes {
//
//  @NodeInfo(shortName = "DynamicArray.Add")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class ArrayAddNode extends Node {
//
//    public abstract void execute(Node node, DynamicArrayObject array, Object item);
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        DynamicArrayObject array,
//        int item,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      int size = valuesLibrary.getKeyArray(array).length;
//      valuesLibrary.putInt(array, String.valueOf(size), item);
//      valuesLibrary.setPropertyFlags(array, String.valueOf(size), INT_TYPE);
//    }
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        DynamicArrayObject array,
//        long item,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      int size = valuesLibrary.getKeyArray(array).length;
//      valuesLibrary.putLong(array, String.valueOf(size), item);
//      valuesLibrary.setPropertyFlags(array, String.valueOf(size), LONG_TYPE);
//    }
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        DynamicArrayObject array,
//        double item,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      int size = valuesLibrary.getKeyArray(array).length;
//      valuesLibrary.putDouble(array, String.valueOf(size), item);
//      valuesLibrary.setPropertyFlags(array, String.valueOf(size), DOUBLE_TYPE);
//    }
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        DynamicArrayObject array,
//        Object item,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      int size = valuesLibrary.getKeyArray(array).length;
//      valuesLibrary.putWithFlags(array, String.valueOf(size), item, OBJECT_TYPE);
//    }
//  }
//
//  @NodeInfo(shortName = "DynamicArray.Size")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class ArraySizeNode extends Node {
//
//    public abstract int execute(Node node, DynamicArrayObject array);
//
//    @Specialization(limit = "3")
//    static int exec(
//        Node node,
//        DynamicArrayObject array,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      return valuesLibrary.getKeyArray(array).length;
//    }
//  }
//
//  @NodeInfo(shortName = "DynamicArray.Get")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class ArrayGetNode extends Node {
//
//    public abstract Object execute(Node node, DynamicArrayObject array, int index);
//
//    @Specialization(
//        limit = "3",
//        guards =
//            "valuesLibrary.getPropertyFlagsOrDefault(array, String.valueOf(index), 5) ==
// INT_TYPE")
//    static int getInt(
//        Node node,
//        DynamicArrayObject array,
//        int index,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      try {
//        return valuesLibrary.getIntOrDefault(array, String.valueOf(index), -1);
//      } catch (UnexpectedResultException e) {
//        throw new RawTruffleInternalErrorException("Unexpected result", e);
//      }
//    }
//
//    @Specialization(limit = "3")
//    static long getLong(
//        Node node,
//        DynamicArrayObject array,
//        int index,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      try {
//        return valuesLibrary.getLongOrDefault(array, String.valueOf(index), -1);
//      } catch (UnexpectedResultException e) {
//        throw new RawTruffleInternalErrorException("Unexpected result", e);
//      }
//    }
//
//    @Specialization(limit = "3")
//    static long getDouble(
//        Node node,
//        DynamicArrayObject array,
//        int index,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      try {
//        return valuesLibrary.getLongOrDefault(array, String.valueOf(index), -1);
//      } catch (UnexpectedResultException e) {
//        throw new RawTruffleInternalErrorException("Unexpected result", e);
//      }
//    }
//
//    @Specialization(limit = "3")
//    static Object getObject(
//        Node node,
//        DynamicArrayObject array,
//        int index,
//        @CachedLibrary("array") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      return valuesLibrary.getOrDefault(array, String.valueOf(index), null);
//    }
//  }
// }
