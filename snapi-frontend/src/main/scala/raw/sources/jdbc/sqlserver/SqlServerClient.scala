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

package raw.sources.jdbc.sqlserver

import com.microsoft.sqlserver.jdbc.SQLServerException

import java.util.concurrent.TimeUnit
import raw.creds.api.SqlServerCredential
import raw.sources.jdbc.api._
import raw.utils.RawSettings

import java.net.{SocketTimeoutException, UnknownHostException}
import scala.util.control.NonFatal

class SqlServerClient(protected val db: SqlServerCredential)(implicit settings: RawSettings) extends JdbcClient {

  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")

  // Note that different properties require the time to be specified in different timescales.
  // The settings used below are described in:
  //   https://docs.microsoft.com/en-us/sql/connect/jdbc/setting-the-connection-properties?view=sql-server-2017
  private val connectTimeout = getConnectTimeout(TimeUnit.SECONDS)
  private val readTimeout = getReadTimeout(TimeUnit.MILLISECONDS)

  override val vendor: String = "sqlserver"
  override val connectionString: String = {
    val maybePort = db.port.map(p => ":" + p).getOrElse("")
    // explicit sendTimeAsDatetime=false to support time JDBC parameters (https://github.com/microsoft/mssql-jdbc/issues/559)
    s"jdbc:$vendor://${db.host}$maybePort;databaseName=${db.database};loginTimeout=$connectTimeout;socketTimeout=$readTimeout;sendTimeAsDatetime=false"
  }
  override val username: Option[String] = db.username
  override val password: Option[String] = db.password

  override val hostname: String = db.host
  override val database: Option[String] = Some(db.database)
  override def wrapSQLException[T](f: => T): T = {
    try {
      f
    } catch {
      // TODO: check SqlServer puts exceptions, codes etc.
      case ex: SQLServerException => ex.getCause match {
          case _: UnknownHostException => throw new RDBMSUnknownHostException(hostname, ex)
          case _: SocketTimeoutException => throw new RDBMSConnectTimeoutException(hostname, ex)
          case int: InterruptedException => throw int
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
      case NonFatal(t) =>
        logger.warn("Unexpected SQL error.", t)
        throw new JdbcLocationException(s"unexpected database error", t)
    }
  }

}
