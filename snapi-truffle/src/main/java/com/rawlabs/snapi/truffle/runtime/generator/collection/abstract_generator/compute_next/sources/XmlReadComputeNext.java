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

package com.rawlabs.snapi.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.rawlabs.snapi.truffle.ast.io.xml.parser.TruffleXmlParser;
import com.rawlabs.snapi.truffle.ast.io.xml.parser.TruffleXmlParserSettings;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleCharInputStream;

public class XmlReadComputeNext {
  private final LocationObject locationObject;
  protected final RootCallTarget parseNextRootCallTarget;
  private final String encoding;
  private TruffleXmlParser parser;
  private final TruffleXmlParserSettings settings;

  private TruffleCharInputStream stream;

  public XmlReadComputeNext(
      LocationObject locationObject,
      String encoding,
      RootCallTarget parseNextRootCallTarget,
      TruffleXmlParserSettings settings) {
    this.encoding = encoding;
    this.settings = settings;
    this.locationObject = locationObject;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
  }

  public void setParser(TruffleXmlParser parser) {
    this.parser = parser;
  }

  public void setStream(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public LocationObject getLocationObject() {
    return locationObject;
  }

  public RootCallTarget getParseNextRootCallTarget() {
    return parseNextRootCallTarget;
  }

  public String getEncoding() {
    return encoding;
  }

  public TruffleXmlParser getParser() {
    return parser;
  }

  public TruffleXmlParserSettings getSettings() {
    return settings;
  }

  public TruffleCharInputStream getStream() {
    return stream;
  }
}
