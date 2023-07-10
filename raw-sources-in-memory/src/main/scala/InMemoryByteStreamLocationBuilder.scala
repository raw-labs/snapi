package raw.sources.bytestream.in_memory

import raw.sources.{LocationDescription, LocationException, SourceContext}
import raw.sources.bytestream.{ByteStreamLocation, ByteStreamLocationBuilder}

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
