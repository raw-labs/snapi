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

package raw.sources.jdbc.snowflake

import raw.creds.api.SnowflakeCredential
import raw.sources.api.{LocationException, SourceContext}
import raw.client.api.LocationDescription

object SnowflakeClients {

  def get(dbName: String, location: LocationDescription)(implicit sourceContext: SourceContext): SnowflakeClient = {

    val cred = location.getStringSetting("db-account-id") match {
      case Some(account) =>
        val userName = location.getStringSetting("db-username")
        val password = location.getStringSetting("db-password")
        val settings = location.getKVSetting("db-options").getOrElse(Array.empty)
        SnowflakeCredential(account, dbName, userName, password, settings.toMap)
      case _ => sourceContext.credentialsService.getRDBMSServer(sourceContext.user, dbName) match {
          case Some(cred: SnowflakeCredential) => cred
          case _ => throw new LocationException(s"no credential found for Snowflake: $dbName")
        }
    }

    new SnowflakeClient(cred)(sourceContext.settings)

  }

}
