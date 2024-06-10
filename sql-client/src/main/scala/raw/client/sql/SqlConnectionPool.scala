/*
 * Copyright 2024 RAW Labs S.A.
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
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import raw.creds.api.CredentialsService
import raw.utils.{AuthenticatedUser, RawService, RawSettings, RawUtils}

import java.sql.SQLException
import java.util.concurrent.TimeUnit
import scala.collection.mutable

class SqlConnectionPool(credentialsService: CredentialsService)(implicit settings: RawSettings)
    extends RawService
    with StrictLogging {

  // One pool of connections per DB (which means per user).
  private val pools = mutable.Map.empty[String, HikariDataSource]
  private val poolsLock = new Object

  private val dbHost = settings.getString("raw.creds.jdbc.fdw.host")
  private val dbPort = settings.getInt("raw.creds.jdbc.fdw.port")
  private val readOnlyUser = settings.getString("raw.creds.jdbc.fdw.user")
  private val password = settings.getString("raw.creds.jdbc.fdw.password")
  private val maxConnections = settings.getInt("raw.client.sql.pool.max-connections")
  private val idleTimeout = settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS)
  private val maxLifetime = settings.getDuration("raw.client.sql.pool.max-lifetime", TimeUnit.MILLISECONDS)
  private val connectionTimeout = settings.getDuration("raw.client.sql.pool.connection-timeout", TimeUnit.MILLISECONDS)

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    // Try to find a user DB in the config settings...
    settings.getStringOpt(s"raw.creds.jdbc.${user.uid.uid}.db") match {
      case Some(db) =>
        logger.debug(s"Found database $db for user ${user.uid.uid} in settings.")
        getConnection(db, settings.getStringOpt(s"raw.creds.jdbc.${user.uid.uid}.schema"))
      case None =>
        // None found, so check the credentials service.
        val db = credentialsService.getUserDb(user)
        logger.debug(s"Found database $db for user $user in credentials service.")
        getConnection(db)
    }
  }

  @throws[SQLException]
  private def getConnection(db: String, currentSchema: Option[String] = None): java.sql.Connection = {
    val pool = {
      poolsLock.synchronized {
        pools.get(db) match {
          case Some(existingPool) => existingPool
          case None =>
            // Create a pool and store it in `pools`.
            logger.info(s"Creating a SQL connection pool for database $db")
            val config = new HikariConfig()
            val jdbcUrl = currentSchema match {
              case Some(schema) => s"jdbc:postgresql://$dbHost:$dbPort/$db?currentSchema=$schema"
              case None => s"jdbc:postgresql://$dbHost:$dbPort/$db"
            }
            config.setJdbcUrl(jdbcUrl)
            config.setMaximumPoolSize(maxConnections)
            config.setMinimumIdle(0)
            config.setIdleTimeout(idleTimeout)
            config.setMaxLifetime(maxLifetime)
            config.setConnectionTimeout(connectionTimeout)
            config.setUsername(readOnlyUser)
            config.setPassword(password)
            val pool = new HikariDataSource(config)
            pools.put(db, pool)
            pool
        }
      }
    }
    pool.getConnection
  }

  override def doStop(): Unit = {
    poolsLock.synchronized {
      for ((db, pool) <- pools) {
        logger.info(s"Shutting down SQL connection pool for database $db")
        RawUtils.withSuppressNonFatalException(pool.close())
      }
    }
  }
}
