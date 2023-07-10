package raw.sources.bytestream.in_memory

import raw.sources.bytestream.{ByteStreamLocation, SeekableInputStream}
import raw.sources._

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.file.Path

object InMemoryByteStreamLocation {
  val schema = "in-memory://"
  val codeDataKey = "code-data"
}

class InMemoryByteStreamLocation(
    locationDescription: LocationDescription
) extends ByteStreamLocation {

  override protected def doGetInputStream(): InputStream = {
    assert(locationDescription.settings.contains(LocationSettingKey(InMemoryByteStreamLocation.codeDataKey)))
    val codeData = locationDescription
      .settings(LocationSettingKey(InMemoryByteStreamLocation.codeDataKey))

    codeData match {
      case LocationBinarySetting(v) => new ByteArrayInputStream(v.toArray)
      case _ => throw new AssertionError(s"${InMemoryByteStreamLocation.codeDataKey} must be a byte array")
    }
  }

  override protected def doGetSeekableInputStream(): SeekableInputStream =
    throw new AssertionError("Calling SeekableInputStream on in memory location")

  override def getLocalPath(): Path = throw new AssertionError("Calling path on in memory location")

  override def cacheStrategy: CacheStrategy = CacheStrategy.NoCache

  override def retryStrategy: RetryStrategy = NoRetry()

  override def rawUri: String = "in-memory:"

  override def testAccess(): Unit = {}
}
