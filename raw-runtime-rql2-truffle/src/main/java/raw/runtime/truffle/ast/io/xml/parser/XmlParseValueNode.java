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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.utils.RawTruffleStringCharStream;

@NodeInfo(shortName = "XmlParseValue")
public class XmlParseValueNode extends ExpressionNode {

    @Child private ExpressionNode stringExp;

    @Child private DirectCallNode childDirectCall;
    @Child private ExpressionNode dateFormatExp;
    @Child private ExpressionNode timeFormatExp;
    @Child private ExpressionNode datetimeFormatExp;

    private RawTruffleXmlParser parser;

    public XmlParseValueNode(
            ExpressionNode stringExp,
            ExpressionNode dateFormatExp,
            ExpressionNode timeFormatExp,
            ExpressionNode datetimeFormatExp,
            RootNode readerNode) {
        this.stringExp = stringExp;
        this.dateFormatExp = dateFormatExp;
        this.timeFormatExp = timeFormatExp;
        this.datetimeFormatExp = datetimeFormatExp;
        this.childDirectCall = DirectCallNode.create(readerNode.getCallTarget());
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        try {
            String string = (String) stringExp.executeGeneric(virtualFrame);
            RawTruffleStringCharStream stream = new RawTruffleStringCharStream(string);

            String dateFormat = (String) dateFormatExp.executeGeneric(virtualFrame);
            String timeFormat = (String) timeFormatExp.executeGeneric(virtualFrame);
            String datetimeFormat = (String) datetimeFormatExp.executeGeneric(virtualFrame);
            RawTruffleXmlParserSettings settings =
                    new RawTruffleXmlParserSettings(dateFormat, timeFormat, datetimeFormat);

            parser = RawTruffleXmlParser.create(stream, settings);
            parser.nextToken(); // consume START_OBJECT
            parser.assertCurrentTokenIsStartTag(); // because it's the top level object
            return this.childDirectCall.call(parser); // ... and we start to parse it.
        } finally {
            if (parser != null) parser.close();
            parser = null;
        }
    }
}
