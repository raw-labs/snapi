package raw.sources.bytestream.in_memory

import raw.RawTestSuite
import raw.sources._

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
    assert(inMemoryLocationBuilder.schemes.head == InMemoryByteStreamLocation.schemaWithColon)
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
