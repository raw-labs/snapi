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

import raw.client.utils.RawTestSuite
import raw.sources.api._
import raw.client.api._

class TestInMemoryLocationBuild extends RawTestSuite {

  test("in memory location builder") { _ =>
    val settings = Map[LocationSettingKey, LocationSettingValue](
      (
        LocationSettingKey(InMemoryByteStreamLocation.codeDataKey),
        LocationBinarySetting("hello world".getBytes(UTF_8().rawEncoding))
      )
    )
    val locationDescription = LocationDescription(InMemoryByteStreamLocation.schemaWithColon, settings)
    implicit val sourceContext: SourceContext = null
    val inMemoryLocationBuilt = new InMemoryByteStreamLocationBuilder().build(locationDescription)
    assert(
      inMemoryLocationBuilt.getInputStream.readAllBytes().sameElements("hello world".getBytes(UTF_8().rawEncoding))
    )
    assert(inMemoryLocationBuilt.rawUri.startsWith("in-memory"))
  }

  test("in memory location builder scheme") { _ =>
    val inMemoryLocationBuilder = new InMemoryByteStreamLocationBuilder()
    assert(inMemoryLocationBuilder.schemes.size == 1)
    assert(inMemoryLocationBuilder.schemes.head == InMemoryByteStreamLocation.schema)
  }

  test("wrong schema build error") { _ =>
    val settings = Map[LocationSettingKey, LocationSettingValue](
      (
        LocationSettingKey(InMemoryByteStreamLocation.codeDataKey),
        LocationBinarySetting("hello world".getBytes(UTF_8().rawEncoding))
      )
    )
    val locationDescription = LocationDescription("not-in-memory", settings)
    implicit val sourceContext: SourceContext = null

    assertThrows[LocationException](new InMemoryByteStreamLocationBuilder().build(locationDescription))
  }

}
