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
import raw.compiler._
import raw.compiler.rql2.tests.CompilerTestContext

trait RD8266Test extends CompilerTestContext with StrictLogging {

  val queryEnvironment: ProgramEnvironment = ProgramEnvironment(Some("snapi"), Set.empty, Map.empty)

  def dotAutoCompleteTest(code: String, line: Int, col: Int) = {
    val response = doLsp(DotAutoCompleteLSPRequest(code, queryEnvironment, Pos(line, col)))
    response match {
      case AutoCompleteLSPResponse(entries, _) => entries.toSeq
      case _ => throw new AssertionError(s"Unexpected response: $response")
    }
  }

  def hoverRequest(code: String, line: Int, col: Int) = {
    val response = doLsp(HoverLSPRequest(code, queryEnvironment, Pos(line, col)))
    response match {
      case HoverLSPResponse(hoverResponse: LSPHoverResponse, errors: List[ErrorMessage]) => hoverResponse
      case _ => throw new AssertionError(s"Unexpected response: $response")
    }
  }

  test("LSP ClassCastException") { _ =>
    val code = snapi"""getAuth0Users(
      |    email: string = null,
      |    start_date: = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(months = 1))),
      |    last_login_since: date = Date.FromTimestamp(Date.SubtractInterval(Date.Now(), Interval.Build(years = 1))),
      |    end_date: timestamp = Date.AddInterval(Date.Now(), Interval.Build(days = 1)),
      |    organization: string = null) =
      |  let
      |    // start_timestamp = Timestamp.FromDate(start_date),
      |    auth0_user_type = type record(
      |          created_at: string,
      |          email: string,
      |          email_verified: bool,
      |          user_id: string,
      |          last_login: string,
      |          last_ip: string,
      |          logins_count: int,
      |          app_metadata: record(
      |              rawOrg: collection(string),
      |              raw_clients: collection(undefined)
      |          )
      |      ),
      |    auth0_users_type = type collection(record(
      |          created_at: string,
      |          email: string,
      |          email_verified: bool,
      |          user_id: string,
      |          last_login: string,
      |          last_ip: string,
      |          logins_count: int,
      |          app_metadata: record(
      |              rawOrg: collection(string),
      |              raw_clients: collection(undefined)
      |          )
      |      )),
      |
      |    rec getUserPage(page: int = 1): auth0_users_type =
      |      let
      |        payload =
      |          if(page>10 or page<0)
      |          then
      |            Error.Build("Illegal page number. Valid pages are [1..10]")
      |          else if(page==10)
      |          then
      |            Collection.Empty(auth0_user_type)
      |          else
      |            Collection.Union(
      |              Json.Read(
      |                Http.Get(
      |                  "https://raw.eu.auth0.com/api/v2/users",
      |                  authCredentialName="auth0_management_token",
      |                  args=List.Build(
      |                    Record.Build(_1="per_page",_2="100"),
      |                    Record.Build(_1="fields", _2="email,app_metadata,email_verified,user_id,last_login,last_ip,logins_count,created_at"),
      |                    Record.Build(_1="page",_2=String.From(page)))
      |                ),
      |                auth0_users_type
      |              ),
      |              getUserPage(page+1)
      |          ),
      |        filterByEmail =
      |          if(Nullable.IsNull(email))
      |          then
      |            payload
      |          else
      |            Collection.Filter(payload, p -> p.email==email),
      |        filterStartDate =
      |          Collection.Filter(filterByEmail, t -> start_date<=Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'")),
      |        filterEndDate =
      |          Collection.Filter(filterStartDate, t -> end_date<=Timestamp.Parse(t.created_at, "yyyy-M-d'T'H:m:s.SSS'Z'")),
      |        filterLastLogin =
      |          Collection.Filter(filterEndDate, t -> last_login_since<=Timestamp.Parse(t.last_login, "yyyy-M-d'T'H:m:s.SSS'Z'")),
      |        filterOrg =
      |          if(Nullable.IsNull(organization))
      |          then
      |            filterLastLogin
      |          else
      |            Collection.Filter(filterLastLogin, t -> Collection.Contains(t.app_metadata.rawOrg, organization)),
      |        output = filterOrg
      |      in
      |        filterByEmail
      |
      |  in
      |    getUserPage(0)
      |
      |getAuth0Users(
      |  start_date=Date.Build(2023, 3, 5),
      |  last_login_since=Date.Build(2023, 1, 1)
      |)""".stripMargin

    hoverRequest(code, 3, 19)
    dotAutoCompleteTest(code, 3, 19)
  }
}
