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

package com.rawlabs.compiler.snapi.inferrer.local.json

import com.rawlabs.utils.core.{RawTestSuite, SettingsTestContext, TestData}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.matchers.{MatchResult, Matcher}
import com.rawlabs.compiler.snapi.inferrer.api._
import com.rawlabs.compiler.snapi.inferrer.local.LocalInferrerTestContext
import com.rawlabs.utils.core._

import java.io.StringReader

class JsonOrTypeTest extends RawTestSuite with SettingsTestContext with LocalInferrerTestContext {

  private var inferrer: JsonInferrer = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    inferrer = new JsonInferrer()
  }

  test("""[
    | { "a": 1, "b": null },
    | { "a": 1, "b": "Hello"}
    |]""".stripMargin) { x =>
    x should inferAsCollectionOf(
      record("a" -> SourceIntType(false), "b" -> SourceStringType(true))
    )
  }

  test(
    """[1, {"a": 1}]""".stripMargin
  )(x => x should inferAsCollectionOf(orType(SourceIntType(false), record("a" -> SourceIntType(false)))))

  test(
    """[1, {"a": 1}, "hello"]""".stripMargin
  )(x => x should inferAsCollectionOf(orType(SourceStringType(false), record("a" -> SourceIntType(false)))))

  test(
    """[1, {"a": 1}, "hello", {"b": 1.0}]""".stripMargin
  )(x =>
    x should inferAsCollectionOf(
      orType(SourceStringType(false), record("a" -> SourceIntType(true), "b" -> SourceDoubleType(true)))
    )
  )

  test(
    """["1975-06-23", {"a": 1}, "1985-12-01"]""".stripMargin
  )(x =>
    x should inferAsCollectionOf(
      orType(
        SourceDateType(Some("yyyy-M-d"), false),
        record("a" -> SourceIntType(false))
      )
    )
  )

  // format changes during the list
  test(
    """["1975-06-23", {"a": 1}, "01-12-1985"]""".stripMargin
  )(x =>
    x should inferAsCollectionOf(
      orType(
        SourceStringType(false),
        record("a" -> SourceIntType(false))
      )
    )
  )

  // changes from date to time
  test(
    """["1975-06-23", {"a": 1}, "10:00 AM"]""".stripMargin
  )(x =>
    x should inferAsCollectionOf(
      orType(
        SourceStringType(false),
        record("a" -> SourceIntType(false))
      )
    )
  )

  private class InferAs(expectedType: SourceType) extends Matcher[TestData] {
    def isEqual(t1: SourceType, t2: SourceType): Boolean = {
      (t1, t2) match {
        case (SourceRecordType(atts1, n1), SourceRecordType(atts2, n2)) =>
          atts1.forall(x1 => atts2.exists(x2 => x1.idn == x2.idn && isEqual(x1.tipe, x2.tipe))) && n1 == n2
        case (SourceCollectionType(inner1, n1), SourceCollectionType(inner2, n2)) => isEqual(inner1, inner2) && n1 == n2
        case (SourceOrType(o1), SourceOrType(o2)) => o1.forall(x1 => o2.exists(x2 => isEqual(x1, x2)))
        case _ => t1 == t2
      }
    }

    override def apply(left: TestData): MatchResult = {
      val descriptor = inferrer.infer(new StringReader(left.q), None)
      MatchResult(
        isEqual(descriptor.tipe, expectedType),
        s"Expected $expectedType but got ${descriptor.tipe}",
        s"found $expectedType"
      )
    }
  }

  private def inferAs(expectedType: SourceType) = new InferAs(expectedType)

  private def inferAsCollectionOf(expectedType: SourceType) = inferAs(SourceCollectionType(expectedType, false))

  private def record(atts: (String, SourceType)*) = {
    SourceRecordType(atts.map(x => SourceAttrType(x._1, x._2)).toVector, false)
  }

  private def orType(tipes: SourceNullableType*) = SourceOrType(tipes.toSet)
}
