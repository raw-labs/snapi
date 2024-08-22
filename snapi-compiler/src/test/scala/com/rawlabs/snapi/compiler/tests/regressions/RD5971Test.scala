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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class RD5971Test extends Rql2TruffleCompilerTestContext {

  // error when type isn't supported
  test(s"""[{a: "binary", b: [1,2,3,4]}]""") { it =>
    option("output-format", "binary")
    it should runErrorAs("unsupported type")
  }

  // error when type isn't supported
  test(s"""[{a: "text", b: [1,2,3,4]}]""") { it =>
    option("output-format", "text")
    it should runErrorAs("unsupported type")
  }

  // regular string (works)
  test(s""" "tralala" """) { it =>
    option("output-format", "text")
    it should run
  }

  // null works
  test(s""" List.First(["tralala", "ploum", "ploum"]) """) { it =>
    option("output-format", "text")
    it should run
  }

  // errors are propagated
  test(s""" List.First(["tralala", "ploum", "ploum"]) + Error.Build("argh!") """) { it =>
    option("output-format", "text")
    it should runErrorAs("argh!")
  }

  // errors are propagated
  test(s""" "tralala" + Error.Build("argh!") """) { it =>
    option("output-format", "text")
    it should runErrorAs("argh!")
  }

  // regular binary (works)
  test("""Binary.FromString("olala")""") { it =>
    option("output-format", "binary")
    it should run
  }

  // null works
  test("""Binary.FromString(List.First(["olala"]))""") { it =>
    option("output-format", "binary")
    it should run
  }

  // errors are propagated
  test("""Binary.FromString(List.First(["olala"]) + Error.Build("argh!"))""") { it =>
    option("output-format", "binary")
    it should runErrorAs("argh!")
  }

}
