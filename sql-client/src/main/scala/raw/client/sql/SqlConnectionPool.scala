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

package raw.client.sql

import org.postgresql.ds.PGConnectionPoolDataSource
import raw.utils.{AuthenticatedUser, RawSettings}

import java.sql.SQLException
import javax.sql.ConnectionPoolDataSource
import scala.collection.mutable

class SqlConnectionPool(settings: RawSettings) {

  // one pool of connections per DB (which means per user).
  private val pools = mutable.Map.empty[String, ConnectionPoolDataSource]

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    val dbHost = settings.getString("raw.creds.jdbc.fdw.host")
    val dbPort = settings.getInt("raw.creds.jdbc.fdw.port")
    val readOnlyUser = settings.getString("raw.creds.jdbc.fdw.user")
    val password = settings.getString("raw.creds.jdbc.fdw.password")
    val db = user.uid.toString().replace("-", "_")
    val userPool = pools.get(db) match {
      case Some(pool) => pool
      case None =>
        val pool = new PGConnectionPoolDataSource()
        pool.setServerNames(Array(dbHost))
        pool.setPortNumbers(Array(dbPort))
        pool.setUser(readOnlyUser)
        pool.setPassword(password)
        pool.setDatabaseName(db)
        pools.put(db, pool)
        pool
    }
    userPool.getPooledConnection().getConnection
  }

}
