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
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import raw.utils.{AuthenticatedUser, RawException, RawSettings}

import java.sql.SQLException
import java.util.concurrent.TimeUnit
import scala.collection.mutable

//class SqlClientMissingCredentialsException(cause: Throwable) extends RawException("add credentials to use SQL", cause)
class SqlClientMissingCredentialsException(cause: Throwable)
    extends RawException("to properly execute SQL queries, first add credentials", cause)

class SqlConnectionPool(settings: RawSettings) {

  // one pool of connections per DB (which means per user).
  private val pools = mutable.Map.empty[String, HikariDataSource]
  private val dbHost = settings.getString("raw.creds.jdbc.fdw.host")
  private val dbPort = settings.getInt("raw.creds.jdbc.fdw.port")
  private val readOnlyUser = settings.getString("raw.creds.jdbc.fdw.user")
  private val password = settings.getString("raw.creds.jdbc.fdw.password")

  // Default pool for the users that haven't registered a credential yet.
  private val defaultPool: HikariDataSource = {
    val config = new HikariConfig()
    val defaultDB = settings.getString("raw.creds.jdbc.fdw.default.db")
    val defaultDBHost = settings.getString("raw.creds.jdbc.fdw.default.host")
    val defaultDBPort = settings.getInt("raw.creds.jdbc.fdw.default.port")
    config.setJdbcUrl(s"jdbc:postgresql://$defaultDBHost:$defaultDBPort/$defaultDB")
    // (CTM)
    config.setMaximumPoolSize(settings.getInt("raw.client.sql.pool.max-connections"))
    config.setMaxLifetime(settings.getDuration("raw.client.sql.pool.max-lifetime", TimeUnit.MILLISECONDS))
    config.setIdleTimeout(settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS))
    config.setConnectionTimeout(
      settings.getDuration("raw.client.sql.pool.connection-timeout", TimeUnit.MILLISECONDS)
    )

    new HikariDataSource(config)
  }

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    val db = user.uid.toString().replace("-", "_")
    val userPool = pools.get(db) match {
      case Some(pool) => pool
      case None =>
        val config = new HikariConfig()
        config.setJdbcUrl(s"jdbc:postgresql://$dbHost:$dbPort/$db")
        config.setMaximumPoolSize(settings.getInt("raw.client.sql.pool.max-connections"))
        config.setMaxLifetime(settings.getDuration("raw.client.sql.pool.max-lifetime", TimeUnit.MILLISECONDS))
        config.setIdleTimeout(settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS))
        config.setConnectionTimeout(
          settings.getDuration("raw.client.sql.pool.connection-timeout", TimeUnit.MILLISECONDS)
        )
        config.setUsername(readOnlyUser)
        config.setPassword(password)
        try {
          val pool = new HikariDataSource(config)
          pools.put(db, pool)
          pool
        } catch {
          case hikariException: PoolInitializationException => hikariException.getCause match {
              case sqlException: SQLException => sqlException.getSQLState match {
                  case "3D000" =>
                    // We get that when the database does not exist. We throw a more explicit exception.
                    return defaultPool.getConnection // return default pool
                  case _ => throw hikariException
                }
              case e: Throwable => throw e
            }
          case e: Throwable => throw e
        }
    }
    userPool.getConnection
  }

}
