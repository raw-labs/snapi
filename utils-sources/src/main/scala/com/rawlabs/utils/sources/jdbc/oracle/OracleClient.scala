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

package com.rawlabs.utils.sources.jdbc.oracle

import oracle.net.ns.NetException
import com.rawlabs.utils.core.RawSettings
import com.rawlabs.utils.sources.jdbc.api._

import java.io.Closeable
import java.net.{ConnectException, SocketTimeoutException, UnknownHostException}
import java.sql.{Connection, DriverManager, SQLException}
import java.sql.Types._
import scala.collection.mutable
import scala.util.matching.Regex
import java.util.Properties
import java.util.concurrent.{Executors, TimeUnit}
import scala.util.control.NonFatal

object OracleClient {
  private val TIMESTAMP_REGEX: Regex = """timestamp\(\d+\)""".r
  private val INTERVAL1_REGEX: Regex = """interval year\(\d+\) to month""".r
  private val INTERVAL2_REGEX: Regex = """interval day\(\d+\) to second\(\d+\)""".r
}

class OracleClient(val hostname: String, val port: Int, dbName: String, username: String, password: String)(
    implicit settings: RawSettings
) extends JdbcClient {

  import OracleClient._

  Class.forName("oracle.jdbc.OracleDriver")

  override val vendor: String = "oracle"

  override val maybeDatabase: Option[String] = Some(dbName)

  override val maybeUsername: Option[String] = Some(username)

  override val maybePassword: Option[String] = Some(password)

  override val connectionString: String = {
    s"jdbc:$vendor:thin:@$hostname:$port:$dbName"
  }

  override def getConnection: Connection = {
    // For connection pool:
    //    wrapSQLException(datasource.getConnection())
    wrapSQLException {
      val props = new Properties()
      maybeUsername.foreach(user => props.setProperty("user", user))
      maybePassword.foreach(passwd => props.setProperty("password", passwd))

      // This property is defined in interface oracle.jdbc.OracleConnection.CONNECTION_PROPERTY_THIN_NET_CONNECT_TIMEOUT
      // see https://docs.oracle.com/cd/E18283_01/appdev.112/e13995/oracle/jdbc/OracleConnection.html#CONNECTION_PROPERTY_THIN_READ_TIMEOUT
      props.setProperty("oracle.net.CONNECT_TIMEOUT", getConnectTimeout(TimeUnit.MILLISECONDS).toString)
      // oracle.jdbc.OracleConnection.CONNECTION_PROPERTY_THIN_READ_TIMEOUT
      props.setProperty("oracle.jdbc.ReadTimeout", getReadTimeout(TimeUnit.MILLISECONDS).toString)
      val conn = DriverManager.getConnection(connectionString, props)
      conn.setNetworkTimeout(Executors.newSingleThreadExecutor(), getNetworkTimeout(TimeUnit.MILLISECONDS).toInt)
      conn
    }
  }

  override def listTables(schema: String): Iterator[String] with Closeable = {
    // Compensate for ORACLE behaviour that requires the schema to be in upper case if it is not quoted.
    val sch = if (schema.startsWith("\"")) schema else schema.toUpperCase()
    super.listTables(sch)
  }

  override def tableMetadata(maybeSchema: Option[String], table: String): TableMetadata = {
    val schema = maybeSchema.get
    val conn = getConnection
    try {
      val stmt = wrapSQLException(conn.createStatement())
      try {
        val rs = wrapSQLException(
          stmt.executeQuery(s"""SELECT column_name, data_type, nullable, data_length, data_precision, data_scale
            |  FROM ALL_TAB_COLUMNS
            |  WHERE UPPER(table_name) = '${table.toUpperCase}' AND UPPER(owner) = '${schema.toUpperCase}'
            |  ORDER BY column_id""".stripMargin)
        )
        val columns = mutable.ListBuffer[TableColumn]()
        var nFields = 0
        while (wrapSQLException(rs.next())) {
          nFields += 1
          val nullable = wrapSQLException(rs.getString("nullable")) == "Y"
          val columnName = wrapSQLException(rs.getString("column_name"))
          val typeName = wrapSQLException(rs.getString("data_type"))
          val columnType = typeName.toLowerCase match {
            case "char" | "nchar" | "varchar2" | "nvarchar2" => JdbcColumnType(VARCHAR, if (nullable) 1 else 0)
            case "float" =>
              // float is a sub-type of number but precision defined in bits instead of digits
              // so going for decimal, even though might be fair to go for double
              JdbcColumnType(DECIMAL, if (nullable) 1 else 0)
            case "number" =>
              val precision = wrapSQLException(rs.getInt("data_precision"))
              val precisionNull = wrapSQLException(rs.wasNull())
              val scale = wrapSQLException(rs.getInt("data_scale"))
              val t =
                if (scale != 0) DECIMAL
                else if (precisionNull) INTEGER
                else if (precision < 5) SMALLINT
                else if (precision < 10) INTEGER
                else if (precision < 20) BIGINT
                else DECIMAL
              JdbcColumnType(t, if (nullable) 1 else 0)
            case "date" => JdbcColumnType(DATE, if (nullable) 1 else 0)
            case "long" => JdbcColumnType(INTEGER, if (nullable) 1 else 0)
            case "binary_float" => JdbcColumnType(REAL, if (nullable) 1 else 0)
            case "binary_double" => JdbcColumnType(DOUBLE, if (nullable) 1 else 0)
            case TIMESTAMP_REGEX() => JdbcColumnType(TIMESTAMP, if (nullable) 1 else 0)
            case INTERVAL1_REGEX() | INTERVAL2_REGEX() => NativeIntervalType(nullable)
            case "raw" => JdbcColumnType(BLOB, if (nullable) 1 else 0)
            case "blob" => JdbcColumnType(BLOB, if (nullable) 1 else 0)
            case _ => UnsupportedColumnType
          }
          columns += TableColumn(columnName, columnType)
        }
        TableMetadata(columns.to, None)
      } finally {
        stmt.close()
      }
    } finally {
      conn.close()
    }
  }

  override def wrapSQLException[T](f: => T): T = {
    try {
      f
    } catch {
      case ex: SQLException => ex.getCause match {
          case inner: NetException => inner.getCause match {
              case _: UnknownHostException => throw new RDBMSUnknownHostException(hostname, ex)
              case _: SocketTimeoutException => throw new RDBMSConnectTimeoutException(hostname, ex)
              case _: ConnectException => throw new RDBMSConnectErrorException(hostname, ex)
            }
          case ex: InterruptedException => throw ex
          case _ =>
            // TODO (ctm): Find documentation of Oracle error codes and check if it is best to map ORA-<errorCode> here.
            if (ex.getErrorCode == 1017) {
              // ORA-01017: invalid username/password; logon denied
              throw new AuthenticationFailedException(ex)
            } else if (ex.getSQLState != null && ex.getSQLState.startsWith("28")) {
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
