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
import raw.compiler.SnapiInterpolator

trait RD5631Test extends CompilerTestContext {

  private val sampleData = tempFile("""{
    |  "version": "0",
    |  "id": "107cfa03-19d7-1e96-b6e3-efc24a7b158e",
    |  "detail-type": "Auth0 log",
    |  "source": "aws.partner/auth0.com/raw-#####-###-###-###-####/auth0.logs",
    |  "account": "12341234",
    |  "time": "2022-11-11T06:46:39Z",
    |  "region": "eu-west-1",
    |  "resources": [],
    |  "detail": {
    |    "log_id": "90020221111064627068205877781201590913260820163280765026",
    |    "data": {
    |      "date": "2022-11-11T06:46:23.765Z",
    |      "type": "fs",
    |      "description": "The user already exists.",
    |      "connection": "Username-Password-Authentication",
    |      "connection_id": "con_M1GmJ2DBnuVUe3zB",
    |      "client_id": "#####",
    |      "client_name": "Producer",
    |      "ip": "123.123.123.123",
    |      "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36",
    |      "details": {
    |        "body": {
    |          "connection": "Username-Password-Authentication",
    |          "client_id": "###",
    |          "email": "whoami@gmail.com",
    |          "password": "*****",
    |          "tenant": "raw",
    |          "transaction": {
    |            "id": "ap-###",
    |            "locale": "en",
    |            "protocol": "oidc-basic-profile",
    |            "requested_scopes": [
    |              "openid",
    |              "profile",
    |              "email",
    |              "prod"
    |            ],
    |            "acr_values": [],
    |            "ui_locales": [],
    |            "redirect_uri": "https://app.raw-labs.com/admin",
    |            "prompt": [],
    |            "state": "###",
    |            "login_hint": null,
    |            "response_mode": "query",
    |            "response_type": [
    |              "code"
    |            ]
    |          },
    |          "request_language": "en-US,en;q=0.9,ar;q=0.8"
    |        }
    |      },
    |      "user_id": "",
    |      "user_name": "whoami@gmail.com",
    |      "strategy": "auth0",
    |      "strategy_type": "database",
    |      "log_id": "123412431234"
    |    }
    |  }
    |}
    |""".stripMargin)

  test(snapi"""main() =
    |  let
    |    json_type = type record(
    |    version: string,
    |    id: string,
    |    `detail-type`: string,
    |    source: string,
    |    account: string,
    |    time: string,
    |    region: string,
    |    resources: collection(undefined),
    |    detail: record(
    |        log_id: string,
    |        data: record(
    |            date: string,
    |            `type`: string,
    |            description: string,
    |            connection: string,
    |            connection_id: string,
    |            client_id: string,
    |            client_name: string,
    |            ip: string,
    |            user_agent: string,
    |            details: record(
    |                body: record(
    |                    connection: string,
    |                    client_id: string,
    |                    email: string,
    |                    password: string,
    |                    tenant: string,
    |                    transaction: record(
    |                        id: string,
    |                        locale: string,
    |                        protocol: string,
    |                        requested_scopes: collection(string),
    |                        acr_values: collection(undefined),
    |                        ui_locales: collection(undefined),
    |                        redirect_uri: string,
    |                        prompt: collection(undefined),
    |                        state: string,
    |                        login_hint: undefined,
    |                        response_mode: string,
    |                        response_type: collection(string)),
    |                    request_language: string)),
    |            user_id: string,
    |            user_name: string,
    |            strategy: string,
    |            strategy_type: string,
    |            log_id: string))),
    |    // list all files of the bucket path
    |    files = ["$sampleData"],
    |    a = List.Transform(files, f ->
    |      let
    |        recr = Json.Read(f, json_type),
    |        a = Json.Print(
    |          Record.Build(
    |            time = recr.detail.data.date,
    |            event = "registration-fail",
    |            user = recr.detail.data.details.body.email,
    |            cause = recr.detail.data.description ))
    |      in
    |        a
    |    )
    |  in
    |    a
    |
    |main()""".stripMargin)(it => it should run)

}
