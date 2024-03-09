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
import raw.client.sql.metadata.UserMetadataCache
import raw.client.sql.writers.{TypedResultSetCsvWriter, TypedResultSetJsonWriter}
import raw.utils.{AuthenticatedUser, RawSettings, RawUtils}

import java.io.{IOException, OutputStream}
import java.sql.{ResultSet, SQLException, SQLTimeoutException}

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
        case e: SQLException => GetProgramDescriptionFailure(ErrorHandling.asErrorMessage(source, e))
      } finally {
        conn.close()
      }
    } catch {
      case e: SQLException => GetProgramDescriptionFailure(ErrorHandling.asErrorMessage(source, e))
      case e: SQLTimeoutException => GetProgramDescriptionFailure(ErrorHandling.asErrorMessage(source, e))
    }
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
        case e: SQLException => ExecutionValidationFailure(ErrorHandling.asErrorMessage(source, e))
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
    logger.debug(s"dotAutoComplete at position: $position")
    val analyzer = new SqlCodeUtils(source)
    // The editor removes the dot in the this completion event
    // So we call the identifier with +1 column
    val idns = analyzer.getIdentifierUpTo(Pos(position.line, position.column + 1))

    val metadataBrowser = metadataBrowsers.get(environment.user)
    val matches = metadataBrowser.getDotCompletionMatches(idns)
    val collectedValues = matches.collect {
      case (idns, tipe) =>
        // If the last identifier is quoted, we need to quote the completion
        val name = if (idns.last.quoted) '"' + idns.last.value + '"' else idns.last.value
        LetBindCompletion(name, tipe)
    }
    logger.debug(s"dotAutoComplete returned ${collectedValues.size} matches")
    AutoCompleteResponse(collectedValues.toArray)
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    logger.debug(s"wordAutoComplete at position: $position")
    val analyzer = new SqlCodeUtils(source)
    val idns = analyzer.getIdentifierUpTo(position)
    logger.debug(s"idns $idns")

    val metadataBrowser = metadataBrowsers.get(environment.user)
    val matches = metadataBrowser.getWordCompletionMatches(idns)
    val collectedValues = matches.collect { case (idns, value) => LetBindCompletion(idns.last.value, value) }
    logger.debug(s"wordAutoComplete returned ${collectedValues.size} matches")
    AutoCompleteResponse(collectedValues.toArray)
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    logger.debug(s"Hovering at position: $position")
    val analyzer = new SqlCodeUtils(source)
    val idns = analyzer.getIdentifierUnder(position)
    val metadataBrowser = metadataBrowsers.get(environment.user)
    val matches = metadataBrowser.getWordCompletionMatches(idns)
    matches
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
          case e: SQLException => ValidateResponse(ErrorHandling.asErrorMessage(source, e))
        } finally {
          conn.close()
        }
      } catch {
        case e: SQLException => ValidateResponse(ErrorHandling.asErrorMessage(source, e))
        case e: SQLTimeoutException => ValidateResponse(ErrorHandling.asErrorMessage(source, e))
      }
    r
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    ValidateResponse(List.empty)
  }

  private val connectionPool = new SqlConnectionPool(settings)
  private val metadataBrowsers = {
    val loader = new CacheLoader[AuthenticatedUser, UserMetadataCache] {
      override def load(user: AuthenticatedUser): UserMetadataCache =
        new UserMetadataCache(user, connectionPool, settings)
    }
    CacheBuilder
      .newBuilder()
      .maximumSize(settings.getInt("raw.client.sql.metadata-cache.size"))
      .expireAfterAccess(settings.getDuration("raw.client.sql.metadata-cache.duration"))
      .build(loader)
  }

  override def doStop(): Unit = {}

}
