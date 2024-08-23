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

package com.rawlabs.snapi.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.exceptions.TruffleRuntimeException;

@NodeInfo(shortName = "String.SubString")
@NodeChild(value = "string")
@NodeChild(value = "begin")
@NodeChild(value = "length")
public abstract class StringSubStringNode extends ExpressionNode {

  @Specialization
  @TruffleBoundary
  protected String stringSubstring(String string, int begin, int length) {
    if (begin <= 0) {
      throw new TruffleRuntimeException("invalid index: indexes start at 1", this);
    }

    int end;
    if (length >= 0 && begin - 1 + length < string.length()) {
      end = begin - 1 + length;
    } else {
      end = string.length();
    }
    return string.substring(begin - 1, end);
  }
}
