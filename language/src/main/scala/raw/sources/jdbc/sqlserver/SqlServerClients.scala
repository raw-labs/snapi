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

package raw.sources.jdbc.sqlserver

import raw.creds.api.SqlServerCredential
import raw.sources.api.{LocationDescription, LocationException, SourceContext}

object SqlServerClients {

  def get(dbName: String, location: LocationDescription)(implicit sourceContext: SourceContext): SqlServerClient = {
    val cred: SqlServerCredential = location.getStringSetting("db-host") match {
      case Some(host) =>
        val port = location.getIntSetting("db-port")
        val userName = location.getStringSetting("db-username")
        val password = location.getStringSetting("db-password")
        SqlServerCredential(host, port, dbName, userName, password)
      case _ => sourceContext.credentialsService.getRDBMSServer(sourceContext.user, dbName) match {
          case Some(cred: SqlServerCredential) => cred
          case _ => throw new LocationException(s"no credential found for sqlserver: $dbName")
        }
    }
    new SqlServerClient(cred)(sourceContext.settings)
  }

}
