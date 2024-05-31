/*
 * Copyright 2024 RAW Labs S.A.
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

    public static RawLanguage getRawLanguage(Node node) {
      return RawLanguage.get(node);
    }

    @Specialization(guards = {"shaper != null", "!shaper.forList()"})
    static Object makeRowCollection(
        Node node,
        RecordShaper shaper,
        Object key,
        Object[] values,
        @Bind("$node") Node thisNode,
        @Cached(value = "getRawLanguage(thisNode)", allowUncached = true) RawLanguage language,
        @Cached @Cached.Exclusive RecordNodes.AddPropNode addPropNode1,
        @Cached @Cached.Exclusive RecordNodes.AddPropNode addPropNode2) {
      Object record = language.createPureRecord();
      addPropNode1.execute(thisNode, record, "key", key, false);
      addPropNode2.execute(thisNode, record, "group", new ObjectList(values).toIterable(), false);

      return record;
    }

    @Specialization(guards = {"shaper != null", "shaper.forList()"})
    static Object makeRowList(
        Node node,
        RecordShaper shaper,
        Object key,
        Object[] values,
        @Bind("$node") Node thisNode,
        @Cached(value = "getRawLanguage(thisNode)", allowUncached = true) RawLanguage language,
        @Cached @Cached.Exclusive RecordNodes.AddPropNode addPropNode1,
        @Cached @Cached.Exclusive RecordNodes.AddPropNode addPropNode2) {
      Object record = language.createPureRecord();
      addPropNode1.execute(node, record, "key", key, false);
      addPropNode2.execute(node, record, "group", new ObjectList(values), false);
      return record;
    }

    @Specialization(guards = "shaper == null")
    static Object makeRowEquiJoin(Node node, Object shaper, Object key, Object[] values) {
      return new Object[] {key, values};
    }
  }
}
