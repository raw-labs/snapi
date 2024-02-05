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
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import raw.utils.{AuthenticatedUser, RawException, RawSettings}

import java.sql.SQLException
import java.util.concurrent.TimeUnit
import scala.collection.mutable

//class SqlClientMissingCredentialsException(cause: Throwable) extends RawException("add credentials to use SQL", cause)
class SqlClientMissingCredentialsException(cause: Throwable)
    extends RawException("to properly execute SQL queries, first add credentials", cause)

class SqlConnectionPool(settings: RawSettings) extends StrictLogging {

  private val pools = mutable.Map.empty[String, HikariDataSource]

  // Default pool for users that do not have a DB yet
  val defaultPool = {
    val config = new HikariConfig()
    val defaultDB = settings.getString("raw.creds.jdbc.fdw.default.db")
    val defaultDBHost = settings.getString("raw.creds.jdbc.fdw.default.host")
    val defaultDBPort = settings.getInt("raw.creds.jdbc.fdw.default.port")
    config.setJdbcUrl(s"jdbc:postgresql://$defaultDBHost:$defaultDBPort/$defaultDB")
    config.setMaximumPoolSize(settings.getInt("raw.client.sql.default-pool.max-connections"))
    config.setMinimumIdle(settings.getInt("raw.client.sql.default-pool.min-idle"))
    config.setMaxLifetime(settings.getDuration("raw.client.sql.default-pool.max-lifetime", TimeUnit.MILLISECONDS))
    config.setIdleTimeout(settings.getDuration("raw.client.sql.default-pool.idle-timeout", TimeUnit.MILLISECONDS))
    config.setConnectionTimeout(
      settings.getDuration("raw.client.sql.pool.connection-timeout", TimeUnit.MILLISECONDS)
    )
    config.setUsername(settings.getString("raw.creds.jdbc.fdw.default.user"))
    config.setPassword(settings.getString("raw.creds.jdbc.fdw.default.password"))
    new HikariDataSource(config)
  }

  private val defaultPoolsLastTime = mutable.Map.empty[String, Long]
  private val expiration = settings.getDuration("raw.client.sql.default-pool.expiration-time")

  private val dbHost = settings.getString("raw.creds.jdbc.fdw.host")
  private val dbPort = settings.getInt("raw.creds.jdbc.fdw.port")
  private val readOnlyUser = settings.getString("raw.creds.jdbc.fdw.user")
  private val password = settings.getString("raw.creds.jdbc.fdw.password")

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    val db = user.uid.toString().replace("-", "_")
    // Check is the last usage of the default pool is expired
    if (
      defaultPoolsLastTime.contains(db) && (System.currentTimeMillis() - defaultPoolsLastTime(db)) < expiration.toMillis
    ) {
      logger.info(s"Using already existing default pool for user $db")
      return defaultPool.getConnection
    }

    val userPool = pools.get(db) match {
      case Some(pool) => pool
      case None =>
        logger.info(s"Checking user DB $db")
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
        try {
          val pool = new HikariDataSource(config)
          // remove the key from the map, we will now connect only to the user DB
          logger.info(s"Connected to user deleting default entry for $db")
          defaultPoolsLastTime.remove(db)
          pools.put(db, pool)
          pool
        } catch {
          case hikariException: PoolInitializationException => hikariException.getCause match {
              case sqlException: SQLException => sqlException.getSQLState match {
                  case "3D000" =>
                    logger.info(s"user $db not up yet, using default pool for now.")
                    // Put a new value in the map to avoid checking if the user DB until expiration time
                    defaultPoolsLastTime(db) = System.currentTimeMillis()
                    defaultPool
                  case _ => throw hikariException
                }
              case e: Throwable => throw e
            }
        }
    }
    userPool.getConnection
  }

}
