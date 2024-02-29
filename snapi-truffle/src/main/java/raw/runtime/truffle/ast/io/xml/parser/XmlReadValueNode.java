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

package raw.runtime.truffle.ast.io.xml.parser;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.xml.XmlParserRawTruffleException;
import raw.runtime.truffle.runtime.exceptions.xml.XmlReaderRawTruffleException;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleCharInputStream;
import raw.runtime.truffle.utils.TruffleInputStream;
import raw.sources.api.SourceContext;

@NodeInfo(shortName = "XmlReadValue")
public class XmlReadValueNode extends ExpressionNode {

  @Child private ExpressionNode locationExp;

  @Child private ExpressionNode encodingExp;
  @Child private ExpressionNode dateFormatExp;
  @Child private ExpressionNode timeFormatExp;
  @Child private ExpressionNode datetimeFormatExp;

  @Child private DirectCallNode childDirectCall;

  public XmlReadValueNode(
      ExpressionNode locationExp,
      ExpressionNode encodingExp,
      ExpressionNode dateFormatExp,
      ExpressionNode timeFormatExp,
      ExpressionNode datetimeFormatExp,
      RootCallTarget readRootCallTarget) {
    this.locationExp = locationExp;
    this.encodingExp = encodingExp;
    this.dateFormatExp = dateFormatExp;
    this.timeFormatExp = timeFormatExp;
    this.datetimeFormatExp = datetimeFormatExp;
    this.childDirectCall = DirectCallNode.create(readRootCallTarget);
  }

  @Override
  public Object executeGeneric(VirtualFrame virtualFrame) {
    RawTruffleXmlParser parser = null;
    try {
      LocationObject locationObject = (LocationObject) locationExp.executeGeneric(virtualFrame);
      String encoding = (String) encodingExp.executeGeneric(virtualFrame);
      SourceContext context = RawContext.get(this).getSourceContext();

      TruffleInputStream truffleInputStream = new TruffleInputStream(locationObject, context);
      TruffleCharInputStream stream = new TruffleCharInputStream(truffleInputStream, encoding);
      String dateFormat = (String) dateFormatExp.executeGeneric(virtualFrame);
      String timeFormat = (String) timeFormatExp.executeGeneric(virtualFrame);
      String datetimeFormat = (String) datetimeFormatExp.executeGeneric(virtualFrame);
      RawTruffleXmlParserSettings settings =
          new RawTruffleXmlParserSettings(dateFormat, timeFormat, datetimeFormat);

      try {
        parser = RawTruffleXmlParser.create(stream, settings);
        parser.nextToken(); // consume START_OBJECT
        parser.assertCurrentTokenIsStartTag(); // because it's the top level object
        return this.childDirectCall.call(parser); // ... and we start to parse it.
      } catch (XmlParserRawTruffleException e) {
        throw new XmlReaderRawTruffleException(e, stream, this);
      }
    } finally {
      if (parser != null) parser.close();
    }
  }
}
