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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.location_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.frontend.snapi.extensions.LocationDescription$;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.list.ObjectList;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.utils.core.RawException;
import com.rawlabs.utils.sources.filesystem.api.FileSystemLocation;
import scala.collection.IndexedSeq;

@NodeInfo(shortName = "Location.Ls")
@NodeChild("location")
public abstract class LocationLsNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected Object doLs(LocationObject locationObject) {
    try {
      FileSystemLocation fs = locationObject.getFileSystemLocation();
      IndexedSeq<LocationObject> values =
          fs.ls()
              .map(l -> new LocationObject(l, LocationDescription$.MODULE$.locationToPublicUrl(l)))
              .toIndexedSeq();
      int size = values.size();
      Object[] result = new LocationObject[size];

      for (int i = 0; i < size; i++) {
        result[i] = values.apply(i);
      }

      return new ObjectList(result);
    } catch (RawException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
