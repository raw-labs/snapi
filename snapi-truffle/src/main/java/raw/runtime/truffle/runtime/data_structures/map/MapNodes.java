///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.runtime.truffle.runtime.data_structures.map;
//
//import com.oracle.truffle.api.dsl.*;
//import com.oracle.truffle.api.library.CachedLibrary;
//import com.oracle.truffle.api.nodes.Node;
//import com.oracle.truffle.api.nodes.NodeInfo;
//import com.oracle.truffle.api.nodes.UnexpectedResultException;
//import com.oracle.truffle.api.object.DynamicObjectLibrary;
//import raw.runtime.truffle.PropertyType;
//import raw.runtime.truffle.RawLanguage;
//import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
//import raw.runtime.truffle.runtime.operators.OperatorNodes;
//
//import static raw.runtime.truffle.PropertyType.*;
//
//public class MapNodes {
//  @NodeInfo(shortName = "Map.Add")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class MapAddNode extends Node {
//
//    public abstract void execute(Node node, MapObject mapObject, Object key, Object item);
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        int item,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      valuesLibrary.putInt(mapObject, key, item);
//      valuesLibrary.setPropertyFlags(mapObject, key, INT_TYPE);
//    }
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        long item,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      valuesLibrary.putLong(mapObject, key, item);
//      valuesLibrary.setPropertyFlags(mapObject, key, LONG_TYPE);
//    }
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        double item,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      valuesLibrary.putDouble(mapObject, key, item);
//      valuesLibrary.setPropertyFlags(mapObject, key, DOUBLE_TYPE);
//    }
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        Object item,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      valuesLibrary.putWithFlags(mapObject, key, item, OBJECT_TYPE);
//    }
//  }
//
//  @NodeInfo(shortName = "Map.Clear")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class MapClearNode extends Node {
//
//    public abstract void execute(Node node, MapObject mapObject);
//
//    @Specialization(limit = "3")
//    static void exec(
//        Node node,
//        MapObject mapObject,
//        @Bind("$node") Node thisNode,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      RawLanguage lang = RawLanguage.get(thisNode);
//      valuesLibrary.resetShape(mapObject, lang.mapShape);
//    }
//  }
//
//  @NodeInfo(shortName = "Map.Size")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class MapSizeNode extends Node {
//
//    public abstract int execute(Node node, MapObject mapObject);
//
//    @Specialization(limit = "3")
//    static int exec(
//        Node node,
//        MapObject mapObject,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      return valuesLibrary.getKeyArray(mapObject).length;
//    }
//  }
//
//  @NodeInfo(shortName = "Map.GetKeys")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class MapGetKeysNode extends Node {
//
//    public abstract Object[] execute(Node node, MapObject array);
//
//    @Specialization(limit = "3")
//    static Object[] exec(
//        Node node,
//        MapObject mapObject,
//        @Cached(inline = false) OperatorNodes.CompareUninlinedNode compareNode,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      return valuesLibrary.getKeyArray(mapObject);
//    }
//  }
//
//  @NodeInfo(shortName = "Map.GetKeysUnlined")
//  @GenerateUncached
//  @GenerateInline(false)
//  @ImportStatic(PropertyType.class)
//  public abstract static class MapGetKeysUninlinedNode extends Node {
//
//    public abstract Object[] execute(MapObject array);
//
//    @Specialization(limit = "3")
//    static Object[] exec(
//        MapObject mapObject,
//        @Bind("$node") Node thisNode,
//        @Cached OperatorNodes.CompareUninlinedNode compareNode,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      return valuesLibrary.getKeyArray(mapObject);
//    }
//  }
//
//  @NodeInfo(shortName = "Map.Get")
//  @GenerateUncached
//  @GenerateInline
//  @ImportStatic(PropertyType.class)
//  public abstract static class MapGetNode extends Node {
//
//    public abstract Object execute(Node node, MapObject mapObject, Object key);
//
//    @Specialization(
//        limit = "3",
//        guards = "isInt(valuesLibrary.getPropertyFlagsOrDefault(mapObject, key, 5))")
//    static int getInt(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary) {
//      try {
//        return valuesLibrary.getIntOrDefault(mapObject, key, -1);
//      } catch (UnexpectedResultException e) {
//        throw new RawTruffleInternalErrorException("Unexpected result", e);
//      }
//    }
//
//    @Specialization(
//        limit = "3",
//        guards = "isLong(valuesLibrary.getPropertyFlagsOrDefault(mapObject, key, 5))")
//    static long getLong(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      try {
//        return valuesLibrary.getLongOrDefault(mapObject, key, -1);
//      } catch (UnexpectedResultException e) {
//        throw new RawTruffleInternalErrorException("Unexpected result", e);
//      }
//    }
//
//    @Specialization(
//        limit = "3",
//        guards = "isDouble(valuesLibrary.getPropertyFlagsOrDefault(mapObject, key, 5))")
//    static double getDouble(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      try {
//        return valuesLibrary.getDoubleOrDefault(mapObject, key, -1);
//      } catch (UnexpectedResultException e) {
//        throw new RawTruffleInternalErrorException("Unexpected result", e);
//      }
//    }
//
//    @Specialization(limit = "3")
//    static Object getObject(
//        Node node,
//        MapObject mapObject,
//        Object key,
//        @CachedLibrary("mapObject") DynamicObjectLibrary valuesLibrary,
//        @Bind("$node") Node thisNode) {
//      return valuesLibrary.getOrDefault(mapObject, key, null);
//    }
//  }
//}
