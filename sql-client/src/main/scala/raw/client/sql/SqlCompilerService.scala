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
import org.graalvm.polyglot.{Context, HostAccess, Value}
import raw.client.api._
import raw.client.writers.{TypedPolyglotCsvWriter, TypedPolyglotJsonWriter}
import raw.utils.{AuthenticatedUser, RawSettings, RawUtils}

import java.io.{IOException, OutputStream}
import java.sql.{SQLException, SQLTimeoutException}
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
        val result = pstmt.queryMetadata match {
          case Right(info) =>
            try {
              val tipe = info.outputType
              val access = HostAccess.newBuilder().allowMapAccess(true).allowIteratorAccess(true).build()
              val ctx = Context.newBuilder().allowHostAccess(access).build()
              environment.maybeArguments.foreach(array => setParams(pstmt, array))
              val r = pstmt.executeQuery()
              val v = ctx.asValue(new ResultSetIterator(r, ctx))
              render(environment, tipe, v, outputStream)
            } catch {
              case e: SQLException => ExecutionRuntimeFailure(e.getMessage)
            }
          case Left(errors) => ExecutionValidationFailure(errors)
        }
        pstmt.close()
        result
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
      v: Value,
      outputStream: OutputStream
  ): ExecutionResponse = {
    environment.options
      .get("output-format")
      .map(_.toLowerCase) match {
      case Some("csv") =>
        if (!TypedPolyglotCsvWriter.outputWriteSupport(tipe)) {
          ExecutionRuntimeFailure("unsupported type")
        }
        val windowsLineEnding = environment.options.get("windows-line-ending") match {
          case Some("true") => true
          case _ => false //settings.config.getBoolean("raw.compiler.windows-line-ending")
        }
        val lineSeparator = if (windowsLineEnding) "\r\n" else "\n"
        val csvWriter = new TypedPolyglotCsvWriter(outputStream, lineSeparator)
        try {
          csvWriter.write(v, tipe)
          ExecutionSuccess
        } catch {
          case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
        } finally {
          RawUtils.withSuppressNonFatalException(csvWriter.close())
        }
      case Some("json") =>
        if (!TypedPolyglotJsonWriter.outputWriteSupport(tipe)) {
          ExecutionRuntimeFailure("unsupported type")
        }
        val w = new TypedPolyglotJsonWriter(outputStream)
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
    val token = analyzer.getIdentifierUpTo(position)
    val schemas = getSchemas(environment)
    val matches = schemas.get(token) match {
      case Some(tables) => tables.keys.map(name => LetBindCompletion(name, "table"))
      case None =>
        for (tables <- schemas.values; (table, columns) <- tables; if table == token; (name, tipe) <- columns) yield {
          LetBindCompletion(name, tipe)
        }
    }
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
    val token = analyzer.getIdentifierUpTo(position)
    val idns = SqlCodeUtils.separateIdentifiers(token)
    logger.debug(s"idns $idns")
    logger.debug(token)

    val matches = for (
      (key, value) <- scope; // for each entry in scope
      if key.startsWith(token); // if the key starts with the token
      if !key.drop(token.length).contains('.'); // and the key doesn't contain a dot after the token
      word =
        key.split('.').last // then the word to complete with is the last part of the matching key (after the last dot)
    ) yield {
      LetBindCompletion(word, value)
    }
    AutoCompleteResponse(matches.toArray)
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    logger.debug(s"Hovering at position: $position")
    val analyzer = new SqlCodeUtils(source)
    val token = analyzer.getIdentifierUnder(position)
    val scope = getFullyQualifiedScope(environment)
    scope
      .find(_._1 == token)
      .map { case (name, tipe) => HoverResponse(Some(TypeCompletion(name, tipe))) }
      .getOrElse(HoverResponse(None))
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

  private def getFullyQualifiedScope(environment: ProgramEnvironment): Map[String, String] = {
    val schema = getSchemas(environment)
    val items = mutable.Map.empty[String, String]
    for ((schemaName, tables) <- schema) {
      for ((tableName, columns) <- tables) {
        for ((columnName, tipe) <- columns) {
          // columns are available as: column, table.column, schema.table.column
          items(columnName) = tipe
          items(s"$tableName.$columnName") = tipe
          items(s"$schemaName.$tableName.$columnName") = tipe
        }
        // tables are available as: table, schema.table. If such key overlaps with a column one, it takes precedence (stored last)
        items(s"$tableName") = "table"
        items(s"$schemaName.$tableName") = "table"
      }
      // schemas as stored last. If there's a table or a column with the same name, it takes precedence
      items(s"$schemaName") = "schema"
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
