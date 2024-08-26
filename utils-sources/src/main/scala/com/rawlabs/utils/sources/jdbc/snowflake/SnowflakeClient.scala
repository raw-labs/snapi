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

package com.rawlabs.utils.sources.jdbc.snowflake

import net.snowflake.client.jdbc.SnowflakeSQLException
import net.snowflake.client.jdbc.internal.snowflake.common.core.SqlState
import com.rawlabs.utils.sources.jdbc.api._
import com.rawlabs.utils.core.RawSettings

import java.sql.{Connection, DriverManager}
import java.util.concurrent.{Executors, TimeUnit}
import java.util.{Properties, TimeZone}
import scala.util.control.NonFatal

class SnowflakeClient(
    dbName: String,
    username: String,
    password: String,
    val accountIdentifier: String,
    val parameters: Map[String, String]
)(
    implicit settings: RawSettings
) extends JdbcClient {

  Class.forName("net.snowflake.client.jdbc.SnowflakeDriver")

  override val hostname: String = s"$accountIdentifier.snowflakecomputing.com"

  override val vendor: String = "snowflake"

  override val maybeDatabase: Option[String] = Some(dbName)

  override val maybeUsername: Option[String] = Some(username)

  override val maybePassword: Option[String] = Some(password)

  override val connectionString: String = s"jdbc:snowflake://$hostname/"

  override def getConnection: Connection = {
    wrapSQLException {
      val params = parameters ++ Seq("db" -> dbName)
      val props = new Properties()
      maybeUsername.foreach(user => props.setProperty("user", user))
      maybePassword.foreach(passwd => props.setProperty("password", passwd))
      props.setProperty("JDBC_QUERY_RESULT_FORMAT", "JSON")

      for ((key, value) <- params) props.setProperty(key, value)

      // (CTM) I am having issues with sql.Time with timezones. I am seeing a shift if the timezone was not set to UTC.
      TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
      logger.info(s"current timezone ${TimeZone.getDefault}")
      val conn = DriverManager.getConnection(connectionString, props)
      conn.setNetworkTimeout(Executors.newSingleThreadExecutor(), getNetworkTimeout(TimeUnit.MILLISECONDS).toInt)
      conn
    }
  }

  override def tableMetadata(maybeSchema: Option[String], table: String): TableMetadata = {
    val conn = getConnection
    try {
      val res = getTableMetadata(conn, None, maybeSchema, table)
      try {
        getTableTypeFromTableMetadata(res)
      } finally {
        wrapSQLException(res.close())
      }
    } finally {
      wrapSQLException(conn.close())
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
              s"IO error connecting to $accountIdentifier: ${ex.getMessage}",
              ex
            )
          case SqlState.SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION => throw new JdbcLocationException(
              s"unable to establish connection to $accountIdentifier: ${ex.getMessage}",
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
      case ex: InterruptedException => throw ex
      case NonFatal(t) => throw new JdbcLocationException(s"unexpected database error", t)
    }
  }
}
