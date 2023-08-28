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
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.iterable.sources.XmlParseCollection;

@NodeInfo(shortName = "XmlParseCollection")
public class XmlParseCollectionNode extends ExpressionNode {

    @Child private ExpressionNode stringExp;
    @Child private ExpressionNode dateFormatExp;
    @Child private ExpressionNode timeFormatExp;
    @Child private ExpressionNode datetimeFormatExp;

    @Child private DirectCallNode childDirectCall;

    public XmlParseCollectionNode(
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
        String text = (String) stringExp.executeGeneric(virtualFrame);
        String dateFormat = (String) dateFormatExp.executeGeneric(virtualFrame);
        String timeFormat = (String) timeFormatExp.executeGeneric(virtualFrame);
        String datetimeFormat = (String) datetimeFormatExp.executeGeneric(virtualFrame);
        RawTruffleXmlParserSettings settings =
                new RawTruffleXmlParserSettings(dateFormat, timeFormat, datetimeFormat);
        RuntimeContext context = RawContext.get(this).getRuntimeContext();
        return new XmlParseCollection(text, context, childDirectCall, settings);
    }
}
