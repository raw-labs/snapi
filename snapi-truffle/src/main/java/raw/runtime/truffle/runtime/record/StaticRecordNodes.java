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

package raw.runtime.truffle.runtime.record;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.RawLanguage;

public class StaticRecordNodes {

  @NodeInfo(shortName = "StaticRecord.AddProp")
  @GenerateUncached
  @GenerateInline
  public abstract static class AddPropNode extends Node {

    public abstract Object execute(Node node, StaticObjectRecord record, String key, Object value);

    @Specialization
    static Object exec(
        Node node,
        StaticObjectRecord record,
        String key,
        Object value,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) RecordNodes.AddPropNode addPropNode) {
      Object newRecord = RawLanguage.get(thisNode).createPureRecord();
      String[] keys = record.__shapeRef__.getKeys();
      for (int i = 0; i < keys.length; i++) {
        newRecord =
            addPropNode.execute(
                thisNode, newRecord, keys[i], record.__shapeRef__.getFields()[i].getObject(record));
      }

      return addPropNode.execute(thisNode, newRecord, key, value);
    }
  }

  @NodeInfo(shortName = "StaticRecord.RemoveProp")
  @GenerateUncached
  @GenerateInline
  public abstract static class RemovePropNode extends Node {

    public abstract Object execute(Node node, StaticObjectRecord record, String key);

    @Specialization
    static Object exec(
        Node node,
        StaticObjectRecord record,
        String key,
        @Bind("$node") Node thisNode,
        @Cached(inline = false) RecordNodes.AddPropNode addPropNode) {
      Object newRecord = RawLanguage.get(thisNode).createPureRecord();
      String[] keys = record.__shapeRef__.getKeys();
      for (int i = 0; i < keys.length; i++) {
        if (!record.__shapeRef__.getDistinctKeys()[i].equals(key)) {
          newRecord =
              addPropNode.execute(
                  thisNode,
                  newRecord,
                  keys[i],
                  record.__shapeRef__.getFields()[i].getObject(record));
        }
      }

      return newRecord;
    }
  }
}
