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

package raw.client.writers

import org.graalvm.polyglot.Value

import java.io.{BufferedOutputStream, IOException, OutputStream}

class PolyglotBinaryWriter(os: OutputStream) {

  def writeValue(v: Value): Unit = {
    if (v.isException) {
      v.throwException()
    } else if (v.isNull) {} else if (v.hasBufferElements) {
      // TODO (msb): I suspect this is still terribly slow but not sure what the correct polyglot interface is.
      val bufferedOutputStream = new BufferedOutputStream(os)
      for (i <- 0L until v.getBufferSize) {
        bufferedOutputStream.write(v.readBufferByte(i))
      }
      bufferedOutputStream.flush()
    } else {
      throw new IOException("unsupported type")
    }
  }

}
