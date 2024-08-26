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

package com.rawlabs.compiler.writers

import org.graalvm.polyglot.Value

import java.io.{IOException, OutputStream}
import java.nio.charset.Charset

final class PolyglotTextWriter(os: OutputStream) {

  def writeAndFlush(v: Value): Unit = {
    if (v.isException) {
      v.throwException()
    } else if (v.isNull) {} else if (v.isString) {
      val s = v.asString()
      os.write(s.getBytes(Charset.forName("UTF-8")))
      os.flush()
    } else {
      throw new IOException("unsupported type")
    }
  }

}
