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

package com.rawlabs.utils.sources.jdbc.sqlserver

import com.microsoft.sqlserver.jdbc.SQLServerException

import java.util.concurrent.TimeUnit
import com.rawlabs.utils.sources.jdbc.api._
import com.rawlabs.utils.core.RawSettings

import java.net.{SocketTimeoutException, UnknownHostException}
import scala.util.control.NonFatal

class SqlServerClient(val hostname: String, val port: Int, dbName: String, username: String, password: String)(
    implicit settings: RawSettings
) extends JdbcClient {

  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")

  // Note that different properties require the time to be specified in different timescales.
  // The settings used below are described in:
  //   https://docs.microsoft.com/en-us/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-2017
  private val connectTimeout = getConnectTimeout(TimeUnit.SECONDS)
  private val readTimeout = getReadTimeout(TimeUnit.MILLISECONDS)

  override val vendor: String = "sqlserver"

  override val maybeDatabase: Option[String] = Some(dbName)

  override val maybeUsername: Option[String] = Some(username)

  override val maybePassword: Option[String] = Some(password)

  override val connectionString: String = {
    // Explicit sendTimeAsDatetime=false to support time JDBC parameters (https://github.com/microsoft/mssql-jdbc/issues/559)
    s"jdbc:$vendor://$hostname:$port;databaseName=$dbName;loginTimeout=$connectTimeout;socketTimeout=$readTimeout;sendTimeAsDatetime=false"
  }

  override def wrapSQLException[T](f: => T): T = {
    try {
      f
    } catch {
      // TODO: check SqlServer puts exceptions, codes etc.
      case ex: SQLServerException => ex.getCause match {
          case _: UnknownHostException => throw new RDBMSUnknownHostException(hostname, ex)
          case _: SocketTimeoutException => throw new RDBMSConnectTimeoutException(hostname, ex)
          case ex: InterruptedException => throw ex
          case _ =>
            // Some more codes here (DB2 Universal Messages manual), various databases have varying degrees of compliance
            //https://www.ibm.com/support/knowledgecenter/en/SS6NHC/com.ibm.swg.im.dashdb.messages.doc/doc/rdb2stt.html
            if (ex.getSQLState != null && ex.getSQLState.startsWith("28")) {
              throw new AuthenticationFailedException(ex)
            } else if (ex.getSQLState != null && ex.getSQLState.startsWith("08")) {
              throw new RDBMSConnectErrorException(hostname, ex)
            } else if (ex.getSQLState != null && ex.getSQLState.startsWith("58")) {
              throw new JdbcLocationException(s"database system error: ${ex.getMessage}", ex)
            } else if (ex.getSQLState != null && ex.getSQLState.startsWith("0A")) {
              throw new JdbcLocationException(s"database feature not supported: ${ex.getMessage}", ex)
            } else if (ex.getSQLState != null && ex.getSQLState.startsWith("2E")) {
              throw new JdbcLocationException(s"database invalid connection name: ${ex.getMessage}", ex)
            } else {
              logger.warn(s"Unexpected SQL error (code: ${ex.getErrorCode}; state: ${ex.getSQLState}).", ex)
              throw new JdbcLocationException(ex.getMessage, ex)
            }
        }
      case ex: JdbcLocationException => throw ex
      case ex: InterruptedException => throw ex
      case NonFatal(t) => throw new JdbcLocationException(s"unexpected database error", t)
    }
  }

}
