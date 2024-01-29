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

package raw.sources.jdbc.snowflake

import net.snowflake.client.jdbc.SnowflakeSQLException
import net.snowflake.client.jdbc.internal.snowflake.common.core.SqlState
import raw.creds.api.SnowflakeCredential
import raw.sources.jdbc.api._
import raw.utils.RawSettings

import java.sql.{Connection, DriverManager}
import java.util.concurrent.{Executors, TimeUnit}
import java.util.{Properties, TimeZone}
import scala.util.control.NonFatal

class SnowflakeClient(db: SnowflakeCredential)(implicit settings: RawSettings) extends JdbcClient {

  Class.forName("net.snowflake.client.jdbc.SnowflakeDriver")

  override val vendor: String = "snowflake"

  override val connectionString: String = s"jdbc:snowflake://${db.host}/"

  override val username: Option[String] = db.username
  override val password: Option[String] = db.password

  override val hostname: String = db.host
  override val database: String = db.database
  override def getConnection: Connection = {
    wrapSQLException {
      val parameters = db.parameters ++ Seq("db" -> db.database)
      val props = new Properties()
      username.foreach(user => props.setProperty("user", user))
      password.foreach(passwd => props.setProperty("password", passwd))
      props.setProperty("JDBC_QUERY_RESULT_FORMAT", "JSON")

      for ((key, value) <- parameters) props.setProperty(key, value)

      // (CTM) I am having issues with sql.Time with timezones. I am seeing a shift if the timezone was not set to UTC.
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
      logger.info(s"current timezone ${TimeZone.getDefault}")
      val conn = DriverManager.getConnection(connectionString, props)
      conn.setNetworkTimeout(Executors.newSingleThreadExecutor(), getNetworkTimeout(TimeUnit.MILLISECONDS).toInt)
      conn
    }
  }

  override def wrapSQLException[T](f: => T): T = {
    try {
      f
    } catch {
      case ex: JdbcLocationException => throw ex
      case ex: SnowflakeSQLException =>
        val e = ex
        e.getSQLState match {
          case SqlState.INVALID_PASSWORD | SqlState.INVALID_AUTHORIZATION_SPECIFICATION =>
            throw new AuthenticationFailedException(ex)
          case SqlState.IO_ERROR => throw new JdbcLocationException(
              s"IO error connecting to ${db.accountIdentifier}: ${ex.getMessage}",
              ex
            )
          case SqlState.SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION => throw new JdbcLocationException(
              s"unable to establish connection to ${db.accountIdentifier}: ${ex.getMessage}",
              ex
            )
          case SqlState.CONNECTION_EXCEPTION | SqlState.CONNECTION_FAILURE =>
            throw new RDBMSConnectErrorException(hostname, ex)
          case SqlState.SYSTEM_ERROR => throw new JdbcLocationException(s"database system error: ${ex.getMessage}", ex)
          case SqlState.FEATURE_NOT_SUPPORTED =>
            throw new JdbcLocationException(s"database feature not supported: ${ex.getMessage}", ex)
          case _ =>
            logger.warn(s"Unexpected SQL error (code: ${ex.getErrorCode}; state: ${ex.getSQLState}).", ex)
            throw new JdbcLocationException(ex.getMessage, ex)
        }

      case NonFatal(t) =>
        logger.warn("Unexpected SQL error.", t)
        throw new JdbcLocationException(s"unexpected database error", t)
    }
  }
}
