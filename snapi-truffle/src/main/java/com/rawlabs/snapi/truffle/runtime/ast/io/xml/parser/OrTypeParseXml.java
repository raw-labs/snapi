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

package com.rawlabs.snapi.truffle.runtime.ast.io.xml.parser;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.xml.XmlOrTypeParserException;
import com.rawlabs.snapi.truffle.runtime.runtime.exceptions.xml.XmlParserTruffleException;
import com.rawlabs.snapi.truffle.runtime.runtime.or.OrObject;
import java.util.ArrayList;

@NodeInfo(shortName = "OrTypeParseXml")
public class OrTypeParseXml extends ExpressionNode {

  @Children private DirectCallNode[] options;

  public OrTypeParseXml(ProgramExpressionNode[] options) {
    this.options = new DirectCallNode[options.length];
    for (int i = 0; i < options.length; i++) {
      this.options[i] = DirectCallNode.create(options[i].getCallTarget());
    }
  }

  @ExplodeLoop
  public OrObject executeGeneric(VirtualFrame frame) {
    Object[] args = frame.getArguments();
    TruffleXmlParser parser = (TruffleXmlParser) args[0];
    String text = parser.elementAsString();
    ArrayList<String> parseErrors = new ArrayList<>();
    for (int i = 0; i < options.length; i++) {
      DirectCallNode option = options[i];
      TruffleXmlParser optionParser = parser.duplicateFor(text);
      try {
        optionParser.nextToken();
        optionParser.assertCurrentTokenIsStartTag();
        Object value = option.call(optionParser, text);
        optionParser.close();
        parser.expectEndTag(null);
        parser.nextToken(); // skip end tag
        return new OrObject(i, value);
      } catch (XmlParserTruffleException e) {
        String error = e.getMessage();
        parseErrors.add(error);
        optionParser.close();
      }
    }
    throw new XmlOrTypeParserException(parseErrors, parser, this);
  }
}
