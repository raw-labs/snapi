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

package raw.runtime.truffle.ast.expressions.record;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.util.Arrays;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.runtime.record.RecordNodes;

@NodeInfo(shortName = "Record.Fields")
@NodeChild("record")
public abstract class RecordFieldsNode extends ExpressionNode {

  @Specialization
  protected StringList doFields(
      Object record, @Cached(inline = true) RecordNodes.GetKeysNode getKeysNode) {
    Object[] keys = getKeysNode.execute(this, record);
    String[] result = Arrays.stream(keys).map(Object::toString).toArray(String[]::new);
    return new StringList(result);
  }
}
