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

package com.rawlabs.snapi.truffle;

import com.oracle.truffle.api.dsl.TypeSystem;
import com.rawlabs.snapi.truffle.runtime.primitives.*;
import com.rawlabs.snapi.truffle.runtime.record.DuplicateKeyRecord;
import com.rawlabs.snapi.truffle.runtime.record.PureRecord;

@TypeSystem({
  boolean.class,
  byte.class,
  short.class,
  int.class,
  long.class,
  float.class,
  double.class,
  String.class,
  BinaryObject.class,
  DecimalObject.class,
  DateObject.class,
  TimeObject.class,
  IntervalObject.class,
  TimestampObject.class,
  LocationObject.class,
  PureRecord.class,
  DuplicateKeyRecord.class
})
public abstract class Rql2Types {}
