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

package raw.client.sql

import com.google.common.cache.{CacheBuilder, CacheLoader}
import raw.client.api._
import raw.client.sql.SqlCodeUtils._
import raw.client.sql.writers.{TypedResultSetCsvWriter, TypedResultSetJsonWriter}
import raw.utils.{AuthenticatedUser, RawSettings, RawUtils}

import java.io.{IOException, OutputStream}
import java.sql.{ResultSet, SQLException, SQLTimeoutException}
import scala.collection.mutable

class SqlCompilerService(maybeClassLoader: Option[ClassLoader] = None)(implicit protected val settings: RawSettings)
    extends CompilerService {

  override def language: Set[String] = Set("sql")
  override def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse = {
    logger.debug(s"Getting program description: $source")
    try {
      val conn = connectionPool.getConnection(environment.user)
      try {
        val stmt = new NamedParametersPreparedStatement(conn, source)
        val description = stmt.queryMetadata match {
          case Right(info) =>
            val parameters = info.parameters
            val tableType = info.outputType
            val description = {
              // Regardless if there are parameters, we declare a main function with the output type.
              // This permits the publish endpoints from the UI (https://raw-labs.atlassian.net/browse/RD-10359)
              val ps = for ((name, rawType) <- parameters) yield {
                ParamDescription(name, rawType, Some(RawNull()), false)
              }
              ProgramDescription(
                Map("main" -> List(DeclDescription(Some(ps.toVector), tableType, None))),
                None,
                None
              )
            }
            GetProgramDescriptionSuccess(description)
          case Left(errors) => GetProgramDescriptionFailure(errors)
        }
        stmt.close()
        description
      } catch {
        case e: SQLException => GetProgramDescriptionFailure(mkError(source, e))
      } finally {
        conn.close()
      }
    } catch {
      case e: SQLException => GetProgramDescriptionFailure(mkError(source, e))
      case e: SQLTimeoutException => GetProgramDescriptionFailure(mkError(source, e))
    }
  }

  private def mkError(source: String, exception: SQLException): List[ErrorMessage] = {
    val message = exception.getMessage
    logger.warn(message, exception)
    mkError(source, message)
  }

  private def mkError(source: String, message: String): List[ErrorMessage] = {
    val fullRange = {
      val lines = source.split("\n")
      val nLines = lines.length
      val lastLine = lines.last
      val lastLineLength = lastLine.length
      ErrorRange(ErrorPosition(1, 1), ErrorPosition(nLines, lastLineLength + 1))
    }
    List(ErrorMessage(message, List(fullRange)))
  }

  override def eval(source: String, tipe: RawType, environment: ProgramEnvironment): EvalResponse = {
    ???
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse = {
    logger.debug(s"Executing: $source")
    try {
      val conn = connectionPool.getConnection(environment.user)
      try {
        val pstmt = new NamedParametersPreparedStatement(conn, source)
        try {
          pstmt.queryMetadata match {
            case Right(info) =>
              try {
                val tipe = info.outputType
                environment.maybeArguments.foreach(array => setParams(pstmt, array))
                val r = pstmt.executeQuery()
                render(environment, tipe, r, outputStream)
              } catch {
                case e: SQLException => ExecutionRuntimeFailure(e.getMessage)
              }
            case Left(errors) => ExecutionValidationFailure(errors)
          }
        } finally {
          RawUtils.withSuppressNonFatalException(pstmt.close())
        }
      } catch {
        case e: SQLException => ExecutionValidationFailure(mkError(source, e))
      } finally {
        conn.close()
      }
    } catch {
      case e: SQLException => ExecutionRuntimeFailure(e.getMessage)
      case e: SQLTimeoutException => ExecutionRuntimeFailure(e.getMessage)
    }
  }

  private def render(
      environment: ProgramEnvironment,
      tipe: RawType,
      v: ResultSet,
      outputStream: OutputStream
  ): ExecutionResponse = {
    environment.options
      .get("output-format")
      .map(_.toLowerCase) match {
      case Some("csv") =>
        if (!TypedResultSetCsvWriter.outputWriteSupport(tipe)) {
          ExecutionRuntimeFailure("unsupported type")
        }
        val windowsLineEnding = environment.options.get("windows-line-ending") match {
          case Some("true") => true
          case _ => false //settings.config.getBoolean("raw.compiler.windows-line-ending")
        }
        val lineSeparator = if (windowsLineEnding) "\r\n" else "\n"
        val csvWriter = new TypedResultSetCsvWriter(outputStream, lineSeparator)
        try {
          csvWriter.write(v, tipe)
          ExecutionSuccess
        } catch {
          case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
        } finally {
          RawUtils.withSuppressNonFatalException(csvWriter.close())
        }
      case Some("json") =>
        if (!TypedResultSetJsonWriter.outputWriteSupport(tipe)) {
          ExecutionRuntimeFailure("unsupported type")
        }
        val w = new TypedResultSetJsonWriter(outputStream)
        try {
          w.write(v, tipe)
          ExecutionSuccess
        } catch {
          case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
        } finally {
          RawUtils.withSuppressNonFatalException(w.close())
        }
      case _ => ExecutionRuntimeFailure("unknown output format")
    }

  }

  private def setParams(statement: NamedParametersPreparedStatement, tuples: Array[(String, RawValue)]): Unit = {
    tuples.foreach { tuple =>
      try {
        tuple match {
          case (p, RawNull()) => statement.setNull(p)
          case (p, RawByte(v)) => statement.setByte(p, v)
          case (p, RawShort(v)) => statement.setShort(p, v)
          case (p, RawInt(v)) => statement.setInt(p, v)
          case (p, RawLong(v)) => statement.setLong(p, v)
          case (p, RawFloat(v)) => statement.setFloat(p, v)
          case (p, RawDouble(v)) => statement.setDouble(p, v)
          case (p, RawBool(v)) => statement.setBoolean(p, v)
          case (p, RawString(v)) => statement.setString(p, v)
          case (p, RawDecimal(v)) => statement.setBigDecimal(p, v)
          case (p, RawDate(v)) => statement.setDate(p, java.sql.Date.valueOf(v))
          case (p, RawTime(v)) => statement.setTime(p, java.sql.Time.valueOf(v))
          case (p, RawTimestamp(v)) => statement.setTimestamp(p, java.sql.Timestamp.valueOf(v))
          case (p, RawInterval(years, months, weeks, days, hours, minutes, seconds, millis)) => ???
          case (p, RawBinary(v)) => statement.setBytes(p, v)
          case _ => ???
        }
      } catch {
        case e: NoSuchElementException => logger.warn("Unknown parameter: " + e.getMessage)
      }
    }
  }

  override def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  ): FormatCodeResponse = {
    FormatCodeResponse(Some(source))
  }

  override def dotAutoComplete(source: String, environment: ProgramEnvironment, position: Pos): AutoCompleteResponse = {
    logger.debug(s"dotAutocompleting at position: $position")
    val analyzer = new SqlCodeUtils(source)
    // The editor removes the dot in the this completion event
    // So we call the identifier with +1 column
    val idns = analyzer.getIdentifierUpTo(Pos(position.line, position.column + 1))

    val schemas = getSchemas(environment)

    // check if we found a schema
    val maybeSchema =
      schemas.find { case (name, _) => compareIdentifiers(idns, Seq(SqlIdentifier(name, quoted = true))) }

    val matches = maybeSchema match {
      case Some((_, tables)) => tables.keys.map(name => LetBindCompletion(name, "table"))
      case None => for (
          tables <- schemas.values; (table, columns) <- tables;
          if compareIdentifiers(Seq(SqlIdentifier(table, quoted = true)), idns); (name, tipe) <- columns
        ) yield {
          LetBindCompletion(name, tipe)
        }
    }
    logger.debug(s"idns $idns matches: $matches")
    AutoCompleteResponse(matches.toArray)
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    logger.debug(s"wordAutocompleting at position: $position")
    val scope = getFullyQualifiedScope(environment)
    val analyzer = new SqlCodeUtils(source)
    val idns = analyzer.getIdentifierUpTo(position)
    logger.debug(s"idns $idns")

    val matches = scope.map { case (key, value) => (matchKey(key, idns), value) }
    val collectedValues = matches.collect { case (Some(word), value) => LetBindCompletion(word, value) }
    logger.debug(s"values $collectedValues")
    AutoCompleteResponse(collectedValues.toArray)
  }

  private def matchKey(key: Seq[SqlIdentifier], idns: Seq[SqlIdentifier]): Option[String] = {
    if (key.length != idns.length) return None
    // compare all identifiers except the last one
    if (!compareIdentifiers(key.take(key.length - 1), idns.take(idns.length - 1))) return None

    // if all intermediate identifiers match now check the key contains the last identifier
    val lastIdn = idns.last
    val lastKey = key.last
    val matches =
      if (lastIdn.quoted && lastKey.quoted) {
        // case sensitive match
        lastKey.value.startsWith(lastIdn.value)
      } else {
        // case insensitive
        lastKey.value.toLowerCase.startsWith(lastIdn.value.toLowerCase())
      }

    if (!matches) return None
    Some(lastKey.value)
  }
  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    logger.debug(s"Hovering at position: $position")
    val analyzer = new SqlCodeUtils(source)
    val idns = analyzer.getIdentifierUnder(position)
    val scope = getFullyQualifiedScope(environment)
    scope
      .find(x => SqlCodeUtils.compareIdentifiers(x._1, idns))
      .map { case (names, tipe) => HoverResponse(Some(TypeCompletion(formatIdns(names), tipe))) }
      .getOrElse(HoverResponse(None))
  }

  def formatIdns(idns: Seq[SqlIdentifier]): String = {
    idns.tail.foldLeft(idns.head.value) { case (acc, idn) => acc + "." + idn.value }
  }
  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    RenameResponse(Array.empty)
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    GoToDefinitionResponse(None)
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    logger.debug(s"Validating: $source")
    val r =
      try {
        val conn = connectionPool.getConnection(environment.user)
        try {
          val stmt = new NamedParametersPreparedStatement(conn, source)
          val result = stmt.queryMetadata match {
            case Right(_) => ValidateResponse(List.empty)
            case Left(errors) => ValidateResponse(errors)
          }
          stmt.close()
          result
        } catch {
          case e: SQLException => ValidateResponse(mkError(source, e))
        } finally {
          conn.close()
        }
      } catch {
        case e: SQLException => ValidateResponse(mkError(source, e))
        case e: SQLTimeoutException => ValidateResponse(mkError(source, e))
      }
    r
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    ValidateResponse(List.empty)
  }

  private def getFullyQualifiedScope(environment: ProgramEnvironment): Map[Seq[SqlIdentifier], String] = {
    val schema = getSchemas(environment)
    val items = mutable.Map.empty[Seq[SqlIdentifier], String]
    for ((schemaName, tables) <- schema) {
      // We are assuming that the values from JDBC are case sensitive so "quoted"
      val schemaIdn = SqlIdentifier(schemaName, quoted = true)
      for ((tableName, columns) <- tables) {
        val tableIdn = SqlIdentifier(tableName, quoted = true)
        for ((columnName, tipe) <- columns) {
          val columnIdn = SqlIdentifier(columnName, quoted = true)
          // Columns are available as: column, table.column, schema.table.column
          items(Seq(columnIdn)) = tipe
          items(Seq(tableIdn, columnIdn)) = tipe
          items(Seq(schemaIdn, tableIdn, columnIdn)) = tipe
        }
        // Tables are available as: table, schema.table. If such key overlaps with a column one, it takes precedence (stored last)
        items(Seq(tableIdn)) = "table"
        items(Seq(schemaIdn, tableIdn)) = "table"
      }
      // schemas as stored last. If there's a table or a column with the same name, it takes precedence
      items(Seq(schemaIdn)) = "schema"
    }
    items.toMap
  }

  private type Schema = Map[String, Map[String, String]]

  private def getSchemas(environment: ProgramEnvironment): Map[String, Schema] = schemaCache.get(environment.user)

  private val loader = new CacheLoader[AuthenticatedUser, Map[String, Schema]] {
    override def load(key: AuthenticatedUser): Map[String, Schema] =
      try {
        val conn = connectionPool.getConnection(key)
        val stmt = conn.createStatement()
        case class Column(
            tableSchema: String,
            tableName: String,
            columnName: String,
            dataType: String,
            isNullable: String
        )
        val items = mutable.ArrayBuffer.empty[Column]
        try {
          val rs = stmt.executeQuery(
            "SELECT table_schema, table_name, column_name, data_type, is_nullable FROM information_schema.columns"
          )
          try {
            while (rs.next()) {
              val schemaName = rs.getString(1)
              val tableName = rs.getString(2)
              val columnName = rs.getString(3)
              val dataType = rs.getString(4)
              val isNullable = rs.getString(5)
              val column = Column(schemaName, tableName, columnName, dataType, isNullable)
              items += column
            }
          } finally {
            rs.close()
          }
        } finally {
          stmt.close()
        }
        conn.close()
        items
          .groupBy(_.tableSchema)
          .mapValues(_.groupBy(_.tableName).mapValues(_.map(column => column.columnName -> column.dataType).toMap))
      } catch {
        case e: SQLException =>
          logger.error(e.getMessage, e)
          Map.empty
      }
  }

  private val schemaCache = {
    CacheBuilder
      .newBuilder()
      .maximumSize(settings.getInt("raw.client.sql.schema-cache.size"))
      .expireAfterAccess(settings.getDuration("raw.client.sql.schema-cache.duration"))
      .build(loader)
  }

  private val connectionPool = new SqlConnectionPool(settings)

}
