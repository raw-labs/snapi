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

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InvalidArrayIndexException;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.record.RecordObject;

@NodeInfo(shortName = "Record.Project")
@NodeChild("receiverNode")
@NodeChild("indexNode")
public abstract class RecordProjNode extends ExpressionNode {

  @Specialization(limit = "3")
  protected Object readMember(RecordObject record, String key) {
    try {
      return record.readByKey(key);
    } catch (UnknownIdentifierException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }

  @Specialization
  protected Object readMember(RecordObject record, int index) {
    try {
      return record.readIdx(index - 1);
    } catch (InvalidArrayIndexException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }
}
