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

import com.typesafe.scalalogging.StrictLogging
import raw.compiler.rql2.tests.CompilerTestContext

trait RD9137Test extends CompilerTestContext with StrictLogging {

  test("""Json.InferAndRead("https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json")""".stripMargin)(
    _ should run
  )

  test("""Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json", type collection(
    |        record(
    |            city: string,
    |            country: string,
    |            patient_id: string,
    |            year_of_birth: int,
    |            gender: string
    |        )
    |    ))""".stripMargin)(_ should run)

  test("""Collection.Take(Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json", type collection(
    |        record(
    |            diagnosis: collection(
    |                record(
    |                    diag_id: string,
    |                    code: string,
    |                    diag_date: date,
    |                    description: string,
    |                    patient_id: string
    |                )
    |            )
    |        )
    |    )), 0)""".stripMargin)(_ should run)

  test("""Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json", type collection(
    |        record(
    |            diagnosis: list(
    |                record(
    |                    diag_id: string,
    |                    code: string,
    |                    description: string,
    |                    patient_id: string
    |                )
    |            )
    |        )
    |    ))""".stripMargin)(_ should run)

  test("""Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json", type collection(
    |        record(
    |            diagnosis: collection(
    |                record(
    |                    diag_id: string,
    |                    code: string,
    |                    diag_date: date,
    |                    description: string,
    |                    patient_id: string
    |                )
    |            )
    |        )
    |    ))""".stripMargin)(_ should run)

  test("""Json.Read("https://raw-tutorial.s3.eu-west-1.amazonaws.com/patients.json", type collection(
    |        record(
    |            city: string,
    |            country: string,
    |            patient_id: string,
    |            year_of_birth: int,
    |            gender: string,
    |            diagnosis: collection(
    |                record(
    |                    diag_id: string,
    |                    code: string,
    |                    diag_date: date,
    |                    description: string,
    |                    patient_id: string
    |                )
    |            )
    |        )
    |    ))""".stripMargin)(_ should run)

}
