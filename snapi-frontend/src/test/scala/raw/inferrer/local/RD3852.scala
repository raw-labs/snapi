package raw.inferrer.local

import com.typesafe.scalalogging.StrictLogging
import raw.client.api.LocationDescription
import raw.inferrer.api.{
  AutoInferrerProperties,
  CsvInputFormatDescriptor,
  LinesInputFormatDescriptor,
  TextInputStreamFormatDescriptor
}
import raw.sources.api.SourceContext
import raw.sources.filesystem.local.LocalLocationsTestContext
import raw.utils.{RawTestSuite, RawUtils, SettingsTestContext}
import raw.sources.filesystem.local.LocalPath

class RD3852 extends RawTestSuite with SettingsTestContext with StrictLogging with LocalLocationsTestContext {


  // CSV is the last in the list in the auto inferrer.
  // So inferring this a csv file means that the other inferrers threw  correctly
  // a LocalInferrerException  while trying to parse the file
  test("Auto inferring CSV") { _ =>
    implicit val sourceContext = new SourceContext(null, null, settings, None)
    val inferrer = new LocalInferrerService
    val p = RawUtils.getResource("data/students/students.csv")
    val l1 = new LocalPath(p)
    try {

      val TextInputStreamFormatDescriptor(_, _, format) =
        inferrer.infer(AutoInferrerProperties(LocationDescription(l1.rawUri), None))
      assert(format.isInstanceOf[CsvInputFormatDescriptor])

    } finally {
      RawUtils.withSuppressNonFatalException(inferrer.stop())
    }
  }

}
