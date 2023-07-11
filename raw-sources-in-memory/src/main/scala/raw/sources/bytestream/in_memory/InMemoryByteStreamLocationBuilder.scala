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

package raw.sources.bytestream.in_memory

import raw.sources.bytestream.{ByteStreamLocation, ByteStreamLocationBuilder}
import raw.sources.{LocationDescription, LocationException, SourceContext}

class InMemoryByteStreamLocationBuilder extends ByteStreamLocationBuilder {

  override def schemes: Seq[String] = Seq(InMemoryByteStreamLocation.schema)
  override def build(location: LocationDescription)(implicit sourceContext: SourceContext): ByteStreamLocation = {
    if (location.url.startsWith(schemes.head)) {
      new InMemoryByteStreamLocation(location)
    } else {
      throw new LocationException(s"Not an in-memory location: ${location.url}")
    }

  }

}
