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

package raw.compiler.rql2.tests.builtin

import com.google.common.collect.HashMultiset
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

import scala.collection.JavaConverters._

@TruffleTests class EnvironmentPackageTest extends Rql2TruffleCompilerTestContext {

  test("""Environment.Secret("my-typo")""")(it => it should runErrorAs("unknown secret: my-typo"))

  test("""Environment.Scopes()""")(it => it should evaluateTo("""[]"""))

  test("""reading scopes""") { it =>
    val scopes = Set("scope1", "scope2")
    val path = doExecute("Environment.Scopes()", scopes = scopes).right.get

    outputParser(path, "collection(string)") match {
      case s: HashMultiset[_] => assert(s.asScala.toSet == scopes)
    }

  }

}
