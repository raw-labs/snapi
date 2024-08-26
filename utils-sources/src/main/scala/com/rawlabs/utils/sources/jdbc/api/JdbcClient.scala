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

package com.rawlabs.utils.sources.jdbc.api

import java.io.Closeable
import java.sql.{Connection, DriverManager, ResultSet}
import java.util.concurrent.{Executors, TimeUnit}
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.utils.core.RawSettings

import scala.collection.mutable

// TODO (msb): Add relevant attributes.
final case class ServerMetadata()

// TODO (msb): Add relevant attributes.
final case class SchemaMetadata()

// TODO (msb): Add histogram info of columns? map? max/min?
final case class TableMetadata(columns: List[TableColumn], cardinality: Option[Long])

final case class TableColumn(name: String, t: TableColumnType)

sealed trait TableColumnType

final case class JdbcColumnType(jdbcType: Int, jdbcNullability: Int) extends TableColumnType

/**
 * JDBC does not have an interval type in the specification, so we add ours here.
 * This means the connector must know how to extract the type and cast it to our interval type expectation.
 */
final case class NativeIntervalType(nullable: Boolean) extends TableColumnType

case object UnsupportedColumnType extends TableColumnType

object JdbcClient {
  private val CONNECT_TIMEOUT = "raw.utils.sources.rdbms.connect-timeout"
  private val READ_TIMEOUT = "raw.utils.sources.rdbms.read-timeout"
  private val NETWORK_TIMEOUT = "raw.utils.sources.rdbms.network-timeout"
  private val LOGIN_TIMEOUT = "raw.utils.sources.rdbms.login-timeout"
}

abstract class JdbcClient()(implicit settings: RawSettings) extends StrictLogging {

  import JdbcClient._

  DriverManager.setLoginTimeout(getLoginTimeout(TimeUnit.SECONDS).toInt)

  def hostname: String

  def vendor: String

  // Database is optional because some databases do not have the concept of database (Teradata and Sqlite).
  def maybeDatabase: Option[String]

  // Wrap vendor-specific calls and ensure only RelationalDatabaseException is thrown.
  def wrapSQLException[T](f: => T): T

  def connectionString: String

  // For connection pool:
  //  private lazy val datasource = {
  //    val connectionFactory = new DriverManagerConnectionFactory(connectionString, username.orNull, password.orNull)
  //    val poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null)
  //    val connectionPool = new GenericObjectPool(poolableConnectionFactory)
  //    poolableConnectionFactory.setPool(connectionPool)
  //    new PoolingDataSource(connectionPool)
  //  }

  def maybeUsername: Option[String]

  def maybePassword: Option[String]

  def getConnection: Connection = {
    // For connection pool:
    //    wrapSQLException(datasource.getConnection())
    wrapSQLException {
      val conn = DriverManager.getConnection(connectionString, maybeUsername.orNull, maybePassword.orNull)
      conn.setNetworkTimeout(Executors.newSingleThreadExecutor(), getNetworkTimeout(TimeUnit.MILLISECONDS).toInt)
      conn
    }
  }

  def testAccess(): Unit = {
    val conn = getConnection
    try {
      // Nothing more to do.
    } finally {
      wrapSQLException(conn.close())
    }
  }

  def testAccess(schema: String): Unit = {
    listTables(schema).close()
  }

  def testAccess(maybeSchema: Option[String], table: String): Unit = {
    tableMetadata(maybeSchema, table)
  }

  def listSchemas: Iterator[String] with Closeable = {
    new JdbcRelationalDatabaseSchemas
  }

  def listTables(schema: String): Iterator[String] with Closeable = {
    new JdbcRelationalDatabaseTables(schema)
  }

  def serverMetadata: ServerMetadata = {
    ServerMetadata()
  }

  def schemaMetadata(schema: String): SchemaMetadata = {
    SchemaMetadata()
  }

  def tableMetadata(maybeSchema: Option[String], table: String): TableMetadata = {
    val conn = getConnection
    try {
      val res = getTableMetadata(conn, maybeDatabase, maybeSchema, table)
      try {
        getTableTypeFromTableMetadata(res)
      } finally {
        wrapSQLException(res.close())
      }
    } finally {
      wrapSQLException(conn.close())
    }
  }

  protected def getTableMetadata(
      conn: Connection,
      maybeDatabase: Option[String],
      maybeSchema: Option[String],
      table: String
  ): ResultSet = {
    wrapSQLException {
      val metaData = conn.getMetaData
      metaData.getColumns(
        maybeDatabase.orNull,
        maybeSchema.orNull,
        table,
        null // Read all columns
      )
    }
  }

  // Infer schema from table.
  // Skip silently fields we do not understand (except if can't understand any field, in which case, fire an error.)
  protected def getTableTypeFromTableMetadata(res: ResultSet): TableMetadata = {
    val columns = mutable.ListBuffer[TableColumn]()
    while (wrapSQLException(res.next)) {
      val columnName = wrapSQLException(res.getString("COLUMN_NAME"))
      val columnType = wrapSQLException(res.getInt("DATA_TYPE"))
      val nullability = wrapSQLException(res.getInt("NULLABLE"))
      columns += TableColumn(columnName, JdbcColumnType(columnType, nullability))
    }
    TableMetadata(columns.to, None)
  }

  final protected def getConnectTimeout(timeUnit: TimeUnit): Long = settings.getDuration(CONNECT_TIMEOUT, timeUnit)

  final protected def getReadTimeout(timeUnit: TimeUnit): Long = settings.getDuration(READ_TIMEOUT, timeUnit)

  final protected def getNetworkTimeout(timeUnit: TimeUnit): Long = settings.getDuration(NETWORK_TIMEOUT, timeUnit)

  final protected def getLoginTimeout(timeUnit: TimeUnit): Long = settings.getDuration(LOGIN_TIMEOUT, timeUnit)

  class JdbcRelationalDatabaseSchemas extends Iterator[String] with Closeable {

    private val conn = getConnection

    private val metadata = wrapSQLException(conn.getMetaData)

    private val rs = wrapSQLException {
      metadata.getSchemas(
        null, // TODO (msb): Should restrict to catalog/database in server connection string
        "%"
      )
    }

    override def hasNext: Boolean = {
      val hasNext = wrapSQLException(rs.next())
      if (!hasNext) {
        wrapSQLException(conn.close())
      }
      hasNext
    }

    override def next(): String = {
      wrapSQLException(rs.getString("TABLE_SCHEM"))
    }

    override def close(): Unit = {
      wrapSQLException(conn.close())
    }
  }

  class JdbcRelationalDatabaseTables(schema: String) extends Iterator[String] with Closeable {

    private val conn = getConnection

    private val metadata = wrapSQLException(conn.getMetaData)

    private val rs = wrapSQLException {
      metadata.getTables(
        null, // TODO (msb): Should restrict to catalog/database in server connection string
        schema,
        "%",
        null
      )
    }

    override def hasNext: Boolean = {
      val hasNext = wrapSQLException(rs.next())
      if (!hasNext) {
        wrapSQLException(conn.close())
      }
      hasNext
    }

    override def next(): String = {
      wrapSQLException(rs.getString("TABLE_NAME"))
    }

    override def close(): Unit = {
      wrapSQLException(conn.close())
    }

  }

}
