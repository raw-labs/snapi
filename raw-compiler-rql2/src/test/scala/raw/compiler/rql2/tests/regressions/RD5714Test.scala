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

package raw.compiler.rql2.tests.regressions

import java.nio.file.Files
import raw.compiler.rql2.tests.CompilerTestContext

import scala.io.Source

trait RD5714Test extends CompilerTestContext {

  test("""let colA = [{id: 1, name: "john"}],
    |    colB = [{id: 2, firstName: "john"}],
    |    join = List.Join(colA, colB, i -> i.name == i.firstName)
    |in Json.Print(join)""".stripMargin) { it =>
    assume(language == "rql2-truffle") // The scala executor renames duplicated fields because RD-9359 is fixed
    it should evaluateTo(""" "[{\"id\":1,\"name\":\"john\",\"id\":2,\"firstName\":\"john\"}]" """.stripMargin)
  }

  test("""let colA = [{id: 1, name: "john"}],
    |    colB = [{id: 2, firstName: "john"}],
    |    join = List.Join(colA, colB, i -> i.name == i.firstName)
    |in join""".stripMargin) { it =>
    assume(language == "rql2-truffle") // The scala executor renames duplicated fields because RD-9359 is fixed
    val path = Files.createTempFile("query", "result")
    try {
      it should saveTo(path)
      val s = Source.fromFile(path.toFile).getLines().mkString("\n")
      assert(s == """[{"id":1,"name":"john","id":2,"firstName":"john"}]""")
    } finally {
      Files.delete(path)
    }
  }

}
