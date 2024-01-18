package raw.client.sql

import org.scalatest.funsuite.AnyFunSuite

class TestSqlCodeUtils extends AnyFunSuite {

  test("extract identifiers") {
    val values = Seq(
      "table" -> Seq(SqlIdentifier("table", quoted = false)),
      "schema.table" -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("table", quoted = false)),
      "schema." -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("", quoted = false))
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.separateIdentifiers(code)
        if (result != expected)
          throw new AssertionError(s"Values did not match for $code: expected $expected but got $result")
    }
  }

  test("extract identifiers with quotes") {
    val values = Seq(
      "\"table\"" -> Seq(SqlIdentifier("table", quoted = true)),
      "\"schema\".table" -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("table", quoted = false)),
      "schema.\"table\"" -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("table", quoted = true)),
      "\"schema\".\"table\"" -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("table", quoted = true)),
      "\"schema\"\"schema\".\"table\"" -> Seq(
        SqlIdentifier("schema\"schema", quoted = true),
        SqlIdentifier("table", quoted = true)
      ),
      "\"schema . schema\".\"table . table\"" -> Seq(
        SqlIdentifier("schema . schema", quoted = true),
        SqlIdentifier("table . table", quoted = true)
      )
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.separateIdentifiers(code)
        if (result != expected)
          throw new AssertionError(s"Values did not match for $code: expected $expected but got $result")
    }
  }

  test("extract incomplete identifiers") {
    val values = Seq(
      "schema." -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("", quoted = false)),
      "\"schema" -> Seq(SqlIdentifier("schema", quoted = true)),
      "\"schema." -> Seq(SqlIdentifier("schema.", quoted = true)),
      "\"schema\".\"" -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("", quoted = true)),
      "schema.\"table" -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("table", quoted = true))
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.separateIdentifiers(code)
        if (result != expected)
          throw new AssertionError(s"Values did not match for $code: expected $expected but got $result")
    }
  }

  test("bail out from extra chars") {
    val values = Seq(
      "schema " -> Seq(SqlIdentifier("schema", quoted = false)),
      "\"schema\" " -> Seq(SqlIdentifier("schema", quoted = true)),
      "\"schema\" .table" -> Seq(SqlIdentifier("schema", quoted = true)),
      "\"schema\"table" -> Seq(SqlIdentifier("schema", quoted = true)),
      "schema.table " -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("table", quoted = false)),
      "\"schema\".\"table\" " -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("table", quoted = true)),
      "\"schema\".\"table\"table" -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("table", quoted = true)),
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.separateIdentifiers(code)
        if (result != expected)
          throw new AssertionError(s"Values did not match for $code: expected $expected but got $result")
    }
  }
}
