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

package raw.compiler.rql2.builtin

import raw.compiler.base.errors.{ErrorCompilerMessage, InvalidSemantic}
import raw.compiler.base.source.{AnythingType, BaseNode, Type}
import raw.compiler.common.source._
import raw.compiler.rql2.source._
import raw.compiler.rql2._
import raw.compiler.rql2.api.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtension,
  Param,
  Rql2ListValue,
  Rql2OptionValue,
  Rql2RecordAttr,
  Rql2RecordValue,
  Rql2StringValue,
  SugarEntryExtension,
  TypeArg,
  TypeParam,
  ValueArg,
  ValueParam
}
import raw.client.api._
import raw.inferrer.api.{
  SqlQueryInferrerProperties,
  SqlQueryInputFormatDescriptor,
  SqlTableInferrerProperties,
  SqlTableInputFormatDescriptor
}
import raw.sources.jdbc.snowflake.{SnowflakeServerLocation, SnowflakeTableLocation}

class SnowflakePackage extends PackageExtension {

  override def name: String = "Snowflake"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for accessing Snowflake database."
  )

}

class SnowflakeInferAndReadEntry extends SugarEntryExtension {

  override def packageName: String = "Snowflake"

  override def entryName: String = "InferAndRead"

  override def docs: EntryDoc = EntryDoc(
    "Reads a Snowflake table with schema detection (inference).",
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
        "accountID",
        typeDoc = TypeDoc(List("string")),
        description =
          """The Snowflake account identifier e.g. JC12345.eu-west-1. For more information on account identifiers check [Snowflake documentation](https://docs.snowflake.com/en/user-guide/admin-account-identifier).""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'accountID' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'accountID' and 'username' arguments.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "options",
        typeDoc = TypeDoc(List("list")),
        description = """Extra connection options.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""Snowflake.InferAndRead("database", "schema", "table")""")),
    ret = Some(
      ReturnDoc("A table with the data read from the Snowflake table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def optionalParams: Option[Set[String]] = Some(Set("accountID", "username", "password", "options"))

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
    Right(ValueParam(Rql2StringType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "accountID" => Right(ValueParam(Rql2StringType()))
      case "username" => Right(ValueParam(Rql2StringType()))
      case "password" => Right(ValueParam(Rql2StringType()))
      case "options" => Right(
          ValueParam(
            Rql2ListType(
              Rql2RecordType(
                Vector(
                  Rql2AttrType("_1", Rql2StringType(Set(Rql2IsNullableTypeProperty()))),
                  Rql2AttrType("_2", Rql2StringType(Set(Rql2IsNullableTypeProperty())))
                )
              )
            )
          )
        )
    }
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val db = FunAppArg(StringConst(getStringValue(mandatoryArgs.head)), None)
    val schema = FunAppArg(StringConst(getStringValue(mandatoryArgs(1))), None)
    val table = FunAppArg(StringConst(getStringValue(mandatoryArgs(2))), None)
    val readType = FunAppArg(TypeExp(t), None)
    val optArgs = optionalArgs.map {
      case (idn, ValueArg(Rql2StringValue(s), _)) => FunAppArg(StringConst(s), Some(idn))
      case (idn, ValueArg(Rql2ListValue(v: Seq[Rql2RecordValue]), _)) =>
        val records = v.map { r =>
          val fields = r.v.zipWithIndex.map {
            case (Rql2RecordAttr(_, Rql2OptionValue(Some(Rql2StringValue(v)))), idx) =>
              s"_${idx + 1}" -> NullablePackageBuilder.Build(StringConst(v))
            case (Rql2RecordAttr(_, Rql2OptionValue(None)), idx) => s"_${idx + 1}" -> NullablePackageBuilder.Empty(t)
          }.toVector
          RecordPackageBuilder.Build(fields)
        }
        FunAppArg(ListPackageBuilder.Build(records: _*), Some(idn))
    }

    FunApp(
      Proj(PackageIdnExp("Snowflake"), "Read"),
      Vector(db, schema, table, readType) ++ optArgs
    )
  }

  private def getTableInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)]
  )(implicit programContext: ProgramContext): Either[String, SqlTableInferrerProperties] = {
    val db = getStringValue(mandatoryArgs(0))
    val schema = getStringValue(mandatoryArgs(1))
    val table = getStringValue(mandatoryArgs(2))
    val parameters =
      optionalArgs.collectFirst { case a if a._1 == "options" => getListKVValue(a._2) }.getOrElse(Seq.empty)
    val location =
      if (
        optionalArgs.exists(_._1 == "accountID") || optionalArgs.exists(_._1 == "username") || optionalArgs.exists(
          _._1 == "password"
        )
      ) {
        val accountID =
          getStringValue(optionalArgs.find(_._1 == "accountID").getOrElse(return Left("accountID is required"))._2)
        val username =
          getStringValue(optionalArgs.find(_._1 == "username").getOrElse(return Left("username is required"))._2)
        val password =
          getStringValue(optionalArgs.find(_._1 == "password").getOrElse(return Left("password is required"))._2)
        new SnowflakeTableLocation(db, username, password, accountID, parameters.toMap, schema, table)(
          programContext.settings
        )
      } else {
        programContext.programEnvironment.jdbcServers.get(db) match {
          case Some(l: SnowflakeJdbcLocation) => new SnowflakeTableLocation(
              l.database,
              l.username,
              l.password,
              l.accountIdentifier,
              l.parameters,
              schema,
              table
            )(
              programContext.settings
            )
          case Some(_) => return Left("not a Snowflake server")
          case None => return Left(s"unknown database credential: $db")
        }
      }
    Right(SqlTableInferrerProperties(location, None))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getTableInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties);
      SqlTableInputFormatDescriptor(tipe) = inputFormatDescriptor
    ) yield {
      inferTypeToRql2Type(tipe, makeNullable = false, makeTryable = false)
    }
  }
}

class SnowflakeReadEntry extends SugarEntryExtension {

  override def packageName: String = "Snowflake"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads a Snowflake table.",
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
        "accountID",
        typeDoc = TypeDoc(List("string")),
        description =
          """The Snowflake account identifier e.g. JC12345.eu-west-1. For more information on account identifiers check [Snowflake documentation](https://docs.snowflake.com/en/user-guide/admin-account-identifier).""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'accountID' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'accountID' and 'username' arguments.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "options",
        typeDoc = TypeDoc(List("list")),
        description = """Extra connection options.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc(
        """Snowflake.Read("database", "schema", "table", type record(id: int, name: string, salary: double))"""
      )
    ),
    ret = Some(
      ReturnDoc("A table with the data read from the Snowflake table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def nrMandatoryParams: Int = 4

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 4)
    if (idx == 3) Right(TypeParam(AnythingType()))
    else Right(ExpParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("accountID", "username", "password", "options"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "accountID" => Right(ExpParam(Rql2StringType()))
      case "username" => Right(ExpParam(Rql2StringType()))
      case "password" => Right(ExpParam(Rql2StringType()))
      case "options" => Right(
          ExpParam(
            Rql2ListType(
              Rql2RecordType(
                Vector(
                  Rql2AttrType("_1", Rql2StringType(Set(Rql2IsNullableTypeProperty()))),
                  Rql2AttrType("_2", Rql2StringType(Set(Rql2IsNullableTypeProperty())))
                )
              )
            )
          )
        )
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    // Check that accountID/username/password are all present if any of them is present.
    if (
      optionalArgs.exists(_._1 == "accountID") || optionalArgs
        .exists(_._1 == "username") || optionalArgs.exists(_._1 == "password")
    ) {
      if (!optionalArgs.exists(_._1 == "accountID")) {
        return Left(Seq(InvalidSemantic(node, "accountID is required")))
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

    // Snowflake needs the schema and the table to be quoted
    def quoted(e: Exp) = BinaryExp(
      Plus(),
      BinaryExp(Plus(), StringConst("\""), e),
      StringConst("\"")
    )

    val tableRef = BinaryExp(Plus(), BinaryExp(Plus(), quoted(schema.e), StringConst(".")), quoted(table.e))
    val select = BinaryExp(Plus(), StringConst("SELECT * FROM "), tableRef)
    val query = FunAppArg(select, None)
    FunApp(
      Proj(PackageIdnExp("Snowflake"), "Query"),
      Vector(db, query, tipe) ++ optArgs
    )
  }

}

class SnowflakeInferAndQueryEntry extends SugarEntryExtension {

  override def packageName: String = "Snowflake"

  override def entryName: String = "InferAndQuery"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a Snowflake database with schema detection (inference).",
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
        "accountID",
        typeDoc = TypeDoc(List("string")),
        description =
          """The Snowflake account identifier e.g. JC12345.eu-west-1. For more information on account identifiers check [Snowflake documentation](https://docs.snowflake.com/en/user-guide/admin-account-identifier).""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'accountID' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'accountID' and 'username' arguments.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "options",
        typeDoc = TypeDoc(List("list")),
        description = """Extra connection options.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(ExampleDoc("""Snowflake.InferAndQuery("database", "SELECT * FROM schema.table")""")),
    ret = Some(
      ReturnDoc("A table with the data read from the Snowflake table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def optionalParams: Option[Set[String]] = Some(Set("accountID", "username", "password", "options"))

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 2)
    Right(ValueParam(Rql2StringType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "accountID" => Right(ValueParam(Rql2StringType()))
      case "username" => Right(ValueParam(Rql2StringType()))
      case "password" => Right(ValueParam(Rql2StringType()))
      case "options" => Right(
          ValueParam(
            Rql2ListType(
              Rql2RecordType(
                Vector(
                  Rql2AttrType("_1", Rql2StringType(Set(Rql2IsNullableTypeProperty()))),
                  Rql2AttrType("_2", Rql2StringType(Set(Rql2IsNullableTypeProperty())))
                )
              )
            )
          )
        )
    }
  }

  private def getQueryInferrerProperties(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)]
  )(implicit programContext: ProgramContext): Either[String, SqlQueryInferrerProperties] = {
    val db = getStringValue(mandatoryArgs(0))
    val query = getStringValue(mandatoryArgs(1))
    val parameters =
      optionalArgs.collectFirst { case a if a._1 == "options" => getListKVValue(a._2) }.getOrElse(Seq.empty)
    val location =
      if (
        optionalArgs.exists(_._1 == "accountID") || optionalArgs
          .exists(_._1 == "username") || optionalArgs.exists(_._1 == "password")
      ) {
        val accountID =
          getStringValue(optionalArgs.find(_._1 == "accountID").getOrElse(return Left("accountID is required"))._2)
        val username =
          getStringValue(optionalArgs.find(_._1 == "username").getOrElse(return Left("username is required"))._2)
        val password =
          getStringValue(optionalArgs.find(_._1 == "password").getOrElse(return Left("password is required"))._2)
        new SnowflakeServerLocation(db, username, password, accountID, parameters.toMap)(programContext.settings)
      } else {
        programContext.programEnvironment.jdbcServers.get(db) match {
          case Some(l: SnowflakeJdbcLocation) =>
            new SnowflakeServerLocation(l.database, l.username, l.password, l.accountIdentifier, l.parameters)(
              programContext.settings
            )
          case Some(_) => return Left("not a Snowflake server")
          case None => return Left(s"unknown database credential: $db")
        }
      }
    Right(SqlQueryInferrerProperties(location, query, None))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getQueryInferrerProperties(mandatoryArgs, optionalArgs);
      inputFormatDescriptor <- programContext.infer(inferrerProperties);
      SqlQueryInputFormatDescriptor(tipe) = inputFormatDescriptor
    ) yield {
      inferTypeToRql2Type(tipe, makeNullable = false, makeTryable = false)
    }
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val db = FunAppArg(StringConst(getStringValue(mandatoryArgs.head)), None)
    val query = FunAppArg(StringConst(getStringValue(mandatoryArgs(1))), None)
    val readType = FunAppArg(TypeExp(t), None)
    val optArgs = optionalArgs.map {
      case (idn, ValueArg(Rql2StringValue(s), _)) => FunAppArg(StringConst(s), Some(idn))
      case (idn, ValueArg(Rql2ListValue(v: Seq[Rql2RecordValue]), _)) =>
        // building a List of tuples
        val records = v.map { r =>
          val fields = r.v.zipWithIndex.map {
            case (Rql2RecordAttr(_, Rql2OptionValue(Some(Rql2StringValue(v)))), idx) =>
              s"_${idx + 1}" -> NullablePackageBuilder.Build(StringConst(v))
            case (Rql2RecordAttr(_, Rql2OptionValue(None)), idx) => s"_${idx + 1}" -> NullablePackageBuilder.Empty(t)

          }.toVector
          RecordPackageBuilder.Build(fields)
        }
        FunAppArg(ListPackageBuilder.Build(records: _*), Some(idn))
    }

    FunApp(
      Proj(PackageIdnExp("Snowflake"), "Query"),
      Vector(db, query, readType) ++ optArgs
    )
  }

}

class SnowflakeQueryEntry extends EntryExtension {

  override def packageName: String = "Snowflake"

  override def entryName: String = "Query"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a Snowflake database.",
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
        "accountID",
        typeDoc = TypeDoc(List("string")),
        description =
          """The Snowflake account identifier e.g. JC12345.eu-west-1. For more on information account identifiers check [Snowflake documentation](https://docs.snowflake.com/en/user-guide/admin-account-identifier).""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "username",
        typeDoc = TypeDoc(List("string")),
        description = """The database user name. Can only to be used together with 'accountID' argument.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "password",
        typeDoc = TypeDoc(List("string")),
        description =
          """The database user password. Can only to be used together with 'accountID' and 'username' arguments.""".stripMargin,
        isOptional = true
      ),
      ParamDoc(
        "options",
        typeDoc = TypeDoc(List("list")),
        description = """Extra connection options.""".stripMargin,
        isOptional = true
      )
    ),
    examples = List(
      ExampleDoc(
        """Snowflake.Query("database", "SELECT a, b FROM schema.table", type collection(record(a: int, b: string)))"""
      )
    ),
    ret = Some(
      ReturnDoc("A table with the data read from the Snowflake table.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
    if (idx == 2) Right(TypeParam(AnythingType()))
    else Right(ExpParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("accountID", "username", "password", "options"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "accountID" => Right(ExpParam(Rql2StringType()))
      case "username" => Right(ExpParam(Rql2StringType()))
      case "password" => Right(ExpParam(Rql2StringType()))
      case "options" => Right(
          ValueParam(
            Rql2ListType(
              Rql2RecordType(
                Vector(
                  Rql2AttrType("_1", Rql2StringType(Set(Rql2IsNullableTypeProperty()))),
                  Rql2AttrType("_2", Rql2StringType(Set(Rql2IsNullableTypeProperty())))
                )
              )
            )
          )
        )
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
      optionalArgs.exists(_._1 == "accountID") || optionalArgs
        .exists(_._1 == "username") || optionalArgs.exists(_._1 == "password")
    ) {
      if (!optionalArgs.exists(_._1 == "accountID")) {
        return Left(Seq(InvalidSemantic(node, "accountID is required")))
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
