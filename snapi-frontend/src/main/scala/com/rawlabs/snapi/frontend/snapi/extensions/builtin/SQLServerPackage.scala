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

package com.rawlabs.snapi.frontend.snapi.extensions.builtin

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.errors.{ErrorCompilerMessage, InvalidSemantic}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, BaseNode, Type}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtension,
  Param,
  SugarEntryExtension,
  TypeArg,
  TypeParam,
  ValueArg,
  ValueParam
}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.inferrer.api.{
  SqlQueryInferrerInput,
  SqlQueryInferrerOutput,
  SqlTableInferrerInput,
  SqlTableInferrerOutput
}
import com.rawlabs.utils.sources.jdbc.sqlserver.{SqlServerServerLocation, SqlServerTableLocation}

class SQLServerPackage extends PackageExtension {

  override def name: String = "SQLServer"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for accessing Microsoft SQL Server."
  )

}

class SQLServerInferAndReadEntry extends SugarEntryExtension {

  override def packageName: String = "SQLServer"

  override def entryName: String = "InferAndRead"

  override def docs: EntryDoc = EntryDoc(
    "Reads a SQLServer table with schema detection (inference).",
    params = List(
      ParamDoc(
        "database",
        TypeDoc(List("string")),
        description =
          "The name of the database to read. If the database credentials are stored in the credentials storage, then this parameter receives the name of the credential."
      ),
      ParamDoc(
        "schema",
        TypeDoc(List("string")),
        description = "The database schema name."
      ),
      ParamDoc(
        "table",
        typeDoc = TypeDoc(List("string")),
        description = "The name of the table."
      ),
      ParamDoc(
        "host",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database server hostname. Can only be used if not using registered credentials.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "port",
        typeDoc = TypeDoc(List("int")),
        description = """The database server port. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'host' and 'username' arguments.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""SQLServer.InferAndRead("database", "schema", "table")""")),
    ret = Some(
      ReturnDoc("A table with the data read from the SQL Server table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
    Right(ValueParam(SnapiStringType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ValueParam(SnapiStringType()))
      case "port" => Right(ValueParam(SnapiIntType()))
      case "username" => Right(ValueParam(SnapiStringType()))
      case "password" => Right(ValueParam(SnapiStringType()))
    }
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val db = FunAppArg(StringConst(getStringValue(mandatoryArgs(0))), None)
    val schema = FunAppArg(StringConst(getStringValue(mandatoryArgs(1))), None)
    val table = FunAppArg(StringConst(getStringValue(mandatoryArgs(2))), None)
    val readType = FunAppArg(TypeExp(t), None)
    val optArgs =
      optionalArgs.map { case (idn, ValueArg(SnapiStringValue(s), _)) => FunAppArg(StringConst(s), Some(idn)) }
    FunApp(
      Proj(PackageIdnExp("SQLServer"), "Read"),
      Vector(db, schema, table, readType) ++ optArgs
    )
  }

  private def getTableInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)]
  )(implicit programContext: ProgramContext): Either[String, SqlTableInferrerInput] = {
    val db = getStringValue(mandatoryArgs(0))
    val schema = getStringValue(mandatoryArgs(1))
    val table = getStringValue(mandatoryArgs(2))
    val location =
      if (
        optionalArgs.exists(_._1 == "host") || optionalArgs
          .exists(_._1 == "username") || optionalArgs.exists(_._1 == "password")
      ) {
        val host = getStringValue(optionalArgs.find(_._1 == "host").getOrElse(return Left("host is required"))._2)
        val port = optionalArgs.find(_._1 == "port").map(v => getIntValue(v._2)).getOrElse(1433)
        val username =
          getStringValue(optionalArgs.find(_._1 == "username").getOrElse(return Left("username is required"))._2)
        val password =
          getStringValue(optionalArgs.find(_._1 == "password").getOrElse(return Left("password is required"))._2)
        new SqlServerTableLocation(host, port, db, username, password, schema, table)(programContext.settings)
      } else {
        programContext.programEnvironment.locationConfigs.get(db) match {
          case Some(l) if l.hasSqlserver =>
            val l1 = l.getSqlserver
            new SqlServerTableLocation(
              l1.getHost,
              l1.getPort,
              l1.getDatabase,
              l1.getUser,
              l1.getPassword,
              schema,
              table
            )(
              programContext.settings
            )
          case Some(l) if l.hasError => return Left(l.getError.getMessage)
          case Some(_) => return Left("not an Oracle server")
          case None => return Left(s"unknown credential: $db")
        }
      }
    Right(SqlTableInferrerInput(location, None))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getTableInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties, mandatoryArgs, optionalArgs, varArgs);
      SqlTableInferrerOutput(tipe) = inputFormatDescriptor
    ) yield {
      inferTypeToSnapiType(tipe, false, false)
    }
  }
}

class SQLServerReadEntry extends SugarEntryExtension {

  override def packageName: String = "SQLServer"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads a SQLServer table.",
    params = List(
      ParamDoc(
        "database",
        TypeDoc(List("string")),
        description =
          "The name of the database to read. If the database credentials are stored in the credentials storage, then this parameter receives the name of the credential."
      ),
      ParamDoc(
        "schema",
        TypeDoc(List("string")),
        description = "The database schema name."
      ),
      ParamDoc(
        "table",
        typeDoc = TypeDoc(List("string")),
        description = "The name of the table."
      ),
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the table."
      ),
      ParamDoc(
        "host",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database server hostname. Can only be used if not using registered credentials.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "port",
        typeDoc = TypeDoc(List("int")),
        description = """The database server port. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'host' and 'username' arguments.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc(
        """SQLServer.Read("database", "schema", "table", type record(id: int, name: string, salary: double))"""
      )
    ),
    ret = Some(
      ReturnDoc("A table with the data read from the SQL Server table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def nrMandatoryParams: Int = 4

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 4)
    if (idx == 3) Right(TypeParam(AnythingType()))
    else Right(ExpParam(SnapiStringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ExpParam(SnapiStringType()))
      case "port" => Right(ExpParam(SnapiIntType()))
      case "username" => Right(ExpParam(SnapiStringType()))
      case "password" => Right(ExpParam(SnapiStringType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    // Check that host/port/username/password are all present if any of them is present.
    if (
      optionalArgs.exists(_._1 == "host") || optionalArgs
        .exists(_._1 == "username") || optionalArgs.exists(_._1 == "password")
    ) {
      if (!optionalArgs.exists(_._1 == "host")) {
        return Left(Seq(InvalidSemantic(node, "host is required")))
      }
      if (!optionalArgs.exists(_._1 == "username")) {
        return Left(Seq(InvalidSemantic(node, "username is required")))
      }
      if (!optionalArgs.exists(_._1 == "password")) {
        return Left(Seq(InvalidSemantic(node, "password is required")))
      }
    }

    val t = mandatoryArgs(3).t
    validateTableType(t)
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val db = FunAppArg(mandatoryArgs.head.asInstanceOf[ExpArg].e, None)
    val schema = FunAppArg(mandatoryArgs(1).asInstanceOf[ExpArg].e, None)
    val table = FunAppArg(mandatoryArgs(2).asInstanceOf[ExpArg].e, None)
    val tipe = FunAppArg(TypeExp(mandatoryArgs(3).asInstanceOf[TypeArg].t), None)
    val optArgs = optionalArgs.map { case (idn, ExpArg(e, _)) => FunAppArg(e, Some(idn)) }

    // SQLServer needs the schema and the table to be quoted
    def quoted(e: Exp) = BinaryExp(Plus(), BinaryExp(Plus(), StringConst("\""), e), StringConst("\""))

    val tableRef = BinaryExp(Plus(), BinaryExp(Plus(), quoted(schema.e), StringConst(".")), quoted(table.e))
    val select = BinaryExp(Plus(), StringConst("SELECT * FROM "), tableRef)
    val query = FunAppArg(select, None)
    FunApp(
      Proj(PackageIdnExp("SQLServer"), "Query"),
      Vector(db, query, tipe) ++ optArgs
    )
  }
}

class SQLServerInferAndQueryEntry extends SugarEntryExtension {

  override def packageName: String = "SQLServer"

  override def entryName: String = "InferAndQuery"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a SQLServer database with schema detection (inference).",
    params = List(
      ParamDoc(
        "database",
        TypeDoc(List("string")),
        description =
          "The name of the database to read. If the database credentials are stored in the credentials storage, then this parameter receives the name of the credential."
      ),
      ParamDoc(
        "query",
        TypeDoc(List("string")),
        description = "The query to execute in the database."
      ),
      ParamDoc(
        "host",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database server hostname. Can only be used if not using registered credentials.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "port",
        typeDoc = TypeDoc(List("int")),
        description = """The database server port. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'host' and 'username' arguments.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""SQLServer.InferAndQuery("database", "SELECT * FROM schema.table")""")),
    ret = Some(
      ReturnDoc("A table with the data read from the SQL Server table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 2)
    Right(ValueParam(SnapiStringType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ValueParam(SnapiStringType()))
      case "port" => Right(ValueParam(SnapiIntType()))
      case "username" => Right(ValueParam(SnapiStringType()))
      case "password" => Right(ValueParam(SnapiStringType()))
    }
  }

  private def getQueryInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)]
  )(implicit programContext: ProgramContext): Either[String, SqlQueryInferrerInput] = {
    val db = getStringValue(mandatoryArgs(0))
    val query = getStringValue(mandatoryArgs(1))
    val location =
      if (
        optionalArgs.exists(_._1 == "host") || optionalArgs
          .exists(_._1 == "username") || optionalArgs.exists(_._1 == "password")
      ) {
        val host = getStringValue(optionalArgs.find(_._1 == "host").getOrElse(return Left("host is required"))._2)
        val port = optionalArgs.find(_._1 == "port").map(v => getIntValue(v._2)).getOrElse(1433)
        val username =
          getStringValue(optionalArgs.find(_._1 == "username").getOrElse(return Left("username is required"))._2)
        val password =
          getStringValue(optionalArgs.find(_._1 == "password").getOrElse(return Left("password is required"))._2)
        new SqlServerServerLocation(host, port, db, username, password)(programContext.settings)
      } else {
        programContext.programEnvironment.locationConfigs.get(db) match {
          case Some(l) if l.hasSqlserver =>
            val l1 = l.getSqlserver
            new SqlServerServerLocation(l1.getHost, l1.getPort, l1.getDatabase, l1.getUser, l1.getPassword)(
              programContext.settings
            )
          case Some(l) if l.hasError => return Left(l.getError.getMessage)
          case Some(_) => return Left("not an Oracle server")
          case None => return Left(s"unknown credential: $db")
        }
      }
    Right(SqlQueryInferrerInput(location, query, None))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getQueryInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties, mandatoryArgs, optionalArgs, varArgs);
      SqlQueryInferrerOutput(tipe) = inputFormatDescriptor
    ) yield {
      inferTypeToSnapiType(tipe, false, false)
    }
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val db = FunAppArg(StringConst(getStringValue(mandatoryArgs(0))), None)
    val query = FunAppArg(StringConst(getStringValue(mandatoryArgs(1))), None)
    val readType = FunAppArg(TypeExp(t), None)
    val optArgs =
      optionalArgs.map { case (idn, ValueArg(SnapiStringValue(s), _)) => FunAppArg(StringConst(s), Some(idn)) }
    FunApp(
      Proj(PackageIdnExp("SQLServer"), "Query"),
      Vector(db, query, readType) ++ optArgs
    )
  }
}

class SQLServerQueryEntry extends EntryExtension {

  override def packageName: String = "SQLServer"

  override def entryName: String = "Query"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a SQLServer database.",
    params = List(
      ParamDoc(
        "database",
        TypeDoc(List("string")),
        description =
          "The name of the database to read. If the database credentials are stored in the credentials storage, then this parameter receives the name of the credential."
      ),
      ParamDoc(
        "query",
        TypeDoc(List("string")),
        description = "The query to execute in the database."
      ),
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the query result."
      ),
      ParamDoc(
        "host",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database server hostname. Can only be used if not using registered credentials.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "port",
        typeDoc = TypeDoc(List("int")),
        description = """The database server port. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'host' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'host' and 'username' arguments.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc(
        """SQLServer.Query("database", "SELECT a, b FROM schema.table", type collection(record(a: int, b: string)))"""
      )
    ),
    ret = Some(
      ReturnDoc("A table with the data read from the SQL Server table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
    if (idx == 2) Right(TypeParam(AnythingType()))
    else Right(ExpParam(SnapiStringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ExpParam(SnapiStringType()))
      case "port" => Right(ExpParam(SnapiIntType()))
      case "username" => Right(ExpParam(SnapiStringType()))
      case "password" => Right(ExpParam(SnapiStringType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    // Check that host/port/username/password are all present if any of them is present.
    if (
      optionalArgs.exists(_._1 == "host") || optionalArgs
        .exists(_._1 == "username") || optionalArgs.exists(_._1 == "password")
    ) {
      if (!optionalArgs.exists(_._1 == "host")) {
        return Left(Seq(InvalidSemantic(node, "host is required")))
      }
      if (!optionalArgs.exists(_._1 == "username")) {
        return Left(Seq(InvalidSemantic(node, "username is required")))
      }
      if (!optionalArgs.exists(_._1 == "password")) {
        return Left(Seq(InvalidSemantic(node, "password is required")))
      }
    }

    val t = mandatoryArgs(2).t
    validateTableType(t)
  }

}
