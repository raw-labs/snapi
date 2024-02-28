package raw.client.sql

import org.bitbucket.inkytonik.kiama.util.Positions
import org.scalatest.funsuite.AnyFunSuite
import raw.client.sql.antlr4.{
  ParseProgramResult,
  RawSqlSyntaxAnalyzer,
  SqlFunctionCall,
  SqlIdentifierNode,
  SqlKeywordNode,
  SqlProgramNode,
  SqlStatementNode,
  SqlWithComaSeparatorNode
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
      case SqlProgramNode(statement, comments) => statement match {
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
    assert(result.returnDescription.contains("Returns lines when of the corresponding client name."))
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
    assert(result.returnDescription.contains("Returns lines when of the corresponding client name."))
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

  test("Test errors with unknown type") {
    val code = """
      | -- @asdfa name Name of the client.
      |SELECT * FROM costumers WHERE "name" = :name""".stripMargin
    val result = doTest(code)
    assert(result.errors.size == 1)
    assert(
      result.errors.head.message == "Unknown param annotation the only keywords allowed are @param, @return, @type, @default"
    )
  }

  test("Test type with params") {
    val code = """
      | -- @type name numeric(1,2)
      |SELECT * FROM costumers WHERE "name" = :name""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    assert(result.params.size == 1)
    result.params.get("name") match {
      case Some(param) => assert(param.tipe.get == "numeric(1,2)")
      case None => fail("Expected parameter not found")
    }
  }

  test("Test single line - multiline description") {
    val code = """-- @param name this is
      |--      a multiline description
      |--      and another line
      |SELECT * FROM costumers WHERE "name" = :name""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    assert(result.params.size == 1)
    result.params.get("name") match {
      case Some(param) => assert(param.description.get == "this is a multiline description and another line")
      case None => fail("Expected parameter not found")
    }
  }

  test("Test params without comments") {
    val code = """SELECT * FROM costumers WHERE "name" = :name""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    assert(result.params.size == 1)
    result.params.get("name") match {
      case Some(param) => result.positions.getStart(param.occurrences.head) match {
          case Some(start) =>
            assert(start.line == 1)
            assert(start.column == 40)
          case None => fail("Expected position not found")
        }
      case None => fail("Expected parameter not found")
    }
  }

  test("Test nested statements") {
    val code = """select
      |	name,
      |	(select max(longitude) from example.airports)
      |from example.airports""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test function calls") {
    val code = """select hello, NOW() from "some"."table"""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    result.tree match {
      case SqlProgramNode(statement, comments) => statement match {
          case SqlStatementNode(statementItems) =>
            assert(statementItems.size == 4)
            statementItems(1) match {
              case SqlWithComaSeparatorNode(inStatements) => inStatements(1) match {
                  case SqlFunctionCall(name, arguments) =>
                    assert(name == "NOW")
                    assert(arguments.isEmpty)
                  case _ => fail("Expected function call")
                }
            }

        }
    }
  }

  test("Test multiple param occurrences") {
    val code = """SELECT * FROM example.airports WHERE city = :param and airport_id = :param"""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    result.params.get("param") match {
      case Some(param) =>
        result.positions.getStart(param.occurrences(0)) match {
          case Some(start) =>
            assert(start.line == 1)
            assert(start.column == 45)
          case None => fail("Expected position not found")
        }
        result.positions.getStart(param.occurrences(1)) match {
          case Some(end) =>
            assert(end.line == 1)
            assert(end.column == 69)
          case None => fail("Expected position not found")
        }
      case None => fail("Expected parameter not found")
    }
  }

  test("Test unknown tokens") {
    val code = """SELECT * FROM $$""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

}
