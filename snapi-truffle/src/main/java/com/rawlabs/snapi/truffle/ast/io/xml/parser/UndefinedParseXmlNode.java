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

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.xml.XmlExpectedNothingException;

@NodeInfo(shortName = "UndefinedParseXml")
public abstract class UndefinedParseXmlNode extends ExpressionNode {

  @Specialization
  public byte parse(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    TruffleXmlParser parser = (TruffleXmlParser) args[0];
    String value = (String) args[1];
    throw new XmlExpectedNothingException(value, parser, this);
  }
}
