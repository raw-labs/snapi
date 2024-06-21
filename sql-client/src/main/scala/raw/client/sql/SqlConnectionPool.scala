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
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import raw.creds.api.CredentialsService
import raw.utils.{AuthenticatedUser, RawService, RawSettings, RawUtils}

import java.sql.SQLException
import java.util.concurrent.TimeUnit

class SqlConnectionPool(credentialsService: CredentialsService)(implicit settings: RawSettings)
    extends RawService
    with StrictLogging {

  private val dbHost = settings.getString("raw.creds.jdbc.fdw.host")
  private val dbPort = settings.getInt("raw.creds.jdbc.fdw.port")
  private val readOnlyUser = settings.getString("raw.creds.jdbc.fdw.user")
  private val password = settings.getString("raw.creds.jdbc.fdw.password")
  private val maxConnections = settings.getInt("raw.client.sql.pool.max-connections")
  private val idleTimeout = settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS)
  private val maxLifetime = settings.getDuration("raw.client.sql.pool.max-lifetime", TimeUnit.MILLISECONDS)
  private val connectionTimeout = settings.getDuration("raw.client.sql.pool.connection-timeout", TimeUnit.MILLISECONDS)

  private val connectionCacheSize = settings.getInt("raw.client.sql.fdw-db-cache.size")
  private val connectionCachePeriod = settings.getDuration("raw.client.sql.fdw-db-cache.period")

  private val dbCacheLoader = new CacheLoader[AuthenticatedUser, HikariDataSource]() {
    override def load(user: AuthenticatedUser): HikariDataSource = {

      val (db, currentSchema) = settings.getStringOpt(s"raw.creds.jdbc.${user.uid.uid}.db") match {
        case Some(db) => (db, settings.getStringOpt(s"raw.creds.jdbc.${user.uid.uid}.schema"))
        // Directly call the provisioning method on the client
        case None => (credentialsService.getUserDb(user), None)
      }

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
      pool
    }
  }

  private val dbCache: LoadingCache[AuthenticatedUser, HikariDataSource] = CacheBuilder
    .newBuilder()
    .maximumSize(connectionCacheSize)
    .expireAfterAccess(connectionCachePeriod)
    .build(dbCacheLoader)

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    dbCache.get(user).getConnection()
  }

  override def doStop(): Unit = {
    dbCache.asMap().values().forEach { pool =>
      logger.info(s"Shutting down SQL connection pool for database ${pool.getJdbcUrl}")
      RawUtils.withSuppressNonFatalException(pool.close())
    }
  }
}
