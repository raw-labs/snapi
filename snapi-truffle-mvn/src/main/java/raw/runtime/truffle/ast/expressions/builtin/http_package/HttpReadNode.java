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
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnknownIdentifierException;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleInternalErrorException;
import raw.runtime.truffle.runtime.list.ListNodes;
import raw.runtime.truffle.runtime.list.ObjectList;
import raw.runtime.truffle.runtime.primitives.BinaryObject;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.record.RecordObject;
import raw.runtime.truffle.tryable_nullable.Nullable;
import raw.sources.api.LocationException;
import raw.sources.api.SourceContext;
import raw.sources.bytestream.http.HttpByteStreamLocation;
import raw.sources.bytestream.http.HttpByteStreamLocationBuilder;
import raw.sources.bytestream.http.HttpResult;
import scala.Tuple2;
import scala.collection.IndexedSeq;

@NodeInfo(shortName = "Http.Read")
@NodeChild(value = "locationObject")
@NodeChild(value = "statusList")
public abstract class HttpReadNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected Object doRead(
      LocationObject locationObject,
      Object statusListOption,
      @Cached(inline = true) ListNodes.SizeNode sizeNode,
      @Cached(inline = true) ListNodes.GetNode getNode,
      @CachedLibrary(limit = "3") InteropLibrary records) {
    try {
      SourceContext context = RawContext.get(this).getSourceContext();
      HttpByteStreamLocationBuilder builder = new HttpByteStreamLocationBuilder();
      HttpByteStreamLocation location =
          builder.build(locationObject.getLocationDescription(), context);
      HttpResult result = location.getHttpResult();
      RecordObject record = RawLanguage.get(this).createRecord();

      if (Nullable.isNotNull(statusListOption)) {
        int[] statuses = new int[(int) sizeNode.execute(this, statusListOption)];
        for (int i = 0; i < statuses.length; i++) {
          statuses[i] = (int) getNode.execute(this, statusListOption, i);
        }
        if (Arrays.stream(statuses).noneMatch(status -> status == result.status())) {
          String method =
              locationObject
                  .getLocationDescription()
                  .getStringSetting("http-method")
                  .getOrElse(() -> "get");
          return new ErrorObject(
              String.format(
                  "HTTP %s failed, got %d, expected %s",
                  method.toUpperCase(),
                  result.status(),
                  String.join(
                      ",",
                      Arrays.stream(statuses).mapToObj(Integer::toString).toArray(String[]::new))));
        }
      }

      records.writeMember(record, "status", result.status());

      try (InputStream is = result.is()) {
        records.writeMember(record, "data", new BinaryObject(is.readAllBytes()));
      }

      IndexedSeq<Tuple2<String, String>> headerTuples = result.headers().toIndexedSeq();
      Object[] headers = new Object[result.headers().size()];

      for (int i = 0; i < result.headers().size(); i++) {
        headers[i] = RawLanguage.get(this).createRecord();
        records.writeMember(headers[i], "_1", headerTuples.apply(i)._1());
        records.writeMember(headers[i], "_2", headerTuples.apply(i)._2());
      }

      ObjectList headersResult = new ObjectList(headers);
      records.writeMember(record, "headers", headersResult);

      return record;
    } catch (LocationException | IOException e) {
      return new ErrorObject(e.getMessage());
    } catch (UnsupportedMessageException
        | UnknownIdentifierException
        | UnsupportedTypeException e) {
      throw new RawTruffleInternalErrorException(e, this);
    }
  }
}
