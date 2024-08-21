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
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

import scala.io.Source

class RD5714Test extends Rql2TruffleCompilerTestContext {

  test("""let colA = [{id: 1, name: "john"}],
    |    colB = [{id: 2, firstName: "john"}],
    |    join = List.Join(colA, colB, i -> i.name == i.firstName)
    |in Json.Print(join)""".stripMargin) { it =>
    it should evaluateTo(""" "[{\"id\":1,\"name\":\"john\",\"id_1\":2,\"firstName\":\"john\"}]" """.stripMargin)
  }

  test("""let colA = [{id: 1, name: "john"}],
    |    colB = [{id: 2, firstName: "john"}],
    |    join = List.Join(colA, colB, i -> i.name == i.firstName)
    |in join""".stripMargin) { it =>
    val path = Files.createTempFile("query", "result")
    try {
      it should saveTo(path)
      val source = Source.fromFile(path.toFile)
      val s =
        try {
          source.getLines().mkString("\n")
        } finally {
          source.close()
        }
      assert(s == """[{"id":1,"name":"john","id_1":2,"firstName":"john"}]""")
    } finally {
      Files.delete(path)
    }
  }

}
