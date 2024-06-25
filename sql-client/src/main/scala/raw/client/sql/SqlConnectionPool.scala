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
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache, RemovalNotification}
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import raw.creds.api.CredentialsService
import raw.utils.{AuthenticatedUser, RawService, RawSettings, RawUtils}

import java.sql.SQLException
import java.util.concurrent.{ConcurrentHashMap, Executors, TimeUnit}
import scala.collection.mutable

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

  private val poolGarbageCollectionPeriod = settings.getDuration("raw.client.sql.pool.gc-period")
  private val poolsToDelete = new ConcurrentHashMap[String, HikariDataSource]()
  private val garbageCollectScheduller =
    Executors.newSingleThreadScheduledExecutor(RawUtils.newThreadFactory("sql-connection-pool-gc"))

  // Periodically check for idle pools and close them
  // If the hikari pool in the cache expires and still has active connections, we will move it to the poolsToDelete map
  // Then we delete it later when the active connections are 0 (i.e. long queries are done and the pool is not needed anymore)
  garbageCollectScheduller.scheduleAtFixedRate(
    () => {
      val urlsToRemove = mutable.ArrayBuffer[String]()
      poolsToDelete.forEach((url, pool) => {
        if (pool.getHikariPoolMXBean.getActiveConnections == 0) {
          logger.info(s"Shutting down SQL connection pool for database $url")
          RawUtils.withSuppressNonFatalException(pool.close())
          urlsToRemove += url
        }
      })
      urlsToRemove.foreach(url => poolsToDelete.remove(url))
    },
    poolGarbageCollectionPeriod.toMillis,
    poolGarbageCollectionPeriod.toMillis,
    TimeUnit.MILLISECONDS
  )

  private val connectionCacheSize = settings.getInt("raw.client.sql.fdw-db-cache.size")
  private val connectionCachePeriod = settings.getDuration("raw.client.sql.fdw-db-cache.duration")

  private val dbCacheLoader = new CacheLoader[String, HikariDataSource]() {
    override def load(url: String): HikariDataSource = {
      logger.info(s"Creating a SQL connection pool for url $url")
      val config = new HikariConfig()
      config.setJdbcUrl(url)
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

  private val dbCache: LoadingCache[String, HikariDataSource] = CacheBuilder
    .newBuilder()
    .maximumSize(connectionCacheSize)
    .expireAfterAccess(connectionCachePeriod)
    .removalListener((notification: RemovalNotification[String, HikariDataSource]) => {
      val active = notification.getValue.getHikariPoolMXBean.getActiveConnections
      if (active == 0) {
        logger.info(s"Shutting down SQL connection pool for database ${notification.getValue.getJdbcUrl}")
        RawUtils.withSuppressNonFatalException(notification.getValue.close())
      } else {
        poolsToDelete.put(notification.getKey, notification.getValue)
      }
    })
    .build(dbCacheLoader)

  @throws[SQLException]
  def getConnection(user: AuthenticatedUser): java.sql.Connection = {
    val db = settings.getStringOpt(s"raw.creds.jdbc.${user.uid.uid}.db").getOrElse(credentialsService.getUserDb(user))
    val maybeSchema = settings.getStringOpt(s"raw.creds.jdbc.${user.uid.uid}.schema")

    val url = maybeSchema match {
      case Some(schema) => s"jdbc:postgresql://$dbHost:$dbPort/$db?currentSchema=$schema"
      case None => s"jdbc:postgresql://$dbHost:$dbPort/$db"
    }

    dbCache.get(url).getConnection()
  }

  override def doStop(): Unit = {
    dbCache.asMap().values().forEach { pool =>
      logger.info(s"Shutting down SQL connection pool for database ${pool.getJdbcUrl}")
      RawUtils.withSuppressNonFatalException(pool.close())
    }
    garbageCollectScheduller.shutdown()
    garbageCollectScheduller.awaitTermination(5, TimeUnit.SECONDS)
  }
}
