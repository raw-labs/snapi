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

import raw.compiler.rql2.tests.Rql2CompilerTestContext

trait RD5797Test extends Rql2CompilerTestContext {

  // "raw.eu.auth0.com" is a wrong value, it should be 'auth0' for example
  test("""
    |let
    |  hello = String.Read(Http.Get("https://...",
    |            clientId="...",
    |            clientSecret="..-..-..",
    |            authProvider="raw.eu.auth0.com"))
    |in
    |  hello""".stripMargin)(_ should runErrorAs("invalid credential type"))

  test("""
    |let
    |  hello = Json.InferAndRead(Http.Get("https://...",
    |            clientId="...",
    |            clientSecret="..-..-..",
    |            authProvider="raw.eu.auth0.com"))
    |in
    |  hello""".stripMargin)(_ should runErrorAs("invalid credential type"))

  test("""
    |let
    |  hello = Json.Read(Http.Get("https://...",
    |            clientId="...",
    |            clientSecret="..-..-..",
    |            authProvider="raw.eu.auth0.com"), type list(int))
    |in
    |  hello""".stripMargin)(_ should runErrorAs("invalid credential type"))

  test("""
    |let
    |  hello = Collection.Sum(Json.Read(Http.Get("https://...",
    |            clientId="...",
    |            clientSecret="..-..-..",
    |            authProvider="raw.eu.auth0.com"), type collection(int)))
    |in
    |  hello""".stripMargin)(_ should runErrorAs("invalid credential type"))
}
