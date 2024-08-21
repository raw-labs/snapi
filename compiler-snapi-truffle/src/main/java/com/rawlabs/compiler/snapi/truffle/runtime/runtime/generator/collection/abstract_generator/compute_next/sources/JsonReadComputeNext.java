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

package com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.RootCallTarget;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.LocationObject;
import com.rawlabs.compiler.snapi.truffle.runtime.utils.TruffleCharInputStream;

public class JsonReadComputeNext {
  private final LocationObject locationObject;
  private final RootCallTarget parseNextCallTarget;
  private final String encoding;
  private TruffleCharInputStream stream;
  private JsonParser parser;

  public JsonReadComputeNext(
      LocationObject locationObject, String encoding, RootCallTarget parseNextRootCallTarget) {
    this.encoding = encoding;
    this.locationObject = locationObject;
    this.parseNextCallTarget = parseNextRootCallTarget;
  }

  public void setStream(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public void setParser(JsonParser parser) {
    this.parser = parser;
  }

  public RootCallTarget getParseNextCallTarget() {
    return parseNextCallTarget;
  }

  public JsonParser getParser() {
    return parser;
  }

  public TruffleCharInputStream getStream() {
    return stream;
  }

  public LocationObject getLocationObject() {
    return locationObject;
  }

  public String getEncoding() {
    return encoding;
  }
}
