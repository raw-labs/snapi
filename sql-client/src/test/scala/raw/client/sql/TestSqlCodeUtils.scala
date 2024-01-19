package raw.client.sql

import org.scalatest.funsuite.AnyFunSuite

class TestSqlCodeUtils extends AnyFunSuite {

  test("extract identifiers") {
    val values = Seq(
      "table" -> Seq(SqlIdentifier("table", quoted = false)),
      "schema.table" -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("table", quoted = false)),
      "schema.table.row" -> Seq(
        SqlIdentifier("schema", quoted = false),
        SqlIdentifier("table", quoted = false),
        SqlIdentifier("row", quoted = false)
      )
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.getIdentifiers(code)
        if (result != expected)
          throw new AssertionError(s"Values did not match for $code: expected $expected but got $result")
    }
  }

  test("extract identifiers with quotes") {
    val values = Seq(
      "\"schema\"" -> Seq(SqlIdentifier("schema", quoted = true)),
      "\"schema\".table" -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("table", quoted = false)),
      "\"schema\".table.row" -> Seq(
        SqlIdentifier("schema", quoted = true),
        SqlIdentifier("table", quoted = false),
        SqlIdentifier("row", quoted = false)
      ),
      "schema.\"table\"" -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("table", quoted = true)),
      "schema.\"table\".row" -> Seq(
        SqlIdentifier("schema", quoted = false),
        SqlIdentifier("table", quoted = true),
        SqlIdentifier("row", quoted = false)
      ),
      "\"schema\".\"table\"" -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("table", quoted = true)),
      "\"schema\".\"table\".\"row\"" -> Seq(
        SqlIdentifier("schema", quoted = true),
        SqlIdentifier("table", quoted = true),
        SqlIdentifier("row", quoted = true)
      ),
      "\"schema\".\"table\".row" -> Seq(
        SqlIdentifier("schema", quoted = true),
        SqlIdentifier("table", quoted = true),
        SqlIdentifier("row", quoted = false)
      ),
      // escaped quotes in the schema name
      "\"s\"\"s\".\"table\"" -> Seq(
        SqlIdentifier("s\"s", quoted = true),
        SqlIdentifier("table", quoted = true)
      ),
      "\"s . s\".\"t . t\"" -> Seq(
        SqlIdentifier("s . s", quoted = true),
        SqlIdentifier("t . t", quoted = true)
      ),
      "\"s . s\".\"t . t\".\"r . r\"" -> Seq(
        SqlIdentifier("s . s", quoted = true),
        SqlIdentifier("t . t", quoted = true),
        SqlIdentifier("r . r", quoted = true)
      )
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.getIdentifiers(code)
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
      "schema.\"table" -> Seq(SqlIdentifier("schema", quoted = false), SqlIdentifier("table", quoted = true)),
      "schema.\"table\"." -> Seq(
        SqlIdentifier("schema", quoted = false),
        SqlIdentifier("table", quoted = true),
        SqlIdentifier("", quoted = false)
      ),
      "schema.table.\"row" -> Seq(
        SqlIdentifier("schema", quoted = false),
        SqlIdentifier("table", quoted = false),
        SqlIdentifier("row", quoted = true)
      )
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.getIdentifiers(code)
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
      "\"schema\".\"table\"table" -> Seq(SqlIdentifier("schema", quoted = true), SqlIdentifier("table", quoted = true))
    )
    values.foreach {
      case (code, expected) =>
        val result = SqlCodeUtils.getIdentifiers(code)
        if (result != expected)
          throw new AssertionError(s"Values did not match for $code: expected $expected but got $result")
    }
  }
}
