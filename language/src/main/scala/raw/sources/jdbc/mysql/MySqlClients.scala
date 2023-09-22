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

package raw.sources.jdbc.mysql

import raw.creds.api.MySqlCredential
import raw.sources.{LocationDescription, LocationException, SourceContext}

object MySqlClients {

  def get(dbName: String, location: LocationDescription)(implicit sourceContext: SourceContext): MySqlClient = {
    val cred: MySqlCredential = location.getStringSetting("db-host") match {
      case Some(host) =>
        val port = location.getIntSetting("db-port")
        val userName = location.getStringSetting("db-username")
        val password = location.getStringSetting("db-password")
        MySqlCredential(host, port, dbName, userName, password)
      case _ => sourceContext.credentialsService.getRDBMSServer(sourceContext.user, dbName) match {
          case Some(cred: MySqlCredential) => cred
          case _ => throw new LocationException(s"no credential found for mysql: $dbName")
        }
    }
    new MySqlClient(cred)(sourceContext.settings)
  }

}
