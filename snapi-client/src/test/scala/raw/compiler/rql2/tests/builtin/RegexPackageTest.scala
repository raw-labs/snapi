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

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait RegexPackageTest extends Rql2CompilerTestContext {

  test("""Regex.Replace("Heelloo John", "[aeiou]+", "_")""")(it => it should evaluateTo(""" "H_ll_ J_hn" """))

  test("""[ Regex.Replace("Heelloo John", "(invalid", "_") ]""")(it =>
    it should evaluateTo("""[ Error.Build("Unclosed group near index 8\n(invalid") ]""")
  )

  test("""Regex.Matches("123", "\\d+")""")(it => it should evaluateTo("true"))

  test("""Regex.Matches("abc 123", "\\d+")""")(it => it should evaluateTo("false"))

  test("""[ Regex.Matches("abc 123", "(invalid") ]""".stripMargin)(it =>
    it should evaluateTo("""[ Error.Build("Unclosed group near index 8\n(invalid") ] """)
  )

  test("""Regex.FirstMatchIn("abc 123", "\\d+")""")(it => it should evaluateTo(""" "123" """))

  test("""Regex.FirstMatchIn("abc", "\\d+")""")(it => it should evaluateTo(""" null """))

  test("""[ Regex.FirstMatchIn("abc", "(invalid") ]""".stripMargin)(it =>
    it should evaluateTo("""[ Error.Build("Unclosed group near index 8\n(invalid") ]""")
  )

  test("""Regex.Groups("23-06-1975", "(\\d+)-(\\d+)-(\\d+)")""")(it =>
    it should evaluateTo(""" ["23","06","1975"] """.stripMargin)
  )

  test("""Regex.Groups("23-06-", "(\\d+)-(\\d+)-(\\d+)?")""")(it =>
    it should evaluateTo(""" ["23", "06", null] """.stripMargin)
  )

  test("""Regex.Groups("Eat 12 carrots at 12:30", "\\w+ (\\d+) [\\w ]+ (\\d{2}:\\d{2})")""")(it =>
    it should evaluateTo(""" ["12", "12:30"] """.stripMargin)
  )

  test("""let groups = Regex.Groups("Eat 12 carrots at 12:30", "\\w+ (\\d+) [\\w ]+ (\\d{2}:\\d{2})")
    |in List.Get(groups, 0)""".stripMargin)(it => it should evaluateTo(""" "12" """.stripMargin))

  // no match
  test("""[ Regex.Groups("23-06-", "(\\d+)-(\\d+)-(\\d+)") ]""")(it =>
    it should evaluateTo(""" [ Error.Build("string '23-06-' does not match pattern '(\\d+)-(\\d+)-(\\d+)'") ]""")
  )

  test("""[ Regex.Groups("23-06-", "(invalid") ]""")(it =>
    it should evaluateTo("""[ Error.Build("Unclosed group near index 8\n(invalid") ]""")
  )

}
