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

package raw.runtime.truffle.ast.expressions.builtin.binary_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.primitives.LocationObject;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;
import raw.runtime.truffle.runtime.tryable.StringTryable;
import raw.runtime.truffle.utils.TruffleInputStream;

@NodeInfo(shortName = "Binary.Read")
@NodeChild(value = "binary")
public abstract class BinaryReadNode extends ExpressionNode {

  @Specialization
  protected Object doExecute(LocationObject locationObject) {
    RuntimeContext context = RawContext.get(this).getRuntimeContext();
    InputStream stream = null;
    try {
      stream = (new TruffleInputStream(locationObject, context)).getInputStream();
      byte[] bytes = stream.readAllBytes();
      return ObjectTryable.BuildSuccess(bytes);
    } catch (IOException | RawTruffleRuntimeException ex) {
      return StringTryable.BuildFailure(ex.getMessage());
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }
}
