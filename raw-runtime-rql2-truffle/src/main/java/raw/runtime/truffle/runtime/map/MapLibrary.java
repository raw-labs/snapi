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

package raw.runtime.truffle.runtime.map;

import com.oracle.truffle.api.library.GenerateLibrary;
import com.oracle.truffle.api.library.Library;
import com.oracle.truffle.api.library.LibraryFactory;

@GenerateLibrary
public abstract class MapLibrary extends Library {

  static final LibraryFactory<MapLibrary> FACTORY = LibraryFactory.resolve(MapLibrary.class);

  public abstract void put(Object receiver, Object key, Object value);

  public abstract Object get(Object receiver, Object key);

  public static LibraryFactory<MapLibrary> getFactory() {
    return FACTORY;
  }

  public static MapLibrary getUncached() {
    return FACTORY.getUncached();
  }
}
