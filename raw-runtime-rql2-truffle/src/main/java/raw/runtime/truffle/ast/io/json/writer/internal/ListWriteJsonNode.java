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

package raw.runtime.truffle.ast.io.json.writer.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.StatementNode;
import raw.runtime.truffle.ast.ProgramStatementNode;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodes;
import raw.runtime.truffle.ast.io.json.writer.JsonWriteNodesFactory;
import raw.runtime.truffle.runtime.list.ListLibrary;

@NodeInfo(shortName = "ListWriteJson")
public class ListWriteJsonNode extends StatementNode {

    @Child
    private DirectCallNode childDirectCall;

    @Child
    private ListLibrary listLibrary = ListLibrary.getFactory().createDispatched(1);

    @Child
    private JsonWriteNodes.WriteStartArrayJsonWriterNode writeStartArrayNode = JsonWriteNodesFactory.WriteStartArrayJsonWriterNodeGen.create();

    @Child
    private JsonWriteNodes.WriteEndArrayJsonWriterNode writeEndArrayNode = JsonWriteNodesFactory.WriteEndArrayJsonWriterNodeGen.create();

    public ListWriteJsonNode(ProgramStatementNode childProgramStatementNode) {
        this.childDirectCall = DirectCallNode.create(childProgramStatementNode.getCallTarget());
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        int listSize = (int) listLibrary.size(args[0]);
        JsonGenerator gen = (JsonGenerator) args[1];
        writeStartArrayNode.execute(gen);
        for (int i = 0; i < listSize; i++) {
            childDirectCall.call(listLibrary.get(args[0], i), gen);
        }
        writeEndArrayNode.execute(gen);
    }

}
