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

import raw.compiler.utils._
import raw.creds.s3.S3TestCreds
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext
import raw.testing.tags.TruffleTests

@TruffleTests class RD5932Test extends Rql2TruffleCompilerTestContext with S3TestCreds {

  val data = tempFile("""[
    |    {"id": 1, "network_interface": "eni-08b85cc07294f82bf"},
    |    {"id": 2, "network_interface": []},
    |    {"id": 3, "network_interface": null},
    |    {"id": 4}
    |]  """.stripMargin)

  test(snapi"""Json.InferAndRead("$data")""")(it => it should run)

  test(
    snapi"""let
      |     data = Json.Read(
      |        "$data",
      |        type collection(record(id: int, network_interface: string or collection(undefined)))
      |     )
      |in
      |     Collection.Filter(data, x -> Try.IsError(x.network_interface))
      |    """.stripMargin
  )(it => it should evaluateTo(""" [] """.stripMargin))

  test(
    snapi"""let
      |     data = Json.Read(
      |        "$data",
      |        type collection(record(id: int, network_interface: string or collection(undefined)))
      |     )
      |in
      |    data
      |    """.stripMargin
  )(it => it should run)

}
