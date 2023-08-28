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

package raw.runtime.truffle.ast;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.StatementNode;

public final class ProgramStatementNode extends RootNode {

    //    private static final Source DUMMY_SOURCE = Source.newBuilder(RawLanguage.ID, "",
    // "dummy").build();

    @Child protected StatementNode bodyNode;

    public ProgramStatementNode(
            RawLanguage language, FrameDescriptor frameDescriptor, StatementNode body) {
        super(language, frameDescriptor);
        this.bodyNode = body;
        this.bodyNode.addRootTag();
    }

    @Override
    public Object execute(VirtualFrame frame) {
        bodyNode.executeVoid(frame);
        return null;
    }

    //    @Override
    //    public SourceSection getSourceSection() {
    //        return DUMMY_SOURCE.createUnavailableSection();
    //    }
}
