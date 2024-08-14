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
import java.util.concurrent.{Executors, TimeUnit}
import scala.collection.mutable
import scala.util.control.NonFatal

final private case class ConnectionState(
    borrowed: Boolean, // True if the connection is currently borrowed (i.e. being used) by a client
    lastBorrowed: Instant, // When was the connection last borrowed by a client
    lastCheckIsAlive: Instant // When was the connection last checked if it is alive
)

/**
 * Implements a connection pool that is designed specifically for the needs of FDW and Steampipe.
 * This connection pool tries to keep connections active/alive for as long as possible; this ensures that Steampipe
 * caches, which are per connection, stay alive for as long as possible.
 * The connection pool has a total global limit on the number of connections. If a new connection needs to be
 * established, it will synchronously try to remove the oldest connection that isn't currently borrowed by anyone.
 * In addition, it keeps a limit on the number of active connections to a single database - this is done to prevent a
 * single "user" from taking over all available slots.
 * Finally, it has a background thread that checks the health state of connections in the pool; this picks up one
 * connection at a time (that is not used recently), and uses the JDBC isValid(...) method to check its validity.
 */
class SqlConnectionPool()(implicit settings: RawSettings) extends RawService with StrictLogging {

  // Total max connections to the FDW database (across all users).
  private val maxConnections = settings.getInt("raw.client.sql.pool.max-connections")
  // Max connections per user
  private val maxConnectionsPerDb = settings.getInt("raw.client.sql.pool.max-connections-per-db")
  // Time in milliseconds after which a connection may be released (if not borrowed).
  private val idleTimeout = settings.getDuration("raw.client.sql.pool.idle-timeout", TimeUnit.MILLISECONDS)

  private val healthCheckPeriod = settings.getDuration("raw.client.sql.pool.health-check-period", TimeUnit.MILLISECONDS)

  // The JDBC isValid(<seconds>) value to use.
  private val isValidSeconds = settings.getInt("raw.client.sql.pool.is-valid-seconds")

  private val connectionPoolLock = new Object
  // Holds the connections available for each location.
  private val connectionCache = mutable.HashMap[String, Set[SqlConnection]]()
  // Holds the state of each connection: if it is borrowed, last access time.
  private val connectionState = mutable.HashMap[SqlConnection, ConnectionState]()
  // Holds the JDBC URL Of each connection.
  private val connectionUrls = mutable.HashMap[SqlConnection, String]()

  private val healthChecker =
    Executors.newSingleThreadScheduledExecutor(RawUtils.newThreadFactory("sql-connection-pool-health"))

  // This connection pool checker borrows one connection that has been idle and checks if it's healthy.
  // If it is, puts it back in the pool, with a new 'last access time'. Otherwise, it removes it from the pool.
  // Because it borrows and checks a single connection at a time, this task should run rather frequently, so that it
  // has time to cycle over all idle connections before the 'first one' is idle once more.
  healthChecker.scheduleAtFixedRate(
    () => {
      // Finds ONE connection to check. That is a connection not in use and not used recently.
      val maybeConnToCheck = connectionPoolLock.synchronized {
        connectionState.collectFirst {
          case (conn, state)
              if !state.borrowed && state.lastCheckIsAlive.isBefore(Instant.now().minusMillis(idleTimeout)) =>
            connectionState.put(
              conn,
              ConnectionState(
                borrowed = true, // Mark the connection as borrowed.
                lastBorrowed =
                  connectionState(conn).lastBorrowed, // Don't update since it's not a client asking for it.
                lastCheckIsAlive = connectionState(conn).lastCheckIsAlive // Haven't checked yet if it is alive.
              )
            )
            conn
        }
      }
      maybeConnToCheck match {
        case Some(conn) =>
          logger.debug(s"Checking the connection health for $conn (state: ${connectionUrls(conn)})")
          // Found one connection to check.
          try {
            if (!conn.isClosed() && conn.isValid(isValidSeconds)) {
              logger.debug(s"Connection $conn is healthy")
              // All good, so release borrow.
              // This will update the last check is alive time.
              releaseConnection(conn, isAlive = true)
            } else {
              logger.debug(s"Connection $conn is not healthy; removing it.")
              // Did not validate, so remove it from list.
              actuallyRemoveConnection(conn)
            }
          } catch {
            case NonFatal(t) =>
              // Failed to validate, so also remove from list.
              logger.warn(s"Connection isValid check failed for $conn; removing it.", t)
              actuallyRemoveConnection(conn)
          }
        case None =>
        // No connection to check.
      }
    },
    healthCheckPeriod,
    healthCheckPeriod,
    TimeUnit.MILLISECONDS
  )

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
            connectionState.put(
              conn,
              ConnectionState(
                borrowed = true, // Mark the connection as borrowed.
                lastBorrowed = Instant.now(), // Update the last borrowed time.
                lastCheckIsAlive = connectionState(conn).lastCheckIsAlive // We haven't really checked its state yet.
              )
            )
            return conn
        }

        // If no connection is currently available, just check if this specific db location is maxed out.
        if (maybeConn.isEmpty && conns.size >= maxConnectionsPerDb) {
          // No connection was available to borrow, and too many being used for this db location, so we cannot open
          // any more! We throw with code 08000 (SQLSTATE "connection exception" in SQL standard).
          throw new SQLException("too many connections active", "08000")
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
        throw new SQLException("no connections available", "08000")
      }

      // Create a new connection.
      val conn = createConnection(jdbcUrl)
      connectionCache.get(jdbcUrl) match {
        case Some(conns) => connectionCache.put(jdbcUrl, conns + conn)
        case None => connectionCache.put(jdbcUrl, Set(conn))
      }
      connectionState.put(
        conn,
        ConnectionState(
          borrowed = true, // Mark the connection as borrowed.
          lastBorrowed = Instant.now(), // Set the borrowed time.
          lastCheckIsAlive = Instant.now() // We just created it, so we know it is working.
        )
      )
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
      var oldestConnectionLastBorrow: Instant = null
      connectionState.foreach {
        case (conn, state) =>
          if (
            !state.borrowed && (oldestConnection == null || state.lastBorrowed.isBefore(oldestConnectionLastBorrow))
          ) {
            oldestConnection = conn
            oldestConnectionLastBorrow = state.lastBorrowed
          }
      }

      if (oldestConnection == null) {
        // No connection available to release, so cannot proceed.
        return false
      }

      // Release the oldest connection we found, but only if it is older than the idle timeout.
      // This prevents very "new" connections from being released.
      if (oldestConnectionLastBorrow.isAfter(Instant.now().minusMillis(idleTimeout))) {
        // Connection is "too new" to release.
        false
      } else {
        // Release the connection.
        actuallyRemoveConnection(oldestConnection)
      }
    }
  }

  def actuallyRemoveConnection(conn: SqlConnection): Boolean = {
    connectionPoolLock.synchronized {
      connectionUrls.get(conn) match {
        case Some(jdbcUrl) =>
          logger.debug(s"Actually removing connection $conn")
          try {
            // First try to actually close the connection (note the use of actuallyClose), then clean up all the state.
            conn.actuallyClose()
            connectionState.remove(conn)
            connectionUrls.remove(conn)
            connectionCache.get(jdbcUrl) match {
              case Some(conns) =>
                val nconns = conns - conn
                if (nconns.isEmpty) connectionCache.remove(jdbcUrl)
                else connectionCache.put(jdbcUrl, nconns)
              case None => // Nothing to do.
            }
            true
          } catch {
            case NonFatal(t) =>
              // We failed to actually close the connection.
              logger.warn(s"Failed to actually close the connection $conn", t)
              false
          }
        case None =>
          // Connection didn't exist/was removed already.
          false
      }
    }
  }

  // This does not actually close the underlying connection; just makes it available for 'borrow' by future requests.
  def releaseConnection(conn: SqlConnection, isAlive: Boolean): Unit = {
    connectionPoolLock.synchronized {
      connectionState.put(
        conn,
        ConnectionState(
          borrowed = false, // Release the borrow.
          lastBorrowed = connectionState(conn).lastBorrowed, // No update to borrow time since it wasn't borrowed now.
          lastCheckIsAlive =
            if (isAlive) Instant.now() else connectionState(conn).lastCheckIsAlive // Update if we know it is alive.
        )
      )
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
