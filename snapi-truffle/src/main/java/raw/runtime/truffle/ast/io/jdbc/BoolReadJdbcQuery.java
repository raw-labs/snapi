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

package raw.runtime.truffle.ast.io.jdbc;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;

@NodeInfo(shortName = "Jdbc.BoolRead")
public class BoolReadJdbcQuery extends ExpressionNode {

    private final String colName;

    private final int index;

    public BoolReadJdbcQuery(String colname, int index) {
        this.colName = colname;
        this.index = index;
    }

    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        JdbcQuery rs = (JdbcQuery) args[0];
        return rs.getBool(index, colName, this);
    }
}
