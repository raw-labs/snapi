package raw.runtime.truffle.ast.io.json.reader.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParserNodes;

@NodeInfo(shortName = "AnyParseJson")
public abstract class AnyParseJsonNode extends ExpressionNode {


    @Specialization
    protected Object doParse(
            VirtualFrame frame,
            @Cached("create()") JsonParserNodes.ParseAnyJsonParserNode anyParse
    ) {
        Object[] args = frame.getArguments();
        JsonParser parser = (JsonParser) args[0];
        return anyParse.execute(parser);
    }


}
