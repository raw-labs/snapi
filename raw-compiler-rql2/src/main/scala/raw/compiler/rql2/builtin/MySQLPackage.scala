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

import raw.compiler.base.errors.BaseError
import raw.compiler.base.source.{AnythingType, BaseNode, Type}
import raw.compiler.common.source.Exp
import raw.compiler.rql2.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtension,
  Param,
  ProgramContext,
  SugarEntryExtension,
  TypeArg,
  TypeParam,
  ValueArg,
  ValueParam
}
import raw.compiler.rql2.source.{
  BinaryExp,
  FunApp,
  FunAppArg,
  PackageIdnExp,
  Plus,
  Proj,
  Rql2IntType,
  Rql2StringType,
  StringConst,
  TypeExp
}
import raw.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.inferrer.{SqlQueryInputFormatDescriptor, SqlTableInputFormatDescriptor}
import raw.runtime.interpreter.StringValue

class MySQLPackage extends PackageExtension {

  override def name: String = "MySQL"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for accessing MySQL."
  )

}

class MySQLInferAndReadEntry extends SugarEntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "MySQL"

  override def entryName: String = "InferAndRead"

  override def docs: EntryDoc = EntryDoc(
    "Reads a MySQL table with schema detection (inference).",
    params = List(
      ParamDoc(
        "database",
        TypeDoc(List("string")),
        description =
          "The name of the database to read. If the database credentials are stored in the credentials storage, then this parameter receives the name of the credential."
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
    examples = List(ExampleDoc("""MySQL.InferAndRead("database", "table")""")),
    ret =
      Some(ReturnDoc("A table with the data read from the MySQL table.", retType = Some(TypeDoc(List("collection")))))
  )

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 2)
    Right(ValueParam(Rql2StringType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ValueParam(Rql2StringType()))
      case "port" => Right(ValueParam(Rql2IntType()))
      case "username" => Right(ValueParam(Rql2StringType()))
      case "password" => Right(ValueParam(Rql2StringType()))
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
    val table = FunAppArg(StringConst(getStringValue(mandatoryArgs(1))), None)
    val readType = FunAppArg(TypeExp(t), None)
    val optArgs = optionalArgs.map { case (idn, ValueArg(StringValue(s), _)) => FunAppArg(StringConst(s), Some(idn)) }
    FunApp(
      Proj(PackageIdnExp("MySQL"), "Read"),
      Vector(db, table, readType) ++ optArgs
    )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getTableInferrerProperties(mandatoryArgs, optionalArgs, MySqlVendor());
      inputFormatDescriptor <- programContext.infer(inferrerProperties);
      SqlTableInputFormatDescriptor(_, _, _, _, tipe) = inputFormatDescriptor
    ) yield {
      inferTypeToRql2Type(tipe, false, false)
    }
  }
}

class MySQLReadEntry extends SugarEntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "MySQL"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads a MySQL table with schema detection (inference).",
    params = List(
      ParamDoc(
        "database",
        TypeDoc(List("string")),
        description =
          "The name of the database to read. If the database credentials are stored in the credentials storage, then this parameter receives the name of the credential."
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
    examples =
      List(ExampleDoc("""MySQL.Read("database", "table", type record(id: int, name: string, salary: double))""")),
    ret =
      Some(ReturnDoc("A table with the data read from the MySQL table.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
    if (idx == 2) Right(TypeParam(AnythingType()))
    else Right(ExpParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ExpParam(Rql2StringType()))
      case "port" => Right(ExpParam(Rql2IntType()))
      case "username" => Right(ExpParam(Rql2StringType()))
      case "password" => Right(ExpParam(Rql2StringType()))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[BaseError], Type] = {
    val t = mandatoryArgs(2).t
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
    val table = FunAppArg(mandatoryArgs(1).asInstanceOf[ExpArg].e, None)
    val tipe = FunAppArg(TypeExp(mandatoryArgs(2).asInstanceOf[TypeArg].t), None)
    val optArgs = optionalArgs.map { case (idn, ExpArg(e, _)) => FunAppArg(e, Some(idn)) }

    // MySql needs the table name to be quoted with backticks
    def quoted(e: Exp) = BinaryExp(Plus(), BinaryExp(Plus(), StringConst("`"), e), StringConst("`"))
    val select = BinaryExp(Plus(), StringConst("SELECT * FROM "), quoted(table.e))
    val query = FunAppArg(select, None)
    FunApp(
      Proj(PackageIdnExp("MySQL"), "Query"),
      Vector(db, query, tipe) ++ optArgs
    )
  }
}

class MySQLInferAndQueryEntry extends SugarEntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "MySQL"

  override def entryName: String = "InferAndQuery"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a MySQL database with schema detection (inference).",
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
    examples = List(ExampleDoc("""MySQL.InferAndQuery("database", "SELECT * FROM table")""")),
    ret =
      Some(ReturnDoc("A table with the data read from the MySQL table.", retType = Some(TypeDoc(List("collection")))))
  )

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 2)
    Right(ValueParam(Rql2StringType()))
  }

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ValueParam(Rql2StringType()))
      case "port" => Right(ValueParam(Rql2IntType()))
      case "username" => Right(ValueParam(Rql2StringType()))
      case "password" => Right(ValueParam(Rql2StringType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getQueryInferrerProperties(mandatoryArgs, optionalArgs, MySqlVendor());
      inputFormatDescriptor <- programContext.infer(inferrerProperties);
      SqlQueryInputFormatDescriptor(_, _, tipe) = inputFormatDescriptor
    ) yield {
      inferTypeToRql2Type(tipe, false, false)
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
    val optArgs = optionalArgs.map { case (idn, ValueArg(StringValue(s), _)) => FunAppArg(StringConst(s), Some(idn)) }
    FunApp(
      Proj(PackageIdnExp("MySQL"), "Query"),
      Vector(db, query, readType) ++ optArgs
    )
  }

}

class MySQLQueryEntry extends EntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "MySQL"

  override def entryName: String = "Query"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a MySQL database.",
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
      ExampleDoc("""MySQL.Query("database", "SELECT a, b FROM table", type collection(record(a: int, b: string)))""")
    ),
    ret =
      Some(ReturnDoc("A table with the data read from the MySQL table.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
    if (idx == 2) Right(TypeParam(AnythingType()))
    else Right(ExpParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    idn match {
      case "host" => Right(ExpParam(Rql2StringType()))
      case "port" => Right(ExpParam(Rql2IntType()))
      case "username" => Right(ExpParam(Rql2StringType()))
      case "password" => Right(ExpParam(Rql2StringType()))
    }
  }
  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[BaseError], Type] = {
    val t = mandatoryArgs(2).t
    validateTableType(t)
  }

}
