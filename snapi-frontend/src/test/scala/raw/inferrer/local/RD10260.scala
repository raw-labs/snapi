package raw.inferrer.local

import com.typesafe.scalalogging.StrictLogging
import raw.inferrer.api._
import raw.inferrer.local.json.JsonInferrer
import raw.sources.api.SourceContext
import raw.utils.{RawTestSuite, SettingsTestContext}

import java.io.StringReader
class RD10260 extends RawTestSuite with SettingsTestContext with StrictLogging {

  test("Infer or-type starting on a record") { _ =>
    val json = """[
      | {"name": "Big Ben", "info": {"height": 96, "year": 1859}},
      | {"name": "Tower Bridge", "info": "51°30′20″N 0°04′31″W"},
      | {"name": "Pont Neuf", "info": "48°51′24″N, 2°20′27″E"},
      | {"name": "Eiffel Tower",  "info": {"height": 300, "year": 1889}}
      |]""".stripMargin
    implicit val sourceContext: SourceContext = new SourceContext(null, null, settings, None)
    val inferrer = new JsonInferrer
    val reader = new StringReader(json)
    val JsonInputFormatDescriptor(tipe, _, _, _, _) = inferrer.infer(reader, None)
    val expected = SourceCollectionType(
      SourceRecordType(
        Vector(
          SourceAttrType("name", SourceStringType(false)),
          SourceAttrType(
            "info",
            SourceOrType(
              Set(
                SourceStringType(false),
                SourceRecordType(
                  Vector(SourceAttrType("height", SourceIntType(false)), SourceAttrType("year", SourceIntType(false))),
                  nullable = false
                )
              )
            )
          )
        ),
        nullable = false
      ),
      nullable = false
    )

    assert(tipe == expected)
  }

}
