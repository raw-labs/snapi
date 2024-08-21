/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.compiler.sql

import com.rawlabs.compiler.sql.antlr4.{
  ParseProgramResult,
  RawSqlSyntaxAnalyzer,
  SqlFunctionCall,
  SqlIdentifierNode,
  SqlKeywordNode,
  SqlProgramNode,
  SqlProjNode,
  SqlStatementNode,
  SqlStringLiteralNode,
  SqlWithComaSeparatorNode
}
import org.bitbucket.inkytonik.kiama.util.Positions
import org.scalatest.funsuite.AnyFunSuite

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
      case SqlProgramNode(statement) => statement match {
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

  test("Test with spaces in default value and type") {
    val code = """
      | -- @param age age of the person
      | -- @type age double precision
      | -- @default age 10 * 3.14
      | -- @return Returns lines when of the corresponding person's age.
      |SELECT * FROM people WHERE "age" = :age""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    assert(result.params.size == 1)
    assert(result.returnDescription.contains("Returns lines when of the corresponding person's age."))
    result.params.get("age") match {
      case Some(param) =>
        assert(param.name == "age")
        assert(param.tipe.get == "double precision")
        assert(param.default.contains("10 * 3.14"))
        assert(param.description.get == "age of the person")
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
      case SqlProgramNode(statement) => statement match {
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
    val code = """SELECT * FROM example.airports WHERE city = :param and airport_id = :param""".stripMargin
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

  test("Test OR Case") {
    val code = """SELECT COUNT(*) FROM example.airports
      |WHERE city = :name OR country = :name""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test random keyword test") {
    val code = """SELECT DATE '2002-01-01' - :s::int AS x -- RD-10538""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test complex random code 1") {
    val code = """CREATE FUNCTION check_password(uname TEXT, pass TEXT)
      |RETURNS BOOLEAN AS $$
      |DECLARE passed BOOLEAN;
      |BEGIN
      |        SELECT  (pwd = $2) INTO passed
      |        FROM    pwds
      |        WHERE   username = $1;
      |
      |        RETURN passed;
      |END;
      |$$  LANGUAGE plpgsql
      |    SECURITY DEFINER
      |    -- Set a secure search_path: trusted schema(s), then 'pg_temp'.
      |    SET search_path = admin, pg_temp;""".stripMargin
    val result = doTest(code)
    assert(result.hasErrors)
    assert(result.errors.forall(m => m.message == "Only one statement is allowed"))
  }

  test("Test complex random code 2") {
    val code = """WITH regional_sales AS (
      |    SELECT region, SUM(amount) AS total_sales
      |    FROM orders
      |    GROUP BY region
      |), top_regions AS (
      |    SELECT region
      |    FROM regional_sales
      |    WHERE total_sales > (SELECT SUM(total_sales)/10 FROM regional_sales)
      |)
      |SELECT region,
      |       product,
      |       SUM(quantity) AS product_units,
      |       SUM(amount) AS product_sales
      |FROM orders
      |WHERE region IN (SELECT region FROM top_regions)
      |GROUP BY region, product;""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test complex random code 3") {
    val code = """WITH regional_sales AS (
      |    SELECT region, SUM(amount) AS total_sales
      |    FROM orders
      |    GROUP BY region
      |), top_regions AS (
      |    SELECT region
      |    FROM regional_sales
      |    WHERE total_sales > (SELECT SUM(total_sales)/10 FROM regional_sales)
      |)
      |SELECT region,
      |       product,
      |       SUM(quantity) AS product_units,
      |       SUM(amount) AS product_sales
      |FROM orders
      |WHERE region IN (SELECT region FROM top_regions)
      |GROUP BY region, product;""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test complex random code 4") {
    val code = """WITH RECURSIVE included_parts(sub_part, part, quantity) AS (
      |    SELECT sub_part, part, quantity FROM parts WHERE part = 'our_product'
      |  UNION ALL
      |    SELECT p.sub_part, p.part, p.quantity
      |    FROM included_parts pr, parts p
      |    WHERE p.part = pr.sub_part
      |  )
      |SELECT sub_part, SUM(quantity) as total_quantity
      |FROM included_parts
      |GROUP BY sub_part""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test complex random code 5") {
    val code = """WITH RECURSIVE search_graph(id, link, data, depth, path, cycle) AS (
      |        SELECT g.id, g.link, g.data, 1,
      |          ARRAY[ROW(g.f1, g.f2)],
      |          false
      |        FROM graph g
      |      UNION ALL
      |        SELECT g.id, g.link, g.data, sg.depth + 1,
      |          path || ROW(g.f1, g.f2),
      |          ROW(g.f1, g.f2) = ANY(path)
      |        FROM graph g, search_graph sg
      |        WHERE g.id = sg.link AND NOT cycle
      |)
      |SELECT * FROM search_graph;""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test complex ChatGPT generated query") {
    val code = """SELECT
      |    p.project_name,
      |    SUM(e.salary) AS total_salary,
      |    COUNT(DISTINCT e.department_id) AS unique_departments
      |FROM
      |    projects p
      |INNER JOIN
      |    employee_project ep ON p.project_id = ep.project_id
      |INNER JOIN
      |    employees e ON e.employee_id = ep.employee_id
      |WHERE
      |    p.project_id IN (
      |        SELECT
      |            ep_inner.project_id
      |        FROM
      |            employee_project ep_inner
      |        JOIN
      |            employees e_inner ON ep_inner.employee_id = e_inner.employee_id
      |        GROUP BY
      |            ep_inner.project_id
      |        HAVING
      |            COUNT(DISTINCT e_inner.department_id) > 1
      |    )
      |GROUP BY
      |    p.project_name
      |ORDER BY
      |    total_salary DESC;""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test missing identifier after dot") {
    val code = """SELECT * FROM example.airports
      |WHERE ai.
      |AND   airports.
      |""".stripMargin
    val result = doTest(code)
    assert(result.errors.size == 2)
    assert(
      result.errors.head.message == "Missing identifier after '.'"
    )
    result.tree match {
      case SqlProgramNode(statement) => statement match {
          case SqlStatementNode(statementItems) =>
            assert(statementItems.size == 8)
            statementItems(5) match {
              case SqlProjNode(identifiers) => identifiers(0) match {
                  case identifier: SqlIdentifierNode =>
                    assert(identifier.name == "ai")
                    assert(!identifier.isQuoted)
                }
            }
            statementItems(7) match {
              case SqlProjNode(identifiers) => identifiers(0) match {
                  case identifier: SqlIdentifierNode =>
                    assert(identifier.name == "airports")
                    assert(!identifier.isQuoted)
                }
            }
        }
    }
  }

  test("Test escape identifiers") {
    val code = "SELECT smth from \"\"\"hello\""
    val result = doTest(code)
    assert(result.isSuccess)
    result.tree match {
      case SqlProgramNode(statement) => statement match {
          case SqlStatementNode(statementItems) =>
            assert(statementItems.size == 4)
            statementItems(3) match {
              case identifier: SqlIdentifierNode =>
                assert(identifier.name == "\"\"hello")
                assert(identifier.isQuoted)
            }
        }
    }
  }

  test("Test unicode parsing properly") {
    val code = "SELECT smth from U&\"d\\0061t\\+000061\""
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("Test only one statement is allowed error") {
    val code = """select * from smth;
      |select * from smth;""".stripMargin
    val result = doTest(code)
    assert(result.errors.size == 1)
    assert(
      result.errors.head.positions.head.begin.line == 2 &&
        result.errors.head.positions.head.begin.column == 1 &&
        result.errors.head.positions.head.end.line == 2 &&
        result.errors.head.positions.head.end.column == 19
    )
    assert(result.errors.head.message == "Only one statement is allowed")
  }

  test("Test multiple statements with comments") {
    val code = """select * from smth; -- a comment""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    result.tree match {
      case SqlProgramNode(statement) => statement match {
          case SqlStatementNode(statementItems) => assert(statementItems.size == 4)
        }
    }
  }

  test("Test multiple statements with comments more advanced") {
    val code = """select * from smth;
      | -- a comment;
      | -- @param hello hello""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    result.tree match {
      case SqlProgramNode(statement) => statement match {
          case SqlStatementNode(statementItems) => assert(statementItems.size == 4)
        }
    }
    assert(result.params.isEmpty)
  }

  test("Test mock sql query with comments") {
    val code = """SELECT
      |    -- Identifier, which comes from "generate_series"
      |    s as id,
      |    -- Pick a random first name from 'firstnames' array
      |    arrays.firstnames[trunc(random() * ARRAY_LENGTH(arrays.firstnames, 1) + 1)] AS firstname,
      |    -- Pick a random middle name character
      |    substring('ABCDEFGHIJKLMNOPQRSTUVWXYZ' from trunc(random() * 26 + 1)::int for 1) AS middlename,
      |    -- Pick a random last name from 'lastnames' array
      |    arrays.lastnames[trunc(random() * ARRAY_LENGTH(arrays.lastnames,1) + 1)] AS lastname,
      |    -- Pick number birthdate
      |  date(now() - trunc(random() * 365 * 82 /*max age + 18*/) * '1 day'::interval - interval '18 year' /* min age*/) as birth_date,
      |    -- Generate random SSN number
      |  100000000 + round(random() * 900000000) as ssn,
      |    -- Generate random amount
      |  round((random() * 100000)::numeric, 2) as amount,
      |    -- Generate random house number
      |  (100 + random() * 9900)::int as house,
      |    -- Generate random stree name from 'streets'
      |  arrays.streets[trunc(random() * ARRAY_LENGTH(arrays.streets, 1) + 1)]  AS street,
      |    -- Choose random street type
      |  case (random() * 2)::int when 0 then 'St.' when 1 then 'Ave.' when 2 then 'Rd.' end as street_type,
      |    -- Pick random city
      |  arrays.cities[trunc(random() * ARRAY_LENGTH(arrays.cities, 1) + 1)] AS city,
      |    -- Pick random state
      |  arrays.states[trunc(random() * ARRAY_LENGTH(arrays.states, 1) + 1)] AS state,
      |    -- Generate random phone number
      |  concat(FLOOR(100 + random() * 900), ' ', FLOOR(100 + random() * 900), ' ', FLOOR(1000 + random() * 9000)) as phone
      |FROM
      |    -- Number of rows to generate
      |    generate_series(1, 1000) AS s
      |CROSS JOIN(
      |    SELECT ARRAY[
      |    'Adam', 'Bill', 'Bob', 'Donald', 'Frank', 'George', 'James', 'John', 'Jacob', 'Jack', 'Martin', 'Matthew', 'Max', 'Michael', 'Paul','Peter', 'Ronald',
      |    'Samuel','Steve','William', 'Abigail', 'Alice', 'Amanda', 'Barbara','Betty', 'Carol', 'Donna', 'Jane','Jennifer','Julie','Mary','Melissa','Sarah','Susan'
      |    ] AS firstnames,
      |    ARRAY[
      |        'Matthews','Smith','Jones','Davis','Jacobson','Williams','Donaldson','Maxwell','Peterson','Stevens', 'Franklin','Washington','Jefferson','Adams',
      |        'Jackson','Johnson','Lincoln','Grant','Fillmore','Harding','Taft', 'Truman','Nixon','Ford','Carter','Reagan','Bush','Clinton','Hancock'
      |    ] AS lastnames,
      |    ARRAY[
      |    'Green', 'Smith', 'Church', 'Grant', 'Cedar', 'Forest', 'Frankl', 'Birch', 'Jones', 'Brown',
      |    'Cherry', 'Willow', 'Rose', 'School', 'Wilson', 'Center', 'Walnut', 'Mill', 'Valley'
      |    ] as streets,
      |    ARRAY[
      |    'Washington', 'Franklin', 'Clinton', 'Georgetown', 'Springfield', 'Chester', 'Greenville', 'Dayton', 'Madison', 'Salem',
      |    'Winchester', 'Oakland', 'Milton', 'Newport', 'Ashland', 'Riverside', 'Manchester', 'Oxford', 'Burlington', 'Jackson', 'Milford'
      |    ] as cities,
      |    ARRAY[
      |    'AK','AL','AR','AZ','CA','CO','CT','DC','DE','FL','GA','HI','IA','ID','IL','IN','KS','KY','LA','MA','MD','ME','MI','MN','MS','MO',
      |    'MT','NC','NE','NH','NJ','NM','NV','NY','ND','OH','OK','OR','PA','RI','SC','SD','TN','TX','UT','VT','VA','WA','WV','WI','WY'
      |    ] as states
      |) AS arrays""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("single quoted string with a newline") {
    val code = """SELECT 'c
      | si
      | bon' FROM x
      |""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("single quoted identifier with a newline") {
    val code = """SELECT "c
      | si
      | bon" FROM x
      |""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
  }

  test("colon in the end of the code with a newline") {
    val code = """:
      |""".stripMargin
    val result = doTest(code)
    assert(result.isSuccess)
    val SqlProgramNode(stmt) = result.tree
    assert(result.positions.getStart(stmt).isDefined)
    assert(result.positions.getStart(stmt).flatMap(_.optOffset).isDefined)
    assert(result.positions.getFinish(stmt).flatMap(_.optOffset).isDefined)
  }

  test("multiline string-identifier test") {
    val code = """select "
      |a
      |" from anything """.stripMargin
    val result = doTest(code)
    val SqlProgramNode(stmt) = result.tree
    stmt match {
      case SqlStatementNode(statementItems) =>
        assert(statementItems.size == 4)
        statementItems(1) match {
          case node: SqlIdentifierNode =>
            val finish = result.positions.getFinish(node)
            assert(finish.get.line == 3)
        }
    }
  }

  test("multiline string test") {
    val code = """select '
      |a
      |' from anything """.stripMargin
    val result = doTest(code)
    val SqlProgramNode(stmt) = result.tree
    stmt match {
      case SqlStatementNode(statementItems) =>
        assert(statementItems.size == 4)
        statementItems(1) match {
          case node: SqlStringLiteralNode =>
            val finish = result.positions.getFinish(node)
            assert(finish.get.line == 3)
        }
    }
  }

  test("newline after comment") {
    val code = """select
      |  id,
      |  key,
      |  summary,
      |  project_key,
      |  status,
      |  assignee_display_name,
      |  assignee_account_id
      |from
      |  "jira-danai".jira_backlog_issue
      |  limit 2
      |--where
      |-- assignee_display_name = 'yann';
      | """.stripMargin
    val result = doTest(code)
    checkStartEnd(result)
  }

  private def checkStartEnd(result: ParseProgramResult) = {
    val SqlProgramNode(stmt) = result.tree
    assert(result.positions.getStart(stmt).isDefined)
    assert(result.positions.getFinish(stmt).isDefined)
    assert(result.positions.getStart(stmt).flatMap(_.optOffset).isDefined)
    assert(result.positions.getFinish(stmt).flatMap(_.optOffset).isDefined)
  }

}
