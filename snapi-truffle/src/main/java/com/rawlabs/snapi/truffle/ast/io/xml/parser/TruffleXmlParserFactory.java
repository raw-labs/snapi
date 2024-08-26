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

package com.rawlabs.snapi.truffle.ast.io.xml.parser;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxInputFactory;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import javax.xml.stream.XMLInputFactory;
import org.codehaus.stax2.XMLInputFactory2;

public class TruffleXmlParserFactory extends WstxInputFactory {

  @TruffleBoundary
  public TruffleXmlParserFactory() {
    super();
    setProperty(
        WstxInputProperties.P_INPUT_PARSING_MODE, WstxInputProperties.PARSING_MODE_DOCUMENTS);
    setProperty(XMLInputFactory2.P_LAZY_PARSING, true);
    setProperty(XMLInputFactory.SUPPORT_DTD, false);
  }

  private static TruffleXmlParserFactory singleton = null;

  public static TruffleXmlParserFactory singleton() {
    if (singleton == null) {
      singleton = new TruffleXmlParserFactory();
    }
    return singleton;
  }
}
