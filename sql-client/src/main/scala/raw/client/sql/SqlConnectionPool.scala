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
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache, RemovalListener, RemovalNotification}
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

  val listener: RemovalListener[String, HikariDataSource] = new RemovalListener[String, HikariDataSource]() {
    def onRemoval(notification: RemovalNotification[String, HikariDataSource]): Unit = {
      logger.info(s"Removing pool for user and closing ${notification.getKey}")
      notification.getValue.close()
    }
  }

  // one pool of connections per DB (which means per user).
  private val pools = mutable.Map.empty[String, HikariDataSource]
  // Default pool for users that do not have a DB yet
  // It will expire after 10s so that we can try to connect to the user's DB again.
  private val defaultPools: LoadingCache[String, HikariDataSource] = CacheBuilder
    .newBuilder()
    .expireAfterWrite(10, TimeUnit.SECONDS)
    .removalListener((listener))
    .build(new CacheLoader[String, HikariDataSource] {
      override def load(key: String): HikariDataSource = {
        logger.info(s"Creating default pool for user $key")
        val config = new HikariConfig()
        val defaultDB = settings.getString("raw.creds.jdbc.fdw.default.db")
        val defaultDBHost = settings.getString("raw.creds.jdbc.fdw.default.host")
        val defaultDBPort = settings.getInt("raw.creds.jdbc.fdw.default.port")
        config.setJdbcUrl(s"jdbc:postgresql://$defaultDBHost:$defaultDBPort/$defaultDB")
        config.setMaximumPoolSize(settings.getInt("raw.client.sql.pool.max-connections"))
        config.setMinimumIdle(0)
        config.setMaxLifetime(settings.getDuration("raw.client.sql.pool.max-lifetime", TimeUnit.MILLISECONDS))
        config.setIdleTimeout(settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS))
        config.setConnectionTimeout(
          settings.getDuration("raw.client.sql.pool.connection-timeout", TimeUnit.MILLISECONDS)
        )
        config.setUsername(settings.getString("raw.creds.jdbc.fdw.default.user"))
        config.setPassword(settings.getString("raw.creds.jdbc.fdw.default.password"))
        new HikariDataSource(config)
      }
    })

  private val dbHost = settings.getString("raw.creds.jdbc.fdw.host")
  private val dbPort = settings.getInt("raw.creds.jdbc.fdw.port")
  private val readOnlyUser = settings.getString("raw.creds.jdbc.fdw.user")
  private val password = settings.getString("raw.creds.jdbc.fdw.password")

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    val db = user.uid.toString().replace("-", "_")
    // If there is something in the defaultPools cache,
    // it means that the user tried to connect before and his DB did not exist yet.
    // Until this cache expires (10s), we will use the default DB.
    // After that we will try to connect to the user's DB again
    if (defaultPools.asMap().containsKey(db)) {
      return defaultPools.get(db).getConnection
    }

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
                    // Put a new value in the cache to avoid checking if the user DB exists for 10s
                    defaultPools.get(db)
                  case _ => throw hikariException
                }
              case e: Throwable => throw e
            }
        }
    }
    userPool.getConnection
  }

}
