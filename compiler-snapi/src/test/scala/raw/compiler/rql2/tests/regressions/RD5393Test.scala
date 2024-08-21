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

import com.rawlabs.compiler.snapi.rql2.errors.CannotDetermineTypeOfParameter
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class RD5393Test extends Rql2TruffleCompilerTestContext {

  test("""main(country: string = null,code: string = null) =
    |    let
    |        patients = Json.InferAndRead(
    |            "https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json")
    |    in
    |        Collection.Filter(
    |            patients,
    |            (p) -> Nullable.IsNull(country) or p.country == country
    |                    and Nullable.IsNull(code) or
    |                        Collection.Count(Collection.Filter((d) -> d.code == code)) > 0)
    |main()""".stripMargin) { it =>
    it should typeErrorAs("expected collection but got (undefined) -> undefined")
    it should typeErrorAs(CannotDetermineTypeOfParameter.message)
  }

  test("""String.Trim(x  -> "1+1")""".stripMargin) { it =>
    it should typeErrorAs("expected string but got (undefined) -> undefined")
    it should typeErrorAs(CannotDetermineTypeOfParameter.message)
  }

  test("""String.Trim(x: string -> "1+1")""".stripMargin) { it =>
    it should typeErrorAs("expected string but got (string) -> string")
  }

  test("""String.Trim(() -> "1+1")""".stripMargin) { it =>
    it should typeErrorAs("expected string but got () -> string")
  }

  test("""String.Trim((() -> "1+1")())""".stripMargin)(it => it should evaluateTo(""" "1+1" """))

  test("""let
    |   f() = "1+1"
    |in
    |   String.Trim(f)
    |""".stripMargin)(it => it should typeErrorAs("expected string but got () -> string"))

  test("""let
    |   f = (x) -> "1+1"
    |in
    |   String.Trim(f)
    |""".stripMargin)(it => it should typeErrorAs(CannotDetermineTypeOfParameter.message))

}
