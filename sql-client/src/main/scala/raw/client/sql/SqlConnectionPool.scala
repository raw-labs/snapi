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
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import raw.creds.api.CredentialsService
import raw.client.utils.{AuthenticatedUser, RawService, RawSettings, RawUtils}

import java.sql.SQLException
import java.util.concurrent.TimeUnit
import scala.collection.mutable

class SqlConnectionPool(credentialsService: CredentialsService)(implicit settings: RawSettings)
    extends RawService
    with StrictLogging {

  // One pool of connections per DB (which means per user).
  private val pools = mutable.Map.empty[String, HikariDataSource]
  private val dbHost = settings.getString("raw.creds.jdbc.fdw.host")
  private val dbPort = settings.getInt("raw.creds.jdbc.fdw.port")
  private val readOnlyUser = settings.getString("raw.creds.jdbc.fdw.user")
  private val password = settings.getString("raw.creds.jdbc.fdw.password")

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    val db = credentialsService.getUserDb(user)
    logger.debug(s"Got database $db for user $user")
    getConnection(db)
  }

  @throws[SQLException]
  private def getConnection(db: String): java.sql.Connection = {
    val pool = pools.get(db) match {
      case Some(existingPool) => existingPool
      case None =>
        // Create a pool and store it in `pools`.
        logger.info(s"Creating a SQL connection pool for database $db")
        val config = new HikariConfig()
        config.setJdbcUrl(s"jdbc:postgresql://$dbHost:$dbPort/$db")
        config.setMaximumPoolSize(settings.getInt("raw.client.sql.pool.max-connections"))
        config.setMinimumIdle(0)
        config.setIdleTimeout(settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS))
        config.setMaxLifetime(settings.getDuration("raw.client.sql.pool.max-lifetime", TimeUnit.MILLISECONDS))
        config.setConnectionTimeout(
          settings.getDuration("raw.client.sql.pool.connection-timeout", TimeUnit.MILLISECONDS)
        )
        config.setUsername(readOnlyUser)
        config.setPassword(password)
        val pool = new HikariDataSource(config)
        pools.put(db, pool)
        pool
    }
    pool.getConnection
  }

  override def doStop(): Unit = {
    for ((db, pool) <- pools) {
      logger.info(s"Shutting down SQL connection pool for database $db")
      RawUtils.withSuppressNonFatalException(pool.close())
    }
  }
}
