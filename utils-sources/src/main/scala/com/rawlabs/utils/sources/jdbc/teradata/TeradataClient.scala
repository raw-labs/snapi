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

package com.rawlabs.utils.sources.jdbc.teradata

import com.rawlabs.utils.core.RawSettings
import com.rawlabs.utils.sources.jdbc.api._

import java.net.{NoRouteToHostException, SocketTimeoutException, UnknownHostException}
import java.sql.{Connection, DriverManager, ResultSetMetaData, SQLException}
import scala.collection.mutable
import scala.util.control.NonFatal

class TeradataClient(
    val hostname: String,
    val port: Int,
    dbName: String,
    username: String,
    password: String,
    val parameters: Map[String, String]
)(
    implicit settings: RawSettings
) extends JdbcClient {

  Class.forName("com.teradata.jdbc.TeraDriver")

  override val vendor: String = "teradata"

  override val maybeDatabase: Option[String] = Some(dbName)

  override val maybeUsername: Option[String] = Some(username)

  override val maybePassword: Option[String] = Some(password)

  override val connectionString: String = {
    val params = parameters + ("DBS_PORT" -> port.toString)
    s"jdbc:$vendor://$hostname/${params.map(p => s"${p._1}=${p._2}").mkString(",")}"
  }

  override def getConnection: Connection = {
    wrapSQLException {
      // Teradata jdbc connections does not have the setNetworkTimeout
      DriverManager.getConnection(connectionString, maybeUsername.orNull, maybePassword.orNull)
    }
  }

  override def tableMetadata(maybeSchema: Option[String], table: String): TableMetadata = {
    val schema = maybeSchema.get
    val conn = getConnection
    try {
      val query = s"""select top 1 * from "$schema"."$table" ;"""
      val stmt = wrapSQLException(conn.prepareStatement(query))
      val meta = wrapSQLException(stmt.getMetaData)
      wrapSQLException(stmt.cancel())
      getTableTypeFromResultSetMetadata(meta)
    } finally {
      conn.close()
    }
  }

  private def getTableTypeFromResultSetMetadata(res: ResultSetMetaData): TableMetadata = {
    val columns = mutable.ListBuffer[TableColumn]()
    (1 to wrapSQLException(res.getColumnCount)).foreach { n =>
      val columnName = wrapSQLException(res.getColumnName(n))
      val columnType = wrapSQLException(res.getColumnType(n))
      val nullability = wrapSQLException(res.isNullable(n))
      columns += TableColumn(columnName, JdbcColumnType(columnType, nullability))
    }
    TableMetadata(columns.to, None)
  }

  override def wrapSQLException[T](f: => T): T = {
    try {
      f
    } catch {
      // TODO (ctm): check Teradata exceptions
      case ex: SQLException => ex.getCause match {
          case _: UnknownHostException | _: NoRouteToHostException =>
            // (ctm) In the Python CLI tests, the NoRouteToHostException also happens in the test with bad port
            // RuntimeErrorsSourceTeradataTestCase.test_register_bad_port_timeout the host is correct but the port is wrong.
            throw new RDBMSUnknownHostException(hostname, ex)
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
