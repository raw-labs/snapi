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

package com.rawlabs.utils.sources.jdbc.sqlite

import com.rawlabs.utils.sources.api.LocationException
import com.rawlabs.utils.sources.jdbc.api._
import com.rawlabs.utils.core.RawSettings

import java.nio.file.{InvalidPathException, Path, Paths}
import java.sql.SQLException
import scala.util.control.NonFatal

class SqliteClient(val path: String)(implicit settings: RawSettings) extends JdbcClient {

  private val localPath =
    try {
      Paths.get(path)
    } catch {
      case _: InvalidPathException => throw new LocationException("invalid path")
    }

  // The JDBC driver requires a local path.
  // Opted to validate here instead of having constructor take a Path and force all the callers to valid the path,
  // so that we have more coherent error handling.
  // This way, if there is any error with the "connection string" (the path in this case), we throw early an exception.
  val sqlitePath: Path =
    try {
      localPath.toAbsolutePath
    } catch {
      case ex: InvalidPathException => throw new JdbcLocationException(s"invalid local path: $localPath", ex)
    }

  Class.forName("org.sqlite.JDBC")

  override val vendor: String = "sqlite"

  override val maybeUsername: Option[String] = None

  override val maybePassword: Option[String] = None

  override val maybeDatabase: Option[String] = None

  override val connectionString: String = s"jdbc:$vendor:$sqlitePath"

  override val hostname: String = localPath.toAbsolutePath.toString

  override def wrapSQLException[T](f: => T): T = {
    try {
      f
    } catch {
      // TODO (ctm): check Sqlite exceptions
      case ex: SQLException => ex.getCause match {
          case ex: InterruptedException => throw ex
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
      case ex: InterruptedException => throw ex
      case NonFatal(t) => throw new JdbcLocationException(s"unexpected database error", t)
    }
  }

}
