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

package raw.runtime.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.ErrorObject;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.utils.TruffleInputStream;
import raw.sources.api.SourceContext;

@NodeInfo(shortName = "String.Read")
@NodeChild("location")
@NodeChild("encoding")
public abstract class StringReadNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected Object doExecute(LocationObject locationObject, String encoding) {
    SourceContext context = RawContext.get(this).getSourceContext();
    TruffleInputStream stream = new TruffleInputStream(locationObject, context);
    try {
      Reader reader = stream.getReader(encoding);
      try {
        return IOUtils.toString(reader);
      } catch (IOException ex) {
        return new ErrorObject(ex.getMessage());
      } finally {
        IOUtils.closeQuietly(reader);
      }
    } catch (RawTruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    }
  }
}
