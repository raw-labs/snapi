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

package raw.runtime.truffle.runtime.generator.collection.off_heap_generator.record_shaper;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.record.RecordNodes;

public class RecordShaperNodes {
  @NodeInfo(shortName = "RecordShaper.MakeRow")
  @GenerateUncached
  @GenerateInline
  public abstract static class MakeRowNode extends Node {

    public abstract Object execute(Node node, Object shaper, Object key, Object[] values);

    @Specialization(guards = {"shaper != null", "!shaper.forList()"})
    static Object makeRowCollection(
        Node node,
        RecordShaper shaper,
        Object key,
        Object[] values,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("addProp") RecordNodes.AddPropNode addPropNode) {
      Object record = RawLanguage.get(thisNode).createPureRecord();
      record = addPropNode.execute(thisNode, record, "key", key);
      record = addPropNode.execute(thisNode, record, "group", new ObjectList(values).toIterable());

      return record;
    }

    @Specialization(guards = {"shaper != null", "shaper.forList()"})
    static Object makeRowList(
        Node node,
        RecordShaper shaper,
        Object key,
        Object[] values,
        @Bind("$node") Node thisNode,
        @Cached @Cached.Shared("addProp") RecordNodes.AddPropNode addPropNode) {
      Object record =  RawLanguage.get(thisNode).createPureRecord();
      record = addPropNode.execute(node, record, "key", key);
      record = addPropNode.execute(node, record, "group", new ObjectList(values));
      return record;
    }

    @Specialization(guards = "shaper == null")
    static Object makeRowEquiJoin(Node node, Object shaper, Object key, Object[] values) {
      return new Object[] {key, values};
    }
  }
}
