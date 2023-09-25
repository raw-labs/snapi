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

import raw.utils.RawTestSuite
import raw.sources.api._

class TestInMemoryLocation extends RawTestSuite {

  test("in memory new location") { _ =>
    val settings = Map[LocationSettingKey, LocationSettingValue](
      (
        LocationSettingKey(InMemoryByteStreamLocation.codeDataKey),
        LocationBinarySetting("hello world".getBytes(UTF_8().rawEncoding))
      )
    )
    val locationDescription = LocationDescription(InMemoryByteStreamLocation.schemaWithColon, settings)
    val inMemoryLocation = new InMemoryByteStreamLocation(locationDescription)
    assert(inMemoryLocation.getInputStream.readAllBytes().sameElements("hello world".getBytes(UTF_8().rawEncoding)))
    assert(inMemoryLocation.rawUri.startsWith("in-memory"))
  }

  test("in memory location errors") { _ =>
    val settings = Map[LocationSettingKey, LocationSettingValue](
      (LocationSettingKey(InMemoryByteStreamLocation.codeDataKey), LocationStringSetting("hello world"))
    )
    val locationDescription = LocationDescription(InMemoryByteStreamLocation.schemaWithColon, settings)
    val inMemoryLocation = new InMemoryByteStreamLocation(locationDescription)
    assertThrows[MatchError](inMemoryLocation.getSeekableInputStream)
    assertThrows[MatchError](inMemoryLocation.getInputStream)
    assertThrows[AssertionError](inMemoryLocation.getLocalPath())
  }

}
