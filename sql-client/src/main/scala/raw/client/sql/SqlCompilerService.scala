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
import org.bitbucket.inkytonik.kiama.util.Positions
import raw.client.api._
import raw.client.sql.antlr4.{ParseProgramResult, RawSqlSyntaxAnalyzer, SqlIdnNode, SqlParamUseNode}
import raw.client.sql.metadata.UserMetadataCache
import raw.client.sql.writers.{TypedResultSetCsvWriter, TypedResultSetJsonWriter}
import raw.utils.{AuthenticatedUser, RawSettings, RawUtils}

import java.io.{IOException, OutputStream}
import java.sql.{ResultSet, SQLException, SQLTimeoutException}
import scala.util.control.NonFatal

class SqlCompilerService(maybeClassLoader: Option[ClassLoader] = None)(implicit protected val settings: RawSettings)
  extends CompilerService {

  override def language: Set[String] = Set("sql")

  private def parse(prog: String): Either[List[ErrorMessage], ParseProgramResult] = {
    val positions = new Positions
    val syntaxAnalyzer = new RawSqlSyntaxAnalyzer(positions)
    val tree = syntaxAnalyzer.parse(prog)
    val errors = tree.errors.collect { case e: ErrorMessage => e }
    if (errors.nonEmpty) Left(errors)
    else Right(tree)
  }

  override def getProgramDescription(
                                      source: String,
                                      environment: ProgramEnvironment
                                    ): GetProgramDescriptionResponse = {
    try {
      logger.debug(s"Getting program description: $source")
      parse(source) match {
        case Right(parsedTree) =>
          try {
            val conn = connectionPool.getConnection(environment.user)
            try {
              val stmt = new NamedParametersPreparedStatement(conn, source, parsedTree)
              val description = stmt.queryMetadata match {
                case Right(info) =>
                  val parameters = info.parameters
                  val tableType = info.outputType
                  val description = {
                    // Regardless if there are parameters, we declare a main function with the output type.
                    // This permits the publish endpoints from the UI (https://raw-labs.atlassian.net/browse/RD-10359)
                    val ps = for ((name, tipe) <- parameters) yield {
                      // parameters have been checked
                      val rawType = SqlTypesUtils.rawTypeFromJdbc(tipe).right.get
                      val nullableType = rawType match {
                        case RawAnyType() => rawType;
                        case other => other.cloneNullable
                      }
                      ParamDescription(name, nullableType, Some(RawNull()), false)
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
        case Left(errors) => GetProgramDescriptionFailure(errors)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def eval(source: String, tipe: RawType, environment: ProgramEnvironment): EvalResponse = {
    try {
      ???
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def execute(
                        source: String,
                        environment: ProgramEnvironment,
                        maybeDecl: Option[String],
                        outputStream: OutputStream
                      ): ExecutionResponse = {
    try {
      logger.debug(s"Executing: $source")
      parse(source) match {
        case Left(errors) => ExecutionValidationFailure(errors)
        case Right(parsedTree) =>
          try {
            val conn = connectionPool.getConnection(environment.user)
            try {
              val pstmt = new NamedParametersPreparedStatement(conn, source, parsedTree)
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
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
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
    try {
      FormatCodeResponse(Some(source))
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def dotAutoComplete(source: String, environment: ProgramEnvironment, position: Pos): AutoCompleteResponse = {
    try {
      logger.debug(s"dotAutoComplete at position: $position")
      val analyzer = new SqlCodeUtils(source)
      // The editor removes the dot in the completion event
      // So we call the identifier with +1 column
      analyzer.identifierUnder(Pos(position.line, position.column + 1)) match {
        case Some(idn: SqlIdnNode) =>
          val metadataBrowser = metadataBrowsers.get(environment.user)
          val matches = metadataBrowser.getDotCompletionMatches(idn)
          val collectedValues = matches.collect {
            case (idns, tipe) =>
              // If the last identifier is quoted, we need to quote the completion
              val name = if (idns.last.quoted) '"' + idns.last.value + '"' else idns.last.value
              LetBindCompletion(name, tipe)
          }
          logger.debug(s"dotAutoComplete returned ${collectedValues.size} matches")
          AutoCompleteResponse(collectedValues.toArray)
        case Some(use: SqlParamUseNode) => ???
        case _ => AutoCompleteResponse(Array.empty)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def wordAutoComplete(
                                 source: String,
                                 environment: ProgramEnvironment,
                                 prefix: String,
                                 position: Pos
                               ): AutoCompleteResponse = {
    try {
      logger.debug(s"wordAutoComplete at position: $position")
      val analyzer = new SqlCodeUtils(source)
      //    val idns = analyzer.getIdentifierUpTo(position)
      val item = analyzer.identifierUnder(position)
      logger.debug(s"idn $item")
      item match {
        case Some(idn: SqlIdnNode) =>
          val metadataBrowser = metadataBrowsers.get(environment.user)
          val matches = metadataBrowser.getWordCompletionMatches(idn)
          val collectedValues = matches.collect { case (idns, value) => LetBindCompletion(idns.last.value, value) }
          logger.debug(s"wordAutoComplete returned ${collectedValues.size} matches")
          AutoCompleteResponse(collectedValues.toArray)
        case Some(use: SqlParamUseNode) => ???
        case _ => AutoCompleteResponse(Array.empty)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    try {
      logger.debug(s"Hovering at position: $position")
      val analyzer = new SqlCodeUtils(source)
      analyzer
        .identifierUnder(position)
        .map {
          case identifier: SqlIdnNode =>
            val metadataBrowser = metadataBrowsers.get(environment.user)
            val matches = metadataBrowser.getWordCompletionMatches(identifier)
            matches.headOption
              .map { case (names, tipe) => HoverResponse(Some(TypeCompletion(formatIdns(names), tipe))) }
              .getOrElse(HoverResponse(None))
          case use: SqlParamUseNode =>
            try {
              val conn = connectionPool.getConnection(environment.user)
              try {
                val pstmt = new NamedParametersPreparedStatement(conn, source)
                try {
                  pstmt.parameterType(use.name) match {
                    case Right(tipe) => HoverResponse(Some(TypeCompletion(use.name, tipe.typeName)))
                    case Left(_) => HoverResponse(None)
                  }
                } finally {
                  pstmt.close()
                }
              } finally {
                conn.close()
              }
            } catch {
              case e: SQLException => HoverResponse(None)
              case e: SQLTimeoutException => HoverResponse(None)
            }
        }
        .getOrElse(HoverResponse(None))
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  private def formatIdns(idns: Seq[SqlIdentifier]): String = {
    idns.tail.foldLeft(idns.head.value) { case (acc, idn) => acc + "." + idn.value }
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    try {
      RenameResponse(Array.empty)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def goToDefinition(
                               source: String,
                               environment: ProgramEnvironment,
                               position: Pos
                             ): GoToDefinitionResponse = {
    try {
      GoToDefinitionResponse(None)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    try {
      logger.debug(s"Validating: $source")
      parse(source) match {
        case Right(parsedTree) =>
          val r =
            try {
              val conn = connectionPool.getConnection(environment.user)
              try {
                val stmt = new NamedParametersPreparedStatement(conn, source, parsedTree)
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
        case Left(errors) => ValidateResponse(errors)
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    try {
      ValidateResponse(List.empty)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
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
