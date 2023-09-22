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

package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.object.Shape;

// This is an implementation of `DynamicObject` used as an internal storage of our RecordObject.
class RecordStorageObject extends DynamicObject {

  protected RecordStorageObject(Shape shape) {
    super(shape);
  }
}
