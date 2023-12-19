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

package raw.sources.jdbc.pgsql

import raw.creds.api.PostgresqlCredential
import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.LocationDescription

object PostgresqlClients {

  def get(dbName: String, location: LocationDescription)(implicit sourceContext: SourceContext): PostgresqlClient = {
    val cred: PostgresqlCredential = location.getStringSetting("db-host") match {
      case Some(host) =>
        val port = location.getIntSetting("db-port")
        val userName = location.getStringSetting("db-username")
        val password = location.getStringSetting("db-password")
        PostgresqlCredential(host, port, dbName, userName, password)
      case _ => sourceContext.credentialsService.getRDBMSServer(sourceContext.user, dbName) match {
          case Some(cred: PostgresqlCredential) => cred
          case _ => throw new LocationException(s"no credential found for postgresql: $dbName")
        }
    }
    new PostgresqlClient(cred)(sourceContext.settings)
  }

}
