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
import com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser.TruffleXmlParser;
import com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser.TruffleXmlParserSettings;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleStringCharStream;

public class XmlParseComputeNext {
  private final String text;
  protected final RootCallTarget parseNextRootCallTarget;
  private final TruffleXmlParserSettings settings;
  private TruffleXmlParser parser;

  private TruffleStringCharStream stream;

  public XmlParseComputeNext(
      String text, RootCallTarget parseNextRootCallTarget, TruffleXmlParserSettings settings) {
    this.text = text;
    this.settings = settings;
    this.parseNextRootCallTarget = parseNextRootCallTarget;
  }

  public void setParser(TruffleXmlParser parser) {
    this.parser = parser;
  }

  public void setStream(TruffleStringCharStream stream) {
    this.stream = stream;
  }

  public RootCallTarget getParseNextRootCallTarget() {
    return parseNextRootCallTarget;
  }

  public TruffleXmlParser getParser() {
    return parser;
  }

  public TruffleStringCharStream getStream() {
    return stream;
  }

  public String getText() {
    return text;
  }

  public TruffleXmlParserSettings getSettings() {
    return settings;
  }
}
