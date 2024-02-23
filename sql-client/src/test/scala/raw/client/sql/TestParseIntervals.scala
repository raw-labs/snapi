package raw.client.sql

import org.scalatest.funsuite.AnyFunSuite
import raw.client.api.RawInterval
import raw.client.sql.SqlTypesUtils.parseInterval
class TestParseIntervals extends AnyFunSuite {

  test("parse singular interval items") {
    Map(
      "1 year" -> RawInterval(1, 0, 0, 0, 0, 0, 0, 0),
      "1 mon" -> RawInterval(0, 1, 0, 0, 0, 0, 0, 0),
      "1 day" -> RawInterval(0, 0, 0, 1, 0, 0, 0, 0),
      "01:00:00" -> RawInterval(0, 0, 0, 0, 1, 0, 0, 0),
      "00:01:00" -> RawInterval(0, 0, 0, 0, 0, 1, 0, 0),
      "00:00:01" -> RawInterval(0, 0, 0, 0, 0, 0, 1, 0),
      "00:00:00.001" -> RawInterval(0, 0, 0, 0, 0, 0, 0, 1),
      "2 years" -> RawInterval(2, 0, 0, 0, 0, 0, 0, 0),
      "2 mons" -> RawInterval(0, 2, 0, 0, 0, 0, 0, 0),
      "2 days" -> RawInterval(0, 0, 0, 2, 0, 0, 0, 0),
      "02:00:00" -> RawInterval(0, 0, 0, 0, 2, 0, 0, 0),
      "00:02:00" -> RawInterval(0, 0, 0, 0, 0, 2, 0, 0),
      "00:00:02" -> RawInterval(0, 0, 0, 0, 0, 0, 2, 0),
      "00:00:00.002" -> RawInterval(0, 0, 0, 0, 0, 0, 0, 2)
    ).foreach {
      case (k, v) =>
        val interval = parseInterval(k)
        assert(interval == v)
    }
  }

  test("parse multiple items") {
    val interval = parseInterval("1 year 2 mons 3 days 04:05:06.007")
    assert(interval == RawInterval(1, 2, 0, 3, 4, 5, 6, 7))
  }

}
