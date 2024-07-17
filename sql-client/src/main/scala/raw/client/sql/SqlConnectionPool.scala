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
import raw.utils.{RawService, RawSettings, RawUtils}

import java.sql.{Connection, SQLException}
import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.util.control.NonFatal

final private case class ConnectionState(borrowed: Boolean, lastAccess: Instant)

/**
 * Implements a connection pool that is designed specifically for the needs of FDW and Steampipe.
 * This connection pool tries to keep connections active/alive for as long as possible; this ensures that Steampipe
 * caches, which are per connection, stay alive for as long as possible.
 * The connection pool has a total global limit on the number of connections. If a new connection needs to be
 * established, it will synchronously try to remove the oldest connection that isn't currently borrowed by anyone.
 * In addition, it keeps a limit on the number of active connections to a single database - this is done to prevent a
 * single "user" from taking over all available slots.
 */
class SqlConnectionPool()(implicit settings: RawSettings) extends RawService with StrictLogging {

  // Total max connections to the FDW database (across all users).
  private val maxConnections = settings.getInt("raw.client.sql.pool.max-connections")
  // Max connections per user
  private val maxConnectionsPerDb = settings.getInt("raw.client.sql.pool.max-connections-per-db")
  // Time in milliseconds after which a connection may be released (if not borrowed).
  private val idleTimeout = settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS)

  private val connectionPoolLock = new Object
  // Holds the connections available for each location.
  private val connectionCache = mutable.HashMap[String, Set[SqlConnection]]()
  // Holds the state of each connection: if it is borrowed, last access time.
  private val connectionState = mutable.HashMap[SqlConnection, ConnectionState]()
  // Holds the JDBC URL Of each connection.
  private val connectionUrls = mutable.HashMap[SqlConnection, String]()

  // Create a connection and wrap it in our helper, which takes care of 'releasing the borrow' when the connection
  // is closed.
  private def createConnection(url: String): SqlConnection = {
    val conn = java.sql.DriverManager.getConnection(url)
    new SqlConnection(this, conn)
  }

  @throws[SQLException]
  def getConnection(jdbcUrl: String): Connection = {
    connectionPoolLock.synchronized {
      // See if there is any connection available in the cache for this location.
      connectionCache.get(jdbcUrl).foreach { conns =>
        // Check if we have an available connection for the db location.
        val maybeConn = conns.collectFirst {
          case conn if !connectionState(conn).borrowed =>
            // We found one connection that is not borrowed, so let's reuse it!
            connectionState.put(conn, ConnectionState(borrowed = true, lastAccess = Instant.now()))
            return conn
        }

        // If no connection is currently available, just check if this specific db location is maxed out.
        if (maybeConn.isEmpty && conns.size >= maxConnectionsPerDb) {
          // No connection was available to borrow, and too many being used for this db location, so we cannot open
          // any more!
          throw new SQLException("too many connections active")
        }
      }

      // No connection available...

      // Check if we must release connections to make space.
      var retries = 10
      while (getTotalActiveConnections() >= maxConnections && retries >= 0) {
        if (!releaseOldestConnection()) {
          logger.warn(s"Could not release oldest connection; retry #$retries")
        }
        retries -= 1
        // (msb) Why do I do this? I don't know; I'm assuming by sleeping we increase the change of successful release.
        Thread.sleep(10)
      }

      // We could not successfully release any connection, so bail out.
      if (getTotalActiveConnections() >= maxConnections) {
        throw new SQLException("no connections available")
      }

      // Create a new connection.
      val conn = createConnection(jdbcUrl)
      connectionCache.get(jdbcUrl) match {
        case Some(conns) => connectionCache.put(jdbcUrl, conns + conn)
        case None => connectionCache.put(jdbcUrl, Set(conn))
      }
      connectionState.put(conn, ConnectionState(borrowed = true, lastAccess = Instant.now()))
      connectionUrls.put(conn, jdbcUrl)
      conn
    }
  }

  private def getTotalActiveConnections(): Int = {
    connectionPoolLock.synchronized {
      connectionState.size
    }
  }

  private def releaseOldestConnection(): Boolean = {
    connectionPoolLock.synchronized {
      // Find the oldest connection that is not borrowed/active.
      var oldestConnection: SqlConnection = null
      var oldestConnectionLastAccess: Instant = null
      connectionState.foreach {
        case (conn, state) =>
          if (!state.borrowed && (oldestConnection == null || state.lastAccess.isBefore(oldestConnectionLastAccess))) {
            oldestConnection = conn
            oldestConnectionLastAccess = state.lastAccess
          }
      }

      if (oldestConnection == null) {
        // No connection available to release, so cannot proceed.
        return false
      }

      // Release the oldest connection we found, but only if it is older than the idle timeout.
      // This prevents very "new" connections from being released.
      val oldestInstant = connectionState(oldestConnection).lastAccess
      if (oldestInstant.isAfter(Instant.now().minusMillis(idleTimeout))) {
        // Connection is "too new" to release.
        false
      } else {
        // Release the connection.
        val jdbcUrl = connectionUrls(oldestConnection)
        try {
          // First try to actually close the connection (note the use of actuallyClose), then clean up all the state.
          oldestConnection.actuallyClose()
          connectionState.remove(oldestConnection)
          connectionUrls.remove(oldestConnection)
          connectionCache.get(jdbcUrl) match {
            case Some(conns) =>
              val nconns = conns - oldestConnection
              if (nconns.isEmpty) connectionCache.remove(jdbcUrl)
              else connectionCache.put(jdbcUrl, nconns)
            case None => // Nothing to do.
          }
          true
        } catch {
          case NonFatal(t) =>
            // We failed to release the connection.
            // (Not sure why that could happen but coping with that case nonetheless...)
            logger.warn("Failed to release oldest connection", t)
            false
        }
      }
    }
  }

  // This does not actually close the underlying connection; just makes it available for 'borrow' by future requests.
  def releaseConnection(conn: SqlConnection): Unit = {
    connectionPoolLock.synchronized {
      connectionState.put(conn, ConnectionState(borrowed = false, lastAccess = Instant.now()))
    }
  }

  override def doStop(): Unit = {
    connectionPoolLock.synchronized {
      connectionCache.clear()
      connectionState.keys.foreach(conn => RawUtils.withSuppressNonFatalException(conn.close()))
      connectionState.clear()
      connectionUrls.clear()
    }
  }

}
