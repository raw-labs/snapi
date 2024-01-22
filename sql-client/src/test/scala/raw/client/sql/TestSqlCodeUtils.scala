package raw.client.sql

import org.scalatest.funsuite.AnyFunSuite
import raw.client.api.Pos

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
        val result = SqlCodeUtils.identifiers(code)
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
        val result = SqlCodeUtils.identifiers(code)
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
        val result = SqlCodeUtils.identifiers(code)
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
        val result = SqlCodeUtils.identifiers(code)
        if (result != expected)
          throw new AssertionError(s"Values did not match for $code: expected $expected but got $result")
    }
  }

  test("extract tokens") {
    val code = "select * from schema.table"
    val results = SqlCodeUtils.tokens(code)
    val expected = Seq(("select", Pos(1, 1)), ("*", Pos(1, 8)), ("from", Pos(1, 10)), ("schema.table", Pos(1, 15)))
    assert(results == expected)
  }

  test("extract tokens with quotes") {
    val code = """select * from "schema"."table" where name = 'john smith' """
    val results = SqlCodeUtils.tokens(code)
    val expected = Seq(
      ("select", Pos(1, 1)),
      ("*", Pos(1, 8)),
      ("from", Pos(1, 10)),
      ("\"schema\".\"table\"", Pos(1, 15)),
      ("where", Pos(1, 32)),
      ("name", Pos(1, 38)),
      ("=", Pos(1, 43)),
      ("'john smith'", Pos(1, 45))
    )
    assert(results == expected)
  }

  test("extract tokens with quotes and lines") {
    val code = """select *
      |   from "schema"."table"
      |   where name = 'john smith'""".stripMargin
    val results = SqlCodeUtils.tokens(code)
    val expected = Seq(
      ("select", Pos(1, 1)),
      ("*", Pos(1, 8)),
      ("from", Pos(2, 4)),
      ("\"schema\".\"table\"", Pos(2, 9)),
      ("where", Pos(3, 4)),
      ("name", Pos(3, 10)),
      ("=", Pos(3, 15)),
      ("'john smith'", Pos(3, 17))
    )
    assert(results == expected)
  }

  test("SqlCodeUtils.getIdentifierUnder") {
    val code = """SELECT * FROM example.airports
      |WHERE airports.
      |""".stripMargin

    val values = Seq(
      Pos(1, 16) -> Seq(SqlIdentifier("example", quoted = false)),
      Pos(1, 15) -> Seq(SqlIdentifier("example", quoted = false)),
      Pos(1, 22) -> Seq(SqlIdentifier("example", quoted = false)),
      Pos(1, 23) -> Seq(SqlIdentifier("airports", quoted = false)),
      Pos(2, 12) -> Seq(SqlIdentifier("airports", quoted = false)),
    )
    val analyzer = new SqlCodeUtils(code)
    for((pos, expected) <- values ) {
      val idns = analyzer.getIdentifierUnder(pos)
      if (idns != expected) throw new AssertionError(s"getIdentifierUnder failed for pos $pos, expected $expected but got $idns")
    }
  }

  test("SqlCodeUtils.getIdentifierUpTo") {
    val code = """SELECT * FROM example.airports
                 |WHERE airports.
                 |""".stripMargin

    val values = Seq(
      Pos(1, 16) -> Seq(SqlIdentifier("e", quoted = false)),
      Pos(1, 19) -> Seq(SqlIdentifier("exam", quoted = false)),
      Pos(1, 28) -> Seq(SqlIdentifier("example", quoted = false), SqlIdentifier("airpo", quoted = false)),
    )
    val analyzer = new SqlCodeUtils(code)
    for((pos, expected) <- values ) {
      val idns = analyzer.getIdentifierUpTo(pos)
      if (idns != expected) throw new AssertionError(s"getIdentifierUnder failed for pos $pos, expected $expected but got $idns")
    }
  }
}
