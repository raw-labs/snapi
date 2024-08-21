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

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.ast.ProgramExpressionNode;
import com.rawlabs.snapi.truffle.runtime.runtime.iterable.sources.XmlReadCollection;
import com.rawlabs.snapi.truffle.runtime.runtime.primitives.LocationObject;

@NodeInfo(shortName = "XmlReadCollection")
public class XmlReadCollectionNode extends ExpressionNode {

  @Child private ExpressionNode locationExp;
  @Child private ExpressionNode encodingExp;
  @Child private ExpressionNode dateFormatExp;
  @Child private ExpressionNode timeFormatExp;
  @Child private ExpressionNode datetimeFormatExp;
  private final RootCallTarget parseRootCallTarget;

  public XmlReadCollectionNode(
      ExpressionNode locationExp,
      ExpressionNode encodingExp,
      ExpressionNode dateFormatExp,
      ExpressionNode timeFormatExp,
      ExpressionNode datetimeFormatExp,
      ProgramExpressionNode readerNode) {
    this.locationExp = locationExp;
    this.encodingExp = encodingExp;
    this.dateFormatExp = dateFormatExp;
    this.timeFormatExp = timeFormatExp;
    this.datetimeFormatExp = datetimeFormatExp;
    this.parseRootCallTarget = readerNode.getCallTarget();
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    LocationObject locationObject = (LocationObject) locationExp.executeGeneric(virtualFrame);
    String encoding = (String) encodingExp.executeGeneric(virtualFrame);
    String dateFormat = (String) dateFormatExp.executeGeneric(virtualFrame);
    String timeFormat = (String) timeFormatExp.executeGeneric(virtualFrame);
    String datetimeFormat = (String) datetimeFormatExp.executeGeneric(virtualFrame);
    RawTruffleXmlParserSettings settings =
        new RawTruffleXmlParserSettings(dateFormat, timeFormat, datetimeFormat);
    return new XmlReadCollection(locationObject, encoding, parseRootCallTarget, settings);
  }
}
