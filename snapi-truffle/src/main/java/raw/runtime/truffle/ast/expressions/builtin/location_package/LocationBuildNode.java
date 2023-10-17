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

package raw.runtime.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.time.Duration;
import raw.compiler.rql2.source.Rql2IntType;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.TypeGuards;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ListLibrary;
import raw.runtime.truffle.runtime.option.OptionLibrary;
import raw.runtime.truffle.runtime.primitives.BinaryObject;
import raw.runtime.truffle.runtime.primitives.IntervalObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.client.api.*;
import scala.Tuple2;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;
import scala.collection.immutable.VectorBuilder;

@NodeInfo(shortName = "Location.Build")
public class LocationBuildNode extends ExpressionNode {

  private final String[] keys;

  @Child ListLibrary listLibs = ListLibrary.getFactory().createDispatched(3);
  @Child InteropLibrary interops = InteropLibrary.getFactory().createDispatched(3);
  @Child OptionLibrary options = OptionLibrary.getFactory().createDispatched(3);
  @Child private ExpressionNode url;

  @Children private final ExpressionNode[] values;

  private final Rql2TypeWithProperties[] types;

  public LocationBuildNode(
      ExpressionNode url, String[] keys, ExpressionNode[] values, Rql2TypeWithProperties[] types) {
    assert values.length == keys.length;
    assert values.length == types.length;
    this.url = url;
    this.keys = keys;
    this.values = values;
    this.types = types;
  }

  @Override
  @ExplodeLoop
  public Object executeGeneric(VirtualFrame frame) {
    Map<LocationSettingKey, LocationSettingValue> map = new HashMap<>();
    String url = (String) this.url.executeGeneric(frame);
    for (int i = 0; i < this.keys.length; i++) {
      Object value = this.values[i].executeGeneric(frame);
      map = addToMap(map, this.keys[i], value, i);
    }
    return new LocationObject(url, map);
  }

  @CompilerDirectives.TruffleBoundary
  private Map<LocationSettingKey, LocationSettingValue> addToMap(
      Map<LocationSettingKey, LocationSettingValue> map, String key, Object value, int index) {
    return map.$plus(
        Tuple2.apply(new LocationSettingKey(key), buildLocationSettingValue(value, types[index])));
  }

  @CompilerDirectives.TruffleBoundary
  private LocationSettingValue buildLocationSettingValue(
      Object value, Rql2TypeWithProperties type) {
    try {
      if (TypeGuards.isIntKind(type)) {
        return new LocationIntSetting((Integer) value);
      } else if (TypeGuards.isStringKind(type)) {
        return new LocationStringSetting((String) value);
      } else if (TypeGuards.isByteKind(type)) {
        byte[] bytes = (byte[]) value;
        VectorBuilder<Object> vec = new VectorBuilder<>();
        for (byte aByte : bytes) {
          vec = vec.$plus$eq(aByte);
        }
        return new LocationBinarySetting(vec.result());
      } else if (TypeGuards.isBooleanKind(type)) {
        return new LocationBooleanSetting((Boolean) value);
      } else if (TypeGuards.isIntervalKind(type)) {
        return new LocationDurationSetting(Duration.ofMillis(((IntervalObject) value).toMillis()));
      } else if (TypeGuards.isListKind(type)
          && ((Rql2ListType) type).innerType() instanceof Rql2IntType) {
        ListLibrary lists = ListLibrary.getFactory().create(value);
        int[] ints = (int[]) lists.getInnerList(value);
        return new LocationIntArraySetting(ints);
      } else if (TypeGuards.isListKind(type)) {
        VectorBuilder<Tuple2<String, String>> vec = new VectorBuilder<>();
        int size = (int) listLibs.size(value);
        for (int i = 0; i < size; i++) {
          Object record = listLibs.get(value, i);
          Object keys = interops.getMembers(record);
          Object key = interops.readMember(record, (String) interops.readArrayElement(keys, 0));
          Object val = interops.readMember(record, (String) interops.readArrayElement(keys, 1));
          vec = vec.$plus$eq(Tuple2.apply((String) options.get(key), (String) options.get(val)));
        }
        return new LocationKVSetting(vec.result());
      } else if (TypeGuards.isBinaryKind(type)) {
        VectorBuilder<Object> vec = new VectorBuilder<>();
        for (byte aByte : ((BinaryObject) value).getBytes()) {
          vec = vec.$plus$eq(aByte);
        }
        return new LocationBinarySetting(vec.result());
      } else {
        throw new RawTruffleInternalErrorException();
      }
    } catch (UnsupportedMessageException
        | UnknownIdentifierException
        | InvalidArrayIndexException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }
}
