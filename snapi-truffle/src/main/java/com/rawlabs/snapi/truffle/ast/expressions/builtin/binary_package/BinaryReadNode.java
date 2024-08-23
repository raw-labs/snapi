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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.binary_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;
import com.rawlabs.snapi.truffle.runtime.generator.collection.StaticInitializers;
import com.rawlabs.snapi.truffle.runtime.primitives.BinaryObject;
import com.rawlabs.snapi.truffle.runtime.primitives.ErrorObject;
import com.rawlabs.snapi.truffle.runtime.primitives.LocationObject;
import com.rawlabs.snapi.truffle.runtime.utils.TruffleInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

@NodeInfo(shortName = "Binary.Read")
@NodeChild(value = "binary")
@ImportStatic(StaticInitializers.class)
public abstract class BinaryReadNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected Object doExecute(LocationObject locationObject) {
    InputStream stream = null;
    try {
      stream = (new TruffleInputStream(locationObject)).getInputStream();
      return new BinaryObject(stream.readAllBytes());
    } catch (IOException | TruffleRuntimeException ex) {
      return new ErrorObject(ex.getMessage());
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }
}
