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

trait MathPackageTest extends Rql2CompilerTestContext {

  // Nullable - Tryable tests
  test("""Math.Sin(if(true) then 3.13 else null)""")(_ should beCloseTo("0.011592393936158275"))
  test("""Math.Sin(if(false) then 3.13 else null)""")(_ should evaluateTo("null"))
  test("""Math.Sin(1.2342/0)""")(_ should runErrorAs("/ by zero"))
  test("""Math.Sin(if(true) then 3.13/2 else null)""")(_ should beCloseTo("0.9999832013448761"))
  test("""Math.Sin(if(true) then 3.13/0 else null)""")(_ should runErrorAs("/ by zero"))
  test("""Math.Sin(if(false) then 3.13/2 else null)""")(_ should evaluateTo("null"))

  test("Math.Pi()") { it =>
    it should beCloseTo(s"${Math.PI}")
    it should typeAs("double")
  }

  test(s"Math.Abs(-${Int.MaxValue})") { it =>
    it should evaluateTo(s"${Int.MaxValue}")
    it should evaluateTo(s"Math.Abs(${Int.MaxValue})")
    it should typeAs("int")
  }

  test(s"Math.Abs(-${Long.MaxValue}l)") { it =>
    it should evaluateTo(s"${Long.MaxValue}l")
    it should evaluateTo(s"Math.Abs(${Long.MaxValue}l)")
    it should typeAs("long")
  }

  test("Math.Abs(-10.0)") { it =>
    it should evaluateTo("10.0")
    it should evaluateTo("Math.Abs(10.0)")
    it should typeAs("double")
  }

  test("Math.Abs(-10.0f)") { it =>
    it should evaluateTo("10.0f")
    it should evaluateTo("Math.Abs(10.0f)")
    it should typeAs("float")
  }

  test("""Math.Abs("123")""")(it =>
    it should typeErrorAs("expected either int or long or float or double but got string")
  )

  test("Math.Acos(0)") { it =>
    it should evaluateTo("Math.Acos(0.0)")
    it should beCloseTo("Math.Pi()/2")
    it should typeAs("double")
  }

  test("Math.Acos(1)") { it =>
    it should evaluateTo("Math.Acos(1.0)")
    it should evaluateTo("0.0")
    it should typeAs("double")
  }

  test("Math.Acos(0.5)")(it => it should beCloseTo("Math.Pi()/3"))

  test("""Math.Acos("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Asin(0)") { it =>
    it should evaluateTo("Math.Asin(0.0)")
    it should evaluateTo("0.0")
  }

  test("Math.Asin(1)") { it =>
    it should evaluateTo("Math.Asin(1.0)")
    it should beCloseTo("Math.Pi()/2")
  }

  test("Math.Asin(0.5)")(it => it should beCloseTo("Math.Pi()/6"))

  test("""Math.Asin("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Atan(0)") { it =>
    it should evaluateTo("Math.Atan(0.0)")
    it should evaluateTo("0.0")
    it should typeAs("double")
  }

  test("Math.Atan(1)")(it => it should beCloseTo("Math.Pi()/4"))
  test("Math.Atan(-1)")(it => it should beCloseTo("-Math.Pi()/4"))

  test("""Math.Atan("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Atn2(1, 0)")(it => it should beCloseTo("Math.Pi()/2"))
  test("Math.Atn2(0, 1)")(it => it should evaluateTo("0.0"))
  test("Math.Atn2(1, 1)")(it => it should beCloseTo("Math.Pi()/4"))

  test("""Math.Atn2("123", 0)""")(it => it should typeErrorAs("expected double but got string"))
  test("""Math.Atn2(0, "123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Ceiling(1.1)") { it =>
    it should typeAs("long")
    it should evaluateTo("2l")
  }

  test("Math.Ceiling(1.0)")(it => it should evaluateTo("1l"))

  // Math.Ceiling only works with decimals for now
  test("""Math.Ceiling("123")""")(it => it should typeErrorAs("expected decimal but got string"))

  test("Math.Cos(0)") { it =>
    it should evaluateTo("Math.Cos(0.0)")
    it should evaluateTo("1.0")
    it should typeAs("double")
  }

  test("Math.Cos(Math.Pi())")(it => it should beCloseTo("-1.0"))
  test("Math.Cos(Math.Pi()/3)")(it => it should beCloseTo("0.5"))

  test("""Math.Cos("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Cot(Math.Pi()/4)")(it => it should beCloseTo("1.0"))

  test("Math.Cot(Math.Pi()/2)")(it => it should beCloseTo("0.0"))
  test("""Math.Cot("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Degrees(Math.Pi())")(it => it should beCloseTo("180.0"))
  test("Math.Degrees(Math.Pi()/3)".stripMargin)(it => it should beCloseTo("60.0"))

  test("Math.Degrees(0.0)")(it => it should evaluateTo("0.0"))

  test("""Math.Degrees("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Exp(0)") { it =>
    it should evaluateTo("Math.Exp(0.0)")
    it should evaluateTo("1.0")
    it should typeAs("double")
  }

  test("Math.Exp(1)")(it => it should beCloseTo("2.71828182845905"))

  test("""Math.Exp("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Log(1)")(it => it should evaluateTo("0.0"))

  test("Math.Log(2.71828182845905)")(it => it should beCloseTo("1.0"))

  test("""Math.Log("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Log10(1)") { it =>
    it should typeAs("double")
    it should evaluateTo("0d")
  }

  test("Math.Log10(10)") { it =>
    it should typeAs("double")
    it should evaluateTo("1.0")
  }

  test("""Math.Log10("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Power(2, 3)") { it =>
    it should typeAs("double")
    it should evaluateTo("8.0")
  }

  test("Math.Power(4, 0.5)") { it =>
    it should typeAs("double")
    it should evaluateTo("2.0")
  }

  test("Math.Radians(0d)") { it =>
    it should typeAs("double")
    it should evaluateTo("0d")
  }

  test("Math.Radians(180)")(it => it should beCloseTo("Math.Pi()"))
  test("Math.Radians(60)")(it => it should beCloseTo("Math.Pi()/3"))

  test("""Math.Radians("123")""")(it => it should typeErrorAs("expected double but got string"))

  // how to test a random
  test("Math.Random()") { it =>
    it should typeAs("double")
    it should run
  }

  test("Math.Sign(2)") { it =>
    it should typeAs("int")
    it should evaluateTo("1")
  }

  test("Math.Sign(-2)") { it =>
    it should typeAs("int")
    it should evaluateTo("-1")
  }

  test("""Math.Sign("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Sin(0)") { it =>
    it should typeAs("double")
    it should evaluateTo("0.0")

  }

  test("Math.Sin(Math.Pi()/6)")(it => it should beCloseTo("0.5"))

  test("""Math.Sin("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Tan(0d)") { it =>
    it should typeAs("double")
    it should evaluateTo("0d")
  }

  test("Math.Tan(Math.Pi()/4)")(it => it should beCloseTo("1.0"))

  test("""Math.Tan("123")""")(it => it should typeErrorAs("expected double but got string"))

  test("Math.Square(2)")(it => it should evaluateTo(""" 4.0"""))

  test("Math.Square(3)")(it => it should evaluateTo(""" 9.0"""))

  test("Math.Floor(2.123)")(it => it should evaluateTo("""2l"""))

  test("Math.Floor(2.98)")(it => it should evaluateTo("""2l"""))

}
