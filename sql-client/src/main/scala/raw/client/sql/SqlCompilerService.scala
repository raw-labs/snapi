/*
 * Copyright 2024 RAW Labs S.A.
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
import raw.utils.{RawSettings, RawUtils}

import java.io.{IOException, OutputStream}
import java.sql.ResultSet
import scala.util.control.NonFatal

/**
 * A CompilerService implementation for the SQL (Postgres) language.
 *
 * @param settings The configuration settings for the SQL compiler.
 */
class SqlCompilerService()(implicit protected val settings: RawSettings) extends CompilerService {

  private val connectionPool = new SqlConnectionPool()

  // A short lived database metadata (schema/table/column names) indexed by JDBC URL.
  private val metadataBrowsers = {
    val maxSize = settings.getInt("raw.client.sql.metadata-cache.max-matches")
    val expiry = settings.getDuration("raw.client.sql.metadata-cache.match-validity")
    val loader = new CacheLoader[String, UserMetadataCache] {
      override def load(jdbcUrl: String): UserMetadataCache = new UserMetadataCache(
        jdbcUrl,
        connectionPool,
        maxSize = maxSize,
        expiry = expiry
      )
    }
    CacheBuilder
      .newBuilder()
      .maximumSize(settings.getInt("raw.client.sql.metadata-cache.size"))
      .expireAfterAccess(settings.getDuration("raw.client.sql.metadata-cache.duration"))
      .build(loader)
  }

  override def language: Set[String] = Set("sql")

  private def safeParse(prog: String): Either[List[ErrorMessage], ParseProgramResult] = {
    val positions = new Positions
    val syntaxAnalyzer = new RawSqlSyntaxAnalyzer(positions)
    val tree = syntaxAnalyzer.parse(prog)
    val errors = tree.errors.collect { case e: ErrorMessage => e }
    if (errors.nonEmpty) Left(errors)
    else Right(tree)
  }

  private def parse(prog: String): ParseProgramResult = {
    val positions = new Positions
    val syntaxAnalyzer = new RawSqlSyntaxAnalyzer(positions)
    syntaxAnalyzer.parse(prog)
  }

  private def treeErrors(program: ParseProgramResult, messages: Seq[String]): Seq[ErrorMessage] = {
    val start = program.positions.getStart(program.tree).get
    val startPosition = ErrorPosition(start.line, start.column)
    val end = program.positions.getFinish(program.tree).get
    val endPosition = ErrorPosition(end.line, end.column)
    messages.map(message => ErrorMessage(message, List(ErrorRange(startPosition, endPosition)), ErrorCode.SqlErrorCode))
  }

  override def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse = {
    try {
      logger.debug(s"Getting program description: $source")
      safeParse(source) match {
        case Left(errors) => GetProgramDescriptionFailure(errors)
        case Right(parsedTree) =>
          val conn = connectionPool.getConnection(environment.jdbcUrl.get)
          try {
            val stmt = new NamedParametersPreparedStatement(conn, parsedTree)
            val description = stmt.queryMetadata match {
              case Right(info) =>
                val queryParamInfo = info.parameters
                val outputType = pgRowTypeToIterableType(info.outputType)
                val parameterInfo = queryParamInfo
                  .map {
                    case (name, paramInfo) => SqlTypesUtils.rawTypeFromPgType(paramInfo.pgType).map { rawType =>
                        // we ignore tipe.nullable and mark all parameters as nullable
                        val paramType = rawType match {
                          case RawAnyType() => rawType;
                          case other => other.cloneNullable
                        }
                        ParamDescription(
                          name,
                          Some(paramType),
                          paramInfo.default,
                          comment = paramInfo.comment,
                          required = paramInfo.default.isEmpty
                        )
                      }
                  }
                  .foldLeft(Right(Seq.empty): Either[Seq[String], Seq[ParamDescription]]) {
                    case (Left(errors), Left(error)) => Left(errors :+ error)
                    case (_, Left(error)) => Left(Seq(error))
                    case (Right(params), Right(param)) => Right(params :+ param)
                    case (errors @ Left(_), _) => errors
                    case (_, Right(param)) => Right(Seq(param))
                  }
                (outputType, parameterInfo) match {
                  case (Right(iterableType), Right(ps)) =>
                    // Regardless if there are parameters, we declare a main function with the output type.
                    // This permits the publish endpoints from the UI (https://raw-labs.atlassian.net/browse/RD-10359)
                    val ok = ProgramDescription(
                      Map.empty,
                      Some(DeclDescription(Some(ps.toVector), Some(iterableType), None)),
                      None
                    )
                    GetProgramDescriptionSuccess(ok)
                  case _ =>
                    val errorMessages = outputType.left.getOrElse(Seq.empty) ++ parameterInfo.left.getOrElse(Seq.empty)
                    GetProgramDescriptionFailure(treeErrors(parsedTree, errorMessages).toList)
                }
              case Left(errors) => GetProgramDescriptionFailure(errors)
            }
            RawUtils.withSuppressNonFatalException(stmt.close())
            description
          } catch {
            case e: NamedParametersPreparedStatementException => GetProgramDescriptionFailure(e.errors)
          } finally {
            RawUtils.withSuppressNonFatalException(conn.close())
          }
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream,
      maxRows: Option[Long]
  ): ExecutionResponse = {
    try {
      logger.debug(s"Executing: $source")
      safeParse(source) match {
        case Left(errors) => ExecutionValidationFailure(errors)
        case Right(parsedTree) =>
          val conn = connectionPool.getConnection(environment.jdbcUrl.get)
          try {
            val pstmt = new NamedParametersPreparedStatement(conn, parsedTree, environment.scopes)
            try {
              pstmt.queryMetadata match {
                case Right(info) => pgRowTypeToIterableType(info.outputType) match {
                    case Right(tipe) =>
                      val arguments = environment.maybeArguments.getOrElse(Array.empty)
                      pstmt.executeWith(arguments) match {
                        case Right(r) => render(environment, tipe, r, outputStream, maxRows)
                        case Left(error) => ExecutionRuntimeFailure(error)
                      }
                    case Left(errors) => ExecutionRuntimeFailure(errors.mkString(", "))
                  }
                case Left(errors) => ExecutionValidationFailure(errors)
              }
            } finally {
              RawUtils.withSuppressNonFatalException(pstmt.close())
            }
          } catch {
            case e: NamedParametersPreparedStatementException => ExecutionValidationFailure(e.errors)
          } finally {
            RawUtils.withSuppressNonFatalException(conn.close())
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
      outputStream: OutputStream,
      maxRows: Option[Long]
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
        val w = new TypedResultSetCsvWriter(outputStream, lineSeparator, maxRows)
        try {
          w.write(v, tipe)
          ExecutionSuccess(w.complete)
        } catch {
          case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
        } finally {
          RawUtils.withSuppressNonFatalException(w.close())
        }
      case Some("json") =>
        if (!TypedResultSetJsonWriter.outputWriteSupport(tipe)) {
          ExecutionRuntimeFailure("unsupported type")
        }
        val w = new TypedResultSetJsonWriter(outputStream, maxRows)
        try {
          w.write(v, tipe)
          ExecutionSuccess(w.complete)
        } catch {
          case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
        } finally {
          RawUtils.withSuppressNonFatalException(w.close())
        }
      case _ => ExecutionRuntimeFailure("unknown output format")
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
      val analyzer = new SqlCodeUtils(parse(source))
      // The editor removes the dot in the completion event
      // So we call the identifier with +1 column
      analyzer.identifierUnder(Pos(position.line, position.column + 1)) match {
        case Some(idn: SqlIdnNode) =>
          val metadataBrowser = metadataBrowsers.get(environment.jdbcUrl.get)
          val matches = metadataBrowser.getDotCompletionMatches(idn)
          val collectedValues = matches.collect {
            case (idns, tipe) =>
              // If the last identifier is quoted, we need to quote the completion
              val name = if (idns.last.quoted) '"' + idns.last.value + '"' else idns.last.value
              LetBindCompletion(name, tipe)
          }
          logger.debug(s"dotAutoComplete returned ${collectedValues.size} matches")
          AutoCompleteResponse(collectedValues.toArray)
        case Some(_: SqlParamUseNode) =>
          AutoCompleteResponse(Array.empty) // dot completion makes no sense on parameters
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
      val tree = parse(source)
      val analyzer = new SqlCodeUtils(tree)
      val item = analyzer.identifierUnder(position)
      logger.debug(s"idn $item")
      val matches: Seq[Completion] = item match {
        case Some(idn: SqlIdnNode) =>
          val metadataBrowser = metadataBrowsers.get(environment.jdbcUrl.get)
          val matches = metadataBrowser.getWordCompletionMatches(idn)
          matches.collect { case (idns, value) => LetBindCompletion(idns.last.value, value) }
        case Some(use: SqlParamUseNode) => tree.params.collect {
            case (p, paramDescription) if p.startsWith(use.name) =>
              FunParamCompletion(p, paramDescription.tipe.getOrElse(""))
          }.toSeq
        case _ => Array.empty[Completion]
      }
      AutoCompleteResponse(matches.toArray)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    try {
      logger.debug(s"Hovering at position: $position")
      val tree = parse(source)
      val analyzer = new SqlCodeUtils(tree)
      analyzer
        .identifierUnder(position)
        .map {
          case identifier: SqlIdnNode =>
            val metadataBrowser = metadataBrowsers.get(environment.jdbcUrl.get)
            val matches = metadataBrowser.getWordCompletionMatches(identifier)
            matches.headOption
              .map { case (names, tipe) => HoverResponse(Some(TypeCompletion(formatIdns(names), tipe))) }
              .getOrElse(HoverResponse(None))
          case use: SqlParamUseNode =>
            val conn = connectionPool.getConnection(environment.jdbcUrl.get)
            try {
              val pstmt = new NamedParametersPreparedStatement(conn, tree)
              try {
                pstmt.parameterInfo(use.name) match {
                  case Right(typeInfo) => HoverResponse(Some(TypeCompletion(use.name, typeInfo.pgType.typeName)))
                  case Left(_) => HoverResponse(None)
                }
              } finally {
                RawUtils.withSuppressNonFatalException(pstmt.close())
              }
            } catch {
              case _: NamedParametersPreparedStatementException => HoverResponse(None)
            } finally {
              RawUtils.withSuppressNonFatalException(conn.close())
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
      safeParse(source) match {
        case Left(errors) => ValidateResponse(errors)
        case Right(parsedTree) =>
          val conn = connectionPool.getConnection(environment.jdbcUrl.get)
          try {
            val stmt = new NamedParametersPreparedStatement(conn, parsedTree)
            try {
              stmt.queryMetadata match {
                case Right(_) => ValidateResponse(List.empty)
                case Left(errors) => ValidateResponse(errors)
              }
            } finally {
              RawUtils.withSuppressNonFatalException(stmt.close())
            }
          } catch {
            case e: NamedParametersPreparedStatementException => ValidateResponse(e.errors)
          } finally {
            RawUtils.withSuppressNonFatalException(conn.close())
          }
      }
    } catch {
      case NonFatal(t) =>
        logger.debug(t.getMessage)
        throw new CompilerServiceException(t, environment)
    }
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    try {
      ValidateResponse(List.empty)
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, environment)
    }
  }

  override def doStop(): Unit = {
    connectionPool.stop()
  }

  private def pgRowTypeToIterableType(rowType: PostgresRowType): Either[Seq[String], RawIterableType] = {
    val rowAttrTypes = rowType.columns
      .map(c => SqlTypesUtils.rawTypeFromPgType(c.tipe).map(RawAttrType(c.name, _)))
      .foldLeft(Right(Seq.empty): Either[Seq[String], Seq[RawAttrType]]) {
        case (Left(errors), Left(error)) => Left(errors :+ error)
        case (_, Left(error)) => Left(Seq(error))
        case (Right(tipes), Right(tipe)) => Right(tipes :+ tipe)
        case (errors @ Left(_), _) => errors
        case (_, Right(attrType)) => Right(Seq(attrType))
      }
    rowAttrTypes.right.map(attrs => RawIterableType(RawRecordType(attrs.toVector, false, false), false, false))
  }

}
