package raw.client.sql

import org.bitbucket.inkytonik.kiama.util.Positions
import org.scalatest.funsuite.AnyFunSuite
import raw.client.sql.antlr4.{
  ParseProgramResult,
  RawSqlSyntaxAnalyzer,
  SqlIdentifierNode,
  SqlKeywordNode,
  SqlProgramNode,
  SqlStatementNode
}

class TestSqlParser extends AnyFunSuite {

  private def doTest(code: String): ParseProgramResult = {
    val positions = new Positions
    val parser = new RawSqlSyntaxAnalyzer(positions)
    parser.parse(code)
  }

  test("simple select") {
    val code = """SELECT * FROM "table"""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    assert(result.params.isEmpty)
    assert(result.returnDescription.isEmpty)
    result.tree match {
      case SqlProgramNode(statements, comments) => statements.head match {
          case SqlStatementNode(statementItems) =>
            assert(statementItems.size == 4)
            assert(statementItems(0).isInstanceOf[SqlKeywordNode])
            assert(statementItems(1).isInstanceOf[SqlKeywordNode])
            assert(statementItems(2).isInstanceOf[SqlKeywordNode])
            assert(statementItems(3).isInstanceOf[SqlIdentifierNode])
            val identifier = statementItems(3).asInstanceOf[SqlIdentifierNode]
            assert(
              identifier.name == "table"
            )
        }

    }
  }

  test("Test with all parameters values single line comments") {
    val code = """
      | -- @param name Name of the client.
      | -- @type name VARCHAR
      | -- @default name 'john'
      | -- @return Returns lines when of the corresponding client name.
      |SELECT * FROM costumers WHERE "name" = :name""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    assert(result.params.size == 1)
    result.params.get("name") match {
      case Some(param) =>
        assert(param.name == "name")
        assert(param.tipe.get == "VARCHAR")
        assert(param.default.contains("'john'"))
        assert(param.description.get == "Name of the client.")
        result.positions.getStart(param.occurrences.head) match {
          case Some(start) =>
            assert(start.line == 6)
            assert(start.column == 40)
          case None => fail("Expected position not found")
        }
        result.positions.getFinish(param.occurrences.head) match {
          case Some(end) =>
            assert(end.line == 6)
            assert(end.column == 45)
          case None => fail("Expected position not found")
        }
      case None => fail("Expected parameter not found")
    }
  }

  test("Test with all parameters values multiline line comments") {
    val code = """/* @param name Name of the client.
                 | @type name VARCHAR
                 | @default name 'john'
                 | @return Returns lines when of the corresponding client name.
                 | */
                 |SELECT * FROM costumers WHERE "name" = :name""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    assert(result.params.size == 1)
    result.params.get("name") match {
      case Some(param) =>
        assert(param.name == "name")
        assert(param.tipe.get == "VARCHAR")
        assert(param.default.contains("'john'"))
        assert(param.description.get == "Name of the client.")
        result.positions.getStart(param.occurrences.head) match {
          case Some(start) =>
            assert(start.line == 6)
            assert(start.column == 40)
          case None => fail("Expected position not found")
        }
        result.positions.getFinish(param.occurrences.head) match {
          case Some(end) =>
            assert(end.line == 6)
            assert(end.column == 45)
          case None => fail("Expected position not found")
        }
      case None => fail("Expected parameter not found")
    }
  }

  test("Test errors when parameter is defined twice") {
    val code = """
      | -- @param name Name of the client.
      | -- @type name VARCHAR
      | -- @default name 'john'
      | -- @default name 'john'
      | -- @return Returns lines when of the corresponding client name.
      |SELECT * FROM costumers WHERE "name" = :name""".stripMargin
    val result = doTest(code)
    assert(result.errors.size == 1)
    assert(result.errors.head.message == "Duplicate parameter default definition for name")
  }

}
