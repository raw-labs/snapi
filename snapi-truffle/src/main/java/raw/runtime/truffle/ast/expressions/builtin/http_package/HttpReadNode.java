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

package raw.runtime.truffle.ast.expressions.builtin.http_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.generator.collection.StaticInitializers;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.BinaryObject;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.record.RecordNodes;
import raw.runtime.truffle.tryable_nullable.TryableNullableNodes;
import com.rawlabs.utils.sources.api.LocationException;
import com.rawlabs.utils.sources.bytestream.http.HttpByteStreamLocation;
import com.rawlabs.utils.sources.bytestream.http.HttpResult;
import scala.Tuple2;
import scala.collection.IndexedSeq;

@NodeInfo(shortName = "Http.Read")
@NodeChild(value = "locationObject")
@NodeChild(value = "statusList")
@ImportStatic(StaticInitializers.class)
public abstract class HttpReadNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected static Object doRead(
      LocationObject locationObject,
      Object statusListOption,
      @Bind("$node") Node thisNode,
      @Cached(inline = true) TryableNullableNodes.IsNullNode isNullNode,
      @Cached(inline = true) ListNodes.SizeNode sizeNode,
      @Cached(inline = true) ListNodes.GetNode getNode,
      @Cached(inline = true) RecordNodes.AddPropNode addPropNode) {
    try {
      HttpByteStreamLocation location = locationObject.getHttpByteStreamLocation();
      HttpResult result = location.getHttpResult();
      Object record = RawLanguage.get(thisNode).createPureRecord();

      if (!isNullNode.execute(thisNode, statusListOption)) {
        int[] statuses = new int[(int) sizeNode.execute(thisNode, statusListOption)];
        for (int i = 0; i < statuses.length; i++) {
          statuses[i] = (int) getNode.execute(thisNode, statusListOption, i);
        }
        if (Arrays.stream(statuses).noneMatch(status -> status == result.status())) {
          return new ErrorObject(
              String.format(
                  "HTTP %s failed, got %d, expected %s",
                  location.method().toUpperCase(),
                  result.status(),
                  String.join(
                      ",",
                      Arrays.stream(statuses).mapToObj(Integer::toString).toArray(String[]::new))));
        }
      }

      addPropNode.execute(thisNode, record, "status", result.status(), false);

      try (InputStream is = result.is()) {
        addPropNode.execute(thisNode, record, "data", new BinaryObject(is.readAllBytes()), false);
      }

      IndexedSeq<Tuple2<String, String>> headerTuples = result.headers().toIndexedSeq();
      Object[] headers = new Object[result.headers().size()];

      for (int i = 0; i < result.headers().size(); i++) {
        headers[i] = RawLanguage.get(thisNode).createPureRecord();
        addPropNode.execute(thisNode, headers[i], "_1", headerTuples.apply(i)._1(), false);
        addPropNode.execute(thisNode, headers[i], "_2", headerTuples.apply(i)._2(), false);
      }

      ObjectList headersResult = new ObjectList(headers);
      addPropNode.execute(thisNode, record, "headers", headersResult, false);

      return record;
    } catch (LocationException | IOException e) {
      return new ErrorObject(e.getMessage());
    }
  }
}
