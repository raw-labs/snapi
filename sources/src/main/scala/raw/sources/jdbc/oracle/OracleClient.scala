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

package raw.sources.jdbc.oracle

import oracle.net.ns.NetException
import raw.utils.RawSettings
import raw.creds.api.OracleCredential
import raw.sources.jdbc.api._

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
  val timestampRegex: Regex = """timestamp\(\d+\)""".r
  val interval1Regex: Regex = """interval year\(\d+\) to month""".r
  val interval2Regex: Regex = """interval day\(\d+\) to second\(\d+\)""".r
}

class OracleClient(db: OracleCredential)(implicit settings: RawSettings) extends JdbcClient {

  import OracleClient._

  Class.forName("oracle.jdbc.OracleDriver")

  override val vendor: String = "oracle"
  override val connectionString: String = {
    val port = db.port.map(p => ":" + p).getOrElse(":1521")
    s"jdbc:$vendor:thin:@${db.host}$port:${db.database}"
  }

  override val username: Option[String] = db.username
  override val password: Option[String] = db.password

  override val hostname: String = db.host
  override def database: Option[String] = Some(db.database)

  override def getConnection: Connection = {
    // For connection pool:
    //    wrapSQLException(datasource.getConnection())
    wrapSQLException {
      val props = new Properties()
      username.foreach(user => props.setProperty("user", user))
      password.foreach(passwd => props.setProperty("password", passwd))

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

  override def tableMetadata(database: Option[String], maybeSchema: Option[String], table: String): TableMetadata = {
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
            case timestampRegex() => JdbcColumnType(TIMESTAMP, if (nullable) 1 else 0)
            case interval1Regex() | interval2Regex() => NativeIntervalType(nullable)
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
          case int: InterruptedException => throw int
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
      case NonFatal(t) =>
        logger.warn("Unexpected SQL error.", t)
        throw new JdbcLocationException(s"unexpected database error", t)
    }
  }

}
