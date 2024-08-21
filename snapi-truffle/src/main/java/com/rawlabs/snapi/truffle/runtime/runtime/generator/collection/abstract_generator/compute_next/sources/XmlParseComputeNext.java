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

package com.rawlabs.snapi.truffle.runtime.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser.RawTruffleXmlParser;
import com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser.RawTruffleXmlParserSettings;
import com.rawlabs.snapi.truffle.runtime.utils.RawTruffleStringCharStream;

public class XmlParseComputeNext {
  private final String text;
  protected final RootCallTarget parseNextRootCallTarget;
  private final RawTruffleXmlParserSettings settings;
  private RawTruffleXmlParser parser;

  private RawTruffleStringCharStream stream;

  public XmlParseComputeNext(
      String text, RootCallTarget parseNextRootCallTarget, RawTruffleXmlParserSettings settings) {
    this.text = text;
    this.settings = settings;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
  }

  public void setParser(RawTruffleXmlParser parser) {
    this.parser = parser;
  }

  public void setStream(RawTruffleStringCharStream stream) {
    this.stream = stream;
  }

  public RootCallTarget getParseNextRootCallTarget() {
    return parseNextRootCallTarget;
  }

  public RawTruffleXmlParser getParser() {
    return parser;
  }

  public RawTruffleStringCharStream getStream() {
    return stream;
  }

  public String getText() {
    return text;
  }

  public RawTruffleXmlParserSettings getSettings() {
    return settings;
  }
}
