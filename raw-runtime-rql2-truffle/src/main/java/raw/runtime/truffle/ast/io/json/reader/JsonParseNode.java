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

package raw.runtime.truffle.ast.io.json.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.ParserOperations.*;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.exceptions.json.JsonReaderRawTruffleException;

//@ImportStatic(value = TypeGuards.class)
@NodeInfo(shortName = "ParseJson")
//@NodeChild(value = "str")
//@NodeField(name = "resultType", type = Rql2TypeWithProperties.class)
//@NodeField(name = "childRootNode", type = RootNode.class)
public class JsonParseNode extends ExpressionNode {

    @Child
    private ExpressionNode strExp;

    @Child
    private DirectCallNode childDirectCall;

    @Child
    private InitJsonParserNode initParserNode = ParserOperationsFactory.InitJsonParserNodeGen.create();

    @Child
    private CloseJsonParserNode closeParserNode = ParserOperationsFactory.CloseJsonParserNodeGen.create();

    @Child
    private NextTokenJsonParserNode nextTokenNode = ParserOperationsFactory.NextTokenJsonParserNodeGen.create();

    private JsonParser parser;

    public JsonParseNode(ExpressionNode strExp, RootNode readerNode) {
        this.strExp = strExp;
        this.childDirectCall = DirectCallNode.create(readerNode.getCallTarget());
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        try {
            String str = (String) strExp.executeGeneric(virtualFrame);
            parser = initParserNode.execute(str);
            nextTokenNode.execute(parser);
            return childDirectCall.call(parser);
        } catch (RawTruffleRuntimeException e) {
            throw new JsonReaderRawTruffleException();
        } finally {
            closeParserNode.execute(parser);
            parser = null;
        }

    }

}
