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
import raw.compiler.rql2.api.{
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
import raw.compiler.rql2.ProgramContext
import raw.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.inferrer.api.{SqlQueryInputFormatDescriptor, SqlTableInputFormatDescriptor}
import raw.runtime.interpreter.StringValue

class OraclePackage extends PackageExtension {

  override def name: String = "Oracle"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for accessing Oracle."
  )

}

class OracleInferAndReadEntry extends SugarEntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "Oracle"

  override def entryName: String = "InferAndRead"

  override def docs: EntryDoc = EntryDoc(
    "Reads a Oracle table with schema detection (inference).",
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
    examples = List(ExampleDoc("""Oracle.InferAndRead("database", "schema", "table")""")),
    ret =
      Some(ReturnDoc("A table with the data read from the Oracle table.", retType = Some(TypeDoc(List("collection")))))
  )

  override def optionalParams: Option[Set[String]] = Some(Set("host", "username", "port", "password"))

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 3)
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
    val schema = FunAppArg(StringConst(getStringValue(mandatoryArgs(1))), None)
    val table = FunAppArg(StringConst(getStringValue(mandatoryArgs(2))), None)
    val readType = FunAppArg(TypeExp(t), None)
    val optArgs = optionalArgs.map { case (idn, ValueArg(StringValue(s), _)) => FunAppArg(StringConst(s), Some(idn)) }
    FunApp(
      Proj(PackageIdnExp("Oracle"), "Read"),
      Vector(db, schema, table, readType) ++ optArgs
    )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    for (
      inferrerProperties <- getTableInferrerProperties(mandatoryArgs, optionalArgs, OracleVendor());
      inputFormatDescriptor <- programContext.infer(inferrerProperties);
      SqlTableInputFormatDescriptor(_, _, _, _, tipe) = inputFormatDescriptor
    ) yield {
      inferTypeToRql2Type(tipe, false, false)
    }
  }
}

class OracleReadEntry extends SugarEntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "Oracle"

  override def entryName: String = "Read"

  override def docs: EntryDoc = EntryDoc(
    "Reads a Oracle table.",
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
      ExampleDoc("""Oracle.Read("database", "schema", "table", type record(id: int, name: string, salary: double))""")
    ),
    ret =
      Some(ReturnDoc("A table with the data read from the Oracle table.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 4

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx < 4)
    if (idx == 3) Right(TypeParam(AnythingType()))
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

    // Oracle doesn't need the schema and the table to be quoted
    val select = BinaryExp(
      Plus(),
      BinaryExp(Plus(), BinaryExp(Plus(), StringConst("SELECT * FROM "), schema.e), StringConst(".")),
      table.e
    )
    val query = FunAppArg(select, None)
    FunApp(
      Proj(PackageIdnExp("Oracle"), "Query"),
      Vector(db, query, tipe) ++ optArgs
    )
  }
}

class OracleInferAndQueryEntry extends SugarEntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "Oracle"

  override def entryName: String = "InferAndQuery"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a Oracle database with schema detection (inference).",
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
    examples = List(ExampleDoc("""Oracle.InferAndQuery("database", "SELECT * FROM schema.table")""")),
    ret =
      Some(ReturnDoc("A table with the data read from the Oracle table.", retType = Some(TypeDoc(List("collection")))))
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
      inferrerProperties <- getQueryInferrerProperties(mandatoryArgs, optionalArgs, OracleVendor());
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
      Proj(PackageIdnExp("Oracle"), "Query"),
      Vector(db, query, readType) ++ optArgs
    )
  }

}

class OracleQueryEntry extends EntryExtension with SqlTableExtensionHelper {

  override def packageName: String = "Oracle"

  override def entryName: String = "Query"

  override def docs: EntryDoc = EntryDoc(
    "Performs a query in a Oracle database.",
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
        """Oracle.Query("database", "SELECT a, b FROM schema.table", type collection(record(a: int, b: string)))"""
      )
    ),
    ret =
      Some(ReturnDoc("A table with the data read from the Oracle table.", retType = Some(TypeDoc(List("collection")))))
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
