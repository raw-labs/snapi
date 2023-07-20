package raw.runtime.truffle.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodes;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodesFactory;

@NodeInfo(shortName = "BooleanWriteJson")
public class AnyWriteJsonNode extends StatementNode {

    @Child
    JsonWriteNodes.WriteAnyJsonParserNode writeAny = JsonWriteNodesFactory.WriteAnyJsonParserNodeGen.create();

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        writeAny.execute(args[0], (JsonGenerator) args[1]);
    }
}
