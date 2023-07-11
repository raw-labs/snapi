package raw.sources.bytestream.in_memory

import raw.RawTestSuite
import raw.sources._

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
    assertThrows[AssertionError](inMemoryLocation.getSeekableInputStream)
    assertThrows[AssertionError](inMemoryLocation.getLocalPath())
    assertThrows[AssertionError](inMemoryLocation.getInputStream)
  }

}
