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

package raw.inferrer.local.xml

import org.scalatest.funsuite.AnyFunSuite
import raw.inferrer._

class XmlTypeReaderTest extends AnyFunSuite {

  test("get type of inner elements") {
    val xml = """<top>
      |<a>1</a>
      |<a>1.1</a>
      |<a>Hello</a>
      |<a/>
      |</top>
      |""".stripMargin
    val typeReader = new InferrerXmlTypeReader(xml, 200)

    typeReader.skipToNext()
    assert(typeReader.nextObj(SourceNothingType()) == SourceIntType(false))
    assert(typeReader.nextObj(SourceNothingType()) == SourceDoubleType(false))
    assert(typeReader.nextObj(SourceNothingType()) == SourceStringType(false))
    assert(typeReader.nextObj(SourceNothingType()) == SourceNullType())
    assert(typeReader.atEndOfObj())
    typeReader.skipToNext()
    assert(typeReader.atEndOfDocument())
  }

  test("skip elements") {
    val xml = """<top>
      |<a>1</a>
      |<a>1.1</a>
      |<a>Hello</a>
      |<a/>
      |</top>
      |""".stripMargin

    var typeReader = new InferrerXmlTypeReader(xml, 2)
    assert(
      typeReader.nextObj(SourceNothingType()) == SourceRecordType(
        Vector(
          SourceAttrType("a", SourceCollectionType(SourceDoubleType(false), false))
        ),
        false
      )
    )
    assert(typeReader.atEndOfDocument())

    // inferring the full thing, now a type is collection(string nullable)
    typeReader = new InferrerXmlTypeReader(xml, -1)
    assert(
      typeReader.nextObj(SourceNothingType()) == SourceRecordType(
        Vector(
          SourceAttrType("a", SourceCollectionType(SourceStringType(true), false))
        ),
        false
      )
    )
    assert(typeReader.atEndOfDocument())
  }

  test("skip elements inner collection") {
    val xml = """<top>
      |  <a>
      |     <c>Hello</c>
      |     <d>1</d>
      |  </a>
      |  <b>
      |     <e>Hello</e>
      |     <f>1</f>
      |     <f>1.1</f>
      |     <f>hello</f>
      |     <f/>
      |  </b>
      |</top>
      |""".stripMargin

    var typeReader = new InferrerXmlTypeReader(xml, 2)
    assert(
      typeReader.nextObj(SourceNothingType()) == SourceRecordType(
        Vector(
          SourceAttrType(
            "a",
            SourceRecordType(
              Vector(
                SourceAttrType("c", SourceStringType(false)),
                SourceAttrType("d", SourceIntType(false))
              ),
              false
            )
          ),
          SourceAttrType(
            "b",
            SourceRecordType(
              Vector(
                SourceAttrType("e", SourceStringType(false)),
                SourceAttrType("f", SourceCollectionType(SourceDoubleType(false), false))
              ),
              false
            )
          )
        ),
        false
      )
    )
    assert(typeReader.atEndOfDocument())

    // inferring the full thing, now f type is collection(string nullable)
    typeReader = new InferrerXmlTypeReader(xml, -1)
    assert(
      typeReader.nextObj(SourceNothingType()) == SourceRecordType(
        Vector(
          SourceAttrType(
            "a",
            SourceRecordType(
              Vector(
                SourceAttrType("c", SourceStringType(false)),
                SourceAttrType("d", SourceIntType(false))
              ),
              false
            )
          ),
          SourceAttrType(
            "b",
            SourceRecordType(
              Vector(
                SourceAttrType("e", SourceStringType(false)),
                SourceAttrType("f", SourceCollectionType(SourceStringType(true), false))
              ),
              false
            )
          )
        ),
        false
      )
    )
    assert(typeReader.atEndOfDocument())
  }

  test("infer temporals") {
    val xml = """<top>
      |<a>09:23 AM</a>
      |<a>1975-06-23</a>
      |<a>1975-06-23 09:30 AM</a>
      |</top>
      |""".stripMargin
    val typeReader = new InferrerXmlTypeReader(xml, 200)

    typeReader.skipToNext()
    assert(typeReader.nextObj(SourceNothingType()) == SourceTimeType(Some("K:m a"), false))
    assert(typeReader.nextObj(SourceNothingType()) == SourceDateType(Some("yyyy-M-d"), false))
    assert(typeReader.nextObj(SourceNothingType()) == SourceTimestampType(Some("yyyy-M-d K:m a"), false))
    assert(typeReader.atEndOfObj())
    typeReader.skipToNext()
    assert(typeReader.atEndOfDocument())
  }

  test("do not infer temporals if propagated type is a string") {
    val xml = """<top>
      |<a>09:23 AM</a>
      |<a>1975-06-23</a>
      |<a>1975-06-23 09:30 AM</a>
      |</top>
      |""".stripMargin
    val typeReader = new InferrerXmlTypeReader(xml, 200)

    typeReader.skipToNext()
    assert(typeReader.nextObj(SourceStringType(false)) == SourceStringType(false))
    assert(typeReader.nextObj(SourceStringType(false)) == SourceStringType(false))
    assert(typeReader.nextObj(SourceStringType(false)) == SourceStringType(false))
    assert(typeReader.atEndOfObj())
    typeReader.skipToNext()
    assert(typeReader.atEndOfDocument())
  }

  def inferInner(xml: String, currentType: SourceType) = {
    val typeReader = new InferrerXmlTypeReader(xml, 200)
    typeReader.skipToNext()
    var objType = currentType
    while (!typeReader.atEndOfObj()) {
      objType = typeReader.nextObj(objType)
    }
    objType
  }

  test("propagate type in fields") {

    val xml = """<top>
      |   <a> <b>09:23 AM</b> <c>1975-06-23</c> <d>1975-06-23 09:30 AM</d></a>
      |</top>
      |""".stripMargin
    var propagated: SourceType = SourceStringType(false)
    var expected: SourceType = SourceRecordType(
      Vector(
        SourceAttrType("b", SourceTimeType(Some("K:m a"), true)),
        SourceAttrType("c", SourceDateType(Some("yyyy-M-d"), true)),
        SourceAttrType("d", SourceTimestampType(Some("yyyy-M-d K:m a"), true)),
        SourceAttrType("#text", SourceStringType(true))
      ),
      false
    )
    assert(inferInner(xml, propagated) == expected)

    // all fields are started as strings
    propagated = SourceRecordType(
      Vector(
        SourceAttrType("b", SourceStringType(false)),
        SourceAttrType("c", SourceStringType(false)),
        SourceAttrType("d", SourceStringType(false))
      ),
      false
    )
    expected = propagated
    assert(inferInner(xml, propagated) == expected)

    propagated = SourceRecordType(Vector(SourceAttrType("c", SourceStringType(false))), false)
    expected = SourceRecordType(
      Vector(
        SourceAttrType("b", SourceTimeType(Some("K:m a"), true)),
        SourceAttrType("c", SourceStringType(false)),
        SourceAttrType("d", SourceTimestampType(Some("yyyy-M-d K:m a"), true))
      ),
      false
    )
    assert(inferInner(xml, propagated) == expected)

    propagated =
      SourceRecordType(Vector(SourceAttrType("c", SourceCollectionType(SourceStringType(false), false))), false)
    expected = SourceRecordType(
      Vector(
        SourceAttrType("b", SourceTimeType(Some("K:m a"), true)),
        SourceAttrType("c", SourceCollectionType(SourceStringType(false), false)),
        SourceAttrType("d", SourceTimestampType(Some("yyyy-M-d K:m a"), true))
      ),
      false
    )
    assert(inferInner(xml, propagated) == expected)
  }
}
