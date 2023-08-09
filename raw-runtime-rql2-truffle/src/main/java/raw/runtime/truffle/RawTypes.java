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

package raw.runtime.truffle;

import com.oracle.truffle.api.dsl.TypeSystem;
import raw.runtime.truffle.runtime.primitives.*;
import raw.runtime.truffle.runtime.record.RecordObject;

import java.math.BigDecimal;

@TypeSystem({
  boolean.class,
  byte.class,
  short.class,
  int.class,
  long.class,
  float.class,
  double.class,
  byte[].class,
  BigDecimal.class,
  DateObject.class,
  TimeObject.class,
  IntervalObject.class,
  TimestampObject.class,
  LocationObject.class,
  String.class,
  RecordObject.class
})
public abstract class RawTypes {}
