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

import raw.compiler.rql2.tests.CompilerTestContext

trait RD4741Test extends CompilerTestContext {

  // (CTM): Removed Regex.Build, this tests has to change
  // used to trigger RD-4741: the `s` string was left as an IdnExp in the L0.Tree, and couldn't be
  // read as a StringConst
  // class raw.compiler.common.source.IdnExp cannot be cast to class raw.compiler.L0.source.StringConst
  ignore(
    """let s = "[a-z]*",
      |    r = Regex.Build(s),
      |    l = Collection.Build(r)
      |in Collection.Count(l)
      |""".stripMargin
  )(_ should evaluateTo("""
    |let r = Regex.Build("[a-z]*"),
    |    l = Collection.Build(r)
    |in Collection.Count(l)
    |""".stripMargin))

}
