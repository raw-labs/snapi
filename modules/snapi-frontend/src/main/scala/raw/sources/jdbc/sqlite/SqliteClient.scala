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

package raw.sources.jdbc.sqlite

import raw.sources.jdbc.api._
import raw.client.utils.RawSettings

import java.nio.file.{InvalidPathException, Path}
import java.sql.SQLException
import scala.util.control.NonFatal

class SqliteClient(path: Path)(implicit settings: RawSettings) extends JdbcClient {

  Class.forName("org.sqlite.JDBC")

  /**
   * The JDBC driver requires a local path. Opted to validate here instead of having constructor take a Path and
   * force all the callers to valid the path, so that we have more coherent error handling. This way, if there is
   * any error with the "connection string" (the path), we throw the proper exception.
   * It is possible the "path" is a RAW URI to the local file, when the reader/writer exp is coming from
   * readOrCacheCreate
   */
  val sqlitePath: Path =
    try {
      path.toAbsolutePath
    } catch {
      case ex: InvalidPathException => throw new JdbcLocationException(s"invalid local path: $path", ex)
    }

  override val vendor: String = "sqlite"
  override val connectionString: String = s"jdbc:$vendor:$sqlitePath"
  override val username: Option[String] = None
  override val password: Option[String] = None
  override val database: Option[String] = None

  override val hostname: String = path.toAbsolutePath.toString

  override def wrapSQLException[T](f: => T): T = {
    try {
      f
    } catch {
      // TODO (ctm): check Sqlite exceptions
      case ex: SQLException => ex.getCause match {
          case int: InterruptedException => throw int
          case _ =>
            // Some more codes here (DB2 Universal Messages manual), various databases have varying degrees of compliance
            //https://www.ibm.com/support/knowledgecenter/en/SS6NHC/com.ibm.swg.im.dashdb.messages.doc/doc/rdb2stt.html
            if (ex.getSQLState != null && ex.getSQLState.startsWith("28")) {
              throw new AuthenticationFailedException(ex)
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
