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

package com.rawlabs.compiler.snapi.truffle.runtime.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.exceptions.RawTruffleRuntimeException;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.generator.collection.StaticInitializers;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.ErrorObject;
import com.rawlabs.compiler.snapi.truffle.runtime.runtime.primitives.LocationObject;
import com.rawlabs.compiler.snapi.truffle.runtime.utils.TruffleInputStream;
import java.io.IOException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;

@NodeInfo(shortName = "String.Read")
@NodeChild("location")
@NodeChild("encoding")
@ImportStatic(StaticInitializers.class)
public abstract class StringReadNode extends ExpressionNode {
  @Specialization
  @TruffleBoundary
  protected static Object doExecute(LocationObject locationObject, String encoding) {
    TruffleInputStream stream = new TruffleInputStream(locationObject);
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
