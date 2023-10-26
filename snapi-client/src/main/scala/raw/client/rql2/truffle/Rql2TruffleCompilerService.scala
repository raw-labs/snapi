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

package raw.client.rql2.truffle

import org.bitbucket.inkytonik.kiama.relation.EnsureTree
import org.bitbucket.inkytonik.kiama.util.{Position, Positions}
import org.graalvm.polyglot.{Value, _}
import raw.client.api._
import raw.client.writers.{PolyglotBinaryWriter, PolyglotTextWriter}
import raw.compiler.base
import raw.compiler.base.errors.{BaseError, UnexpectedType, UnknownDecl}
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.base.{CompilerContext, TreeDeclDescription, TreeDescription, TreeParamDescription}
import raw.compiler.common.source.{SourceNode, SourceProgram}
import raw.compiler.rql2.builtin.{BinaryPackage, CsvPackage, JsonPackage, StringPackage}
import raw.compiler.rql2.errors._
import raw.compiler.rql2.lsp.{CompilerLspService, LspSyntaxAnalyzer}
import raw.compiler.rql2.source._
import raw.compiler.rql2._
import raw.compiler.scala2.Scala2CompilerContext
import raw.creds.api.CredentialsServiceProvider
import raw.inferrer.api.InferrerServiceProvider
import raw.runtime._
import raw.sources.api.SourceContext
import raw.utils.{withSuppressNonFatalException, AuthenticatedUser, RawConcurrentHashMap, RawSettings}

import java.io.{IOException, OutputStream}
import scala.collection.mutable
import scala.util.control.NonFatal

class Rql2TruffleCompilerService(maybeClassLoader: Option[ClassLoader] = None)(implicit settings: RawSettings)
    extends CompilerService {

  private val credentials = CredentialsServiceProvider(maybeClassLoader)

  // Map of users to compiler context.
  private val compilerContextCaches = new RawConcurrentHashMap[AuthenticatedUser, CompilerContext]

  private val engine = {
    val options = new java.util.HashMap[String, String]()
    if (settings.onTrainingWheels) {
      options.put("engine.CompileImmediately", "true")
      options.put("engine.TraceCompilation", "true")
      options.put("engine.BackgroundCompilation", "false")
//      options.put("engine.CompilationFailureAction", "Throw")
//      options.put("engine.CompilationFailureAction", "Diagnose")
//      options.put("compiler.LogInlinedTargets", "true")
      //  "-Dpolyglotimpl.CompilationFailureAction=Throw",
      //  "-Dpolyglotimpl.TreatPerformanceWarningsAsErrors=false",
      //  "-Dpolyglotimpl.CompilationExceptionsAreFatal=true",
      //  "-Dpolyglotimpl.BackgroundCompilation=false",
      //  "-Dpolyglotimpl.TraceCompilationDetails=true",
      //  "-Dpolyglotimpl.TraceInlining=true"
      //  "-Dgraal.Dump=Truffle:2",
      //  "-Dgraal.DumpPath=/tmp/graal_dumps",
      //  "-Dgraal.PrintGraph=Network",
      // https://www.graalvm.org/latest/graalvm-as-a-platform/language-implementation-framework/Options/
    }
    Engine.newBuilder().allowExperimentalOptions(true).options(options).build()
  }

  private def getCompilerContext(user: AuthenticatedUser): CompilerContext = {
    compilerContextCaches.getOrElseUpdate(user, createCompilerContext(user, "rql2-truffle"))
  }

  private def createCompilerContext(user: AuthenticatedUser, language: String): CompilerContext = {
    // Initialize source context
    implicit val sourceContext = new SourceContext(user, credentials, settings, maybeClassLoader)

    // Initialize inferrer
    val inferrer = InferrerServiceProvider(maybeClassLoader)

    // Initialize compiler context
    new Scala2CompilerContext(language, user, sourceContext, inferrer, maybeClassLoader, null)
  }

  private def getProgramContext(user: AuthenticatedUser, environment: ProgramEnvironment): ProgramContext = {
    val compilerContext = getCompilerContext(user)
    val runtimeContext = new RuntimeContext(compilerContext.sourceContext, settings, environment)
    new ProgramContext(runtimeContext)(compilerContext)
  }

  def prettyPrint(node: BaseNode, user: AuthenticatedUser): String = {
    SourcePrettyPrinter.format(node)
  }

  // TODO (msb): Change signature to include position of the parsing error.
  def parseType(tipe: String, user: AuthenticatedUser, internal: Boolean = false): ParseTypeResponse = {
    val positions = new Positions()
    val parser = if (!internal) new FrontendSyntaxAnalyzer(positions) else new SyntaxAnalyzer(positions)
    parser.parseType(tipe) match {
      case Right(t) => ParseTypeSuccess(t)
      case Left((err, pos)) => ParseTypeFailure(err)
    }
  }

  def parse(source: String, environment: ProgramEnvironment): ParseResponse = {
    val programContext = getProgramContext(environment.user, environment)
    try {
      val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
      val root = tree.root
      ParseSuccess(root)
    } catch {
      case ex: CompilerParserException => ParseFailure(ex.getMessage, ex.position)
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  def getType(
      source: String,
      environment: ProgramEnvironment
  ): GetTypeResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
          if (tree.valid) {
            GetTypeSuccess(tree.rootType)
          } else {
            GetTypeFailure(tree.errors)
          }
        } catch {
          case ex: CompilerParserException =>
            GetTypeFailure(List(ErrorMessage(ex.getMessage, List(raw.client.api.ErrorRange(ex.position, ex.position)))))
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def getProgramDescription(
      source: String,
      environment: ProgramEnvironment
  ): GetProgramDescriptionResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
          if (tree.valid) {
            val TreeDescription(decls, maybeType, comment) = tree.description
            val formattedDecls = decls.map {
              case (idn, programDecls) =>
                val formattedDecls = programDecls.map {
                  case TreeDeclDescription(None, outType, comment) =>
                    DeclDescription(None, SourcePrettyPrinter.format(outType), comment)
                  case TreeDeclDescription(Some(params), outType, comment) =>
                    val formattedParams = params.map {
                      case TreeParamDescription(idn, tipe, required) =>
                        ParamDescription(idn, SourcePrettyPrinter.format(tipe), required)
                    }
                    DeclDescription(Some(formattedParams), SourcePrettyPrinter.format(outType), comment)
                }
                (idn, formattedDecls)
            }
            val programDescription =
              ProgramDescription(formattedDecls, maybeType.map(t => SourcePrettyPrinter.format(t)), comment)
            GetProgramDescriptionSuccess(programDescription)
          } else {
            GetProgramDescriptionFailure(tree.errors)
          }
        } catch {
          case ex: CompilerParserException => GetProgramDescriptionFailure(
              List(ErrorMessage(ex.getMessage, List(raw.client.api.ErrorRange(ex.position, ex.position))))
            )
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def eval(source: String, tipe: String, environment: ProgramEnvironment): EvalResponse = {
    withTruffleContext(
      environment,
      ctx =>
        try {
          val truffleSource = Source
            .newBuilder("rql", source, "unnamed")
            .cached(false) // Disable code caching because of the inferrer.
            .build()
          val polyglotValue = ctx.eval(truffleSource)
          val internal = environment.options.get("staged-compiler") match {
            case Some("true") => true
            case _ => false
          }
          val rawType = parseType(tipe, environment.user, internal).asInstanceOf[ParseTypeSuccess].tipe
          val rawValue = convertPolyglotValueToRawValue(polyglotValue, rawType)
          EvalSuccess(rawValue)
        } catch {
          case ex: PolyglotException =>
            if (ex.isInterrupted) {
              throw new InterruptedException()
            } else if (ex.isGuestException) {
              val err = ex.getGuestObject
              if (err != null && err.hasMembers && err.hasMember("errors")) {
                val errorsValue = err.getMember("errors")
                val errors = (0L until errorsValue.getArraySize).map { i =>
                  val errorValue = errorsValue.getArrayElement(i)
                  val message = errorValue.asString
                  val positions = (0L until errorValue.getArraySize).map { j =>
                    val posValue = errorValue.getArrayElement(j)
                    val beginValue = posValue.getMember("begin")
                    val endValue = posValue.getMember("end")
                    val begin = ErrorPosition(beginValue.getMember("line").asInt, beginValue.getMember("column").asInt)
                    val end = ErrorPosition(endValue.getMember("line").asInt, endValue.getMember("column").asInt)
                    ErrorRange(begin, end)
                  }
                  ErrorMessage(message, positions.to)
                }
                EvalValidationFailure(errors.to)
              } else {
                EvalRuntimeFailure(ex.getMessage)
              }
            } else {
              // Unexpected error. For now we throw the PolyglotException.
              throw ex
            }
        }
    )
  }

  private def convertPolyglotValueToRawValue(v: Value, t: Type): raw.client.api.Value = {
    t match {
      case t: Rql2TypeWithProperties if t.props.contains(Rql2IsTryableTypeProperty()) =>
        if (v.isException) {
          try {
            v.throwException()
            ??? // Should not happen.
          } catch {
            case NonFatal(ex) => raw.client.api.TryValue(Left(ex.getMessage))
          }
        } else {
          // Success, recurse without the tryable property.
          val v1 = convertPolyglotValueToRawValue(v, t.cloneAndRemoveProp(Rql2IsTryableTypeProperty()))
          raw.client.api.TryValue(Right(v1))
        }
      case t: Rql2TypeWithProperties if t.props.contains(Rql2IsNullableTypeProperty()) =>
        if (v.isNull) {
          raw.client.api.OptionValue(None)
        } else {
          val v1 = convertPolyglotValueToRawValue(v, t.cloneAndRemoveProp(Rql2IsNullableTypeProperty()))
          raw.client.api.OptionValue(Some(v1))
        }
      case _: Rql2BoolType => raw.client.api.BoolValue(v.asBoolean())
      case _: Rql2StringType => raw.client.api.StringValue(v.asString())
      case _: Rql2ByteType => raw.client.api.ByteValue(v.asByte())
      case _: Rql2ShortType => raw.client.api.ShortValue(v.asShort())
      case _: Rql2IntType => raw.client.api.IntValue(v.asInt())
      case _: Rql2LongType => raw.client.api.LongValue(v.asLong())
      case _: Rql2FloatType => raw.client.api.FloatValue(v.asFloat())
      case _: Rql2DoubleType => raw.client.api.DoubleValue(v.asDouble())
      case _: Rql2DecimalType =>
        val bg = BigDecimal(v.asString())
        raw.client.api.DecimalValue(bg)
      case _: Rql2DateType =>
        val date = v.asDate()
        raw.client.api.DateValue(date)
      case _: Rql2TimeType =>
        val time = v.asTime()
        raw.client.api.TimeValue(time)
      case _: Rql2TimestampType =>
        val localDate = v.asDate()
        val localTime = v.asTime()
        raw.client.api.TimestampValue(localDate.atTime(localTime))
      case _: Rql2IntervalType =>
        val duration = v.asDuration()
        raw.client.api.IntervalValue(
          0,
          0,
          0,
          duration.toDaysPart.intValue(),
          duration.toHoursPart,
          duration.toMinutesPart,
          duration.toSecondsPart,
          duration.toMillisPart
        )
      case _: Rql2BinaryType =>
        val bufferSize = v.getBufferSize.toInt
        val byteArray = new Array[Byte](bufferSize)
        for (i <- 0 until bufferSize) {
          byteArray(i) = v.readBufferByte(i)
        }
        raw.client.api.BinaryValue(byteArray)
      case Rql2RecordType(atts, _) =>
        val vs = atts.map(att => convertPolyglotValueToRawValue(v.getMember(att.idn), att.tipe))
        raw.client.api.RecordValue(vs)
      case Rql2ListType(innerType, _) =>
        val seq = mutable.ArrayBuffer[raw.client.api.Value]()
        for (i <- 0L until v.getArraySize) {
          val v1 = v.getArrayElement(i)
          seq.append(convertPolyglotValueToRawValue(v1, innerType))
        }
        raw.client.api.ListValue(seq)
      case Rql2IterableType(innerType, _) =>
        val seq = mutable.ArrayBuffer[raw.client.api.Value]()
        val it = v.getIterator
        while (it.hasIteratorNextElement) {
          val v1 = it.getIteratorNextElement
          seq.append(convertPolyglotValueToRawValue(v1, innerType))
        }
        if (it.canInvokeMember("close")) {
          val callable = it.getMember("close")
          callable.execute()
        }
        raw.client.api.IterableValue(seq)
      case Rql2OrType(tipes, _) =>
        val idx = v.getMember("index").asInt()
        val v1 = v.getMember("value")
        val tipe = tipes(idx)
        convertPolyglotValueToRawValue(v1, tipe)
      case Rql2LocationType(_) =>
        val url = v.asString
        assert(v.hasMembers);
        val members = v.getMemberKeys
        val settings = mutable.Map.empty[LocationSettingKey, LocationSettingValue]
        val keys = members.iterator()
        while (keys.hasNext) {
          val key = keys.next()
          val tv = v.getMember(key)
          val value =
            if (tv.isNumber) LocationIntSetting(tv.asInt)
            else if (tv.isBoolean) LocationBooleanSetting(tv.asBoolean)
            else if (tv.isString) LocationStringSetting(tv.asString)
            else if (tv.hasBufferElements) {
              val bufferSize = tv.getBufferSize.toInt
              val byteArray = new Array[Byte](bufferSize)
              for (i <- 0 until bufferSize) {
                byteArray(i) = tv.readBufferByte(i)
              }
              LocationBinarySetting(byteArray)
            } else if (tv.isDuration) LocationDurationSetting(tv.asDuration())
            else if (tv.hasArrayElements) {
              // in the context of a location, it's int-array for sure
              val size = tv.getArraySize
              val array = new Array[Int](size.toInt)
              for (i <- 0L until size) {
                array(i.toInt) = tv.getArrayElement(i).asInt
              }
              LocationIntArraySetting(array)
            } else if (tv.hasHashEntries) {
              // kv settings
              val iterator = tv.getHashEntriesIterator
              val keyValues = mutable.ArrayBuffer.empty[(String, String)]
              while (iterator.hasIteratorNextElement) {
                val kv = iterator.getIteratorNextElement // array with two elements: key and value
                val key = kv.getArrayElement(0).asString
                val value = kv.getArrayElement(1).asString
                keyValues += ((key, value))
              }
              LocationKVSetting(keyValues)
            } else ???
          settings.put(LocationSettingKey(key), value)
        }
        LocationValue(LocationDescription(url, settings.toMap))
    }
  }

  override def execute(
      source: String,
      environment: ProgramEnvironment,
      maybeDecl: Option[String],
      outputStream: OutputStream
  ): ExecutionResponse = {
    // TODO (msb): This is not handling Validation vs Runtime errors!
    val ctx = buildTruffleContext(environment, maybeOutputStream = Some(outputStream))
    ctx.initialize("rql")
    ctx.enter()
    try {
      val (v, tipe) = maybeDecl match {
        case Some(decl) =>
          val truffleSource = Source
            .newBuilder("rql", source, "unnamed")
            .cached(false) // Disable code caching because of the inferrer.
            .build()
          ctx.eval(truffleSource)
          val bindings = ctx.getBindings("rql")
          val f = bindings.getMember(decl)
          val funType = {
            val rawType = ctx.getPolyglotBindings.getMember("@type:" + decl).asString()
            val ParseTypeSuccess(tipe: FunType) = parseType(rawType, environment.user, internal = true)
            tipe
          }
          // Some typechecking
          val namedArgs = funType.os.map(arg => arg.i -> arg.t).toMap
          val (optionalArgs, mandatoryArgs) = environment.maybeArguments match {
            case Some(args) =>
              val (optional, mandatory) = args.partition { case (idn, _) => namedArgs.contains(idn) }
              (optional.map(arg => arg._1 -> arg._2).toMap, mandatory.map(_._2))
            case None => (Map.empty[String, ParamValue], Array.empty[ParamValue])
          }
          if (mandatoryArgs.length != funType.ms.size) {
            return ExecutionRuntimeFailure("missing mandatory arguments")
          }
          val mandatoryPolyglotArguments = mandatoryArgs.map(arg => javaValueOf(arg, ctx))
          // by order of the optional arguments in the FunType:
          val optionalPolyglotArguments = funType.os.map { arg =>
            optionalArgs.get(arg.i) match {
              // if the argument is provided, use it
              case Some(paramValue) => javaValueOf(paramValue, ctx)
              // else, the argument has a default value stored in `f`. Use it.
              case None => f.invokeMember("default_" + arg.i)
            }
          }
          val result = f.execute(mandatoryPolyglotArguments ++ optionalPolyglotArguments: _*)
          val tipe = funType.r
          (result, tipe)
        case None =>
          val truffleSource = Source
            .newBuilder("rql", source, "unnamed")
            .cached(false) // Disable code caching because of the inferrer.
            .build()
          val result = ctx.eval(truffleSource)
          val rawType = ctx.getPolyglotBindings.getMember("@type").asString()
          val ParseTypeSuccess(tipe) = parseType(rawType, environment.user, internal = true)
          (result, tipe)
      }

      environment.options
        .get("output-format")
        .map(_.toLowerCase) match {
        case Some("csv") =>
          if (!CsvPackage.outputWriteSupport(tipe)) {
            return ExecutionRuntimeFailure("unsupported type");
          }
          val programContext = getProgramContext(environment.user, environment)
          val windowsLineEnding = environment.options.get("windows-line-ending") match {
            case Some("true") => true
            case _ => programContext.settings.config.getBoolean("raw.compiler.windows-line-ending")
          }
          val lineSeparator = if (windowsLineEnding) "\r\n" else "\n"
          val csvWriter = new Rql2CsvWriter(outputStream, lineSeparator)
          try {
            csvWriter.write(v, tipe.asInstanceOf[Rql2TypeWithProperties])
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          } finally {
            withSuppressNonFatalException(csvWriter.close())
          }
        case Some("json") =>
          if (!JsonPackage.outputWriteSupport(tipe)) {
            return ExecutionRuntimeFailure("unsupported type");
          }
          val w = new Rql2JsonWriter(outputStream)
          try {
            w.write(v, tipe.asInstanceOf[Rql2TypeWithProperties])
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          } finally {
            withSuppressNonFatalException(w.close())
          }
        case Some("text") =>
          val w = new PolyglotTextWriter(outputStream)
          if (!StringPackage.outputWriteSupport(tipe)) {
            return ExecutionRuntimeFailure("unsupported type");
          }
          try {
            w.writeValue(v)
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          }
        case Some("binary") =>
          if (!BinaryPackage.outputWriteSupport(tipe)) {
            return ExecutionRuntimeFailure("unsupported type")
          }
          val w = new PolyglotBinaryWriter(outputStream)
          try {
            w.writeValue(v)
            ExecutionSuccess
          } catch {
            case ex: IOException => ExecutionRuntimeFailure(ex.getMessage)
          }
        case _ => ExecutionRuntimeFailure("unknown output format")
      }
    } catch {
      case ex: PolyglotException =>
        if (ex.isInterrupted) {
          throw new InterruptedException()
        } else if (ex.isGuestException) {
          val err = ex.getGuestObject
          if (err != null && err.hasMembers && err.hasMember("errors")) {
            val errorsValue = err.getMember("errors")
            val errors = (0L until errorsValue.getArraySize).map { i =>
              val errorValue = errorsValue.getArrayElement(i)
              val message = errorValue.asString
              val positions = (0L until errorValue.getArraySize).map { j =>
                val posValue = errorValue.getArrayElement(j)
                val beginValue = posValue.getMember("begin")
                val endValue = posValue.getMember("end")
                val begin = ErrorPosition(beginValue.getMember("line").asInt, beginValue.getMember("column").asInt)
                val end = ErrorPosition(endValue.getMember("line").asInt, endValue.getMember("column").asInt)
                ErrorRange(begin, end)
              }
              ErrorMessage(message, positions.to)
            }
            ExecutionValidationFailure(errors.to)
          } else {
            ExecutionRuntimeFailure(ex.getMessage)
          }
        } else {
          // Unexpected error. For now we throw the PolyglotException.
          throw ex
        }
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

  override def formatCode(
      source: String,
      environment: ProgramEnvironment,
      maybeIndent: Option[Int],
      maybeWidth: Option[Int]
  ): FormatCodeResponse = {
    val programContext = getProgramContext(environment.user, environment)
    try {
      val pretty = new SourceCommentsPrettyPrinter(maybeIndent, maybeWidth)
      pretty.prettyCode(source) match {
        case Right(code) => FormatCodeResponse(Some(code), List.empty)
        case Left((err, pos)) => FormatCodeResponse(None, parseError(err, pos))
      }
    } catch {
      case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
    }
  }

  override def dotAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.dotAutoComplete(source, environment, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left((err, pos)) => AutoCompleteResponse(Array.empty, parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def wordAutoComplete(
      source: String,
      environment: ProgramEnvironment,
      prefix: String,
      position: Pos
  ): AutoCompleteResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.wordAutoComplete(source, environment, prefix, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left((err, pos)) => AutoCompleteResponse(Array.empty, parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def hover(source: String, environment: ProgramEnvironment, position: Pos): HoverResponse = {
    /*
    withLspTree(source, lspService => lspService.hover(source, environment, position)) match {
      case Right(value) => value
      case Left((err, pos)) => HoverResponse(None, parseError(err, pos))
    }
     */
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.hover(source, environment, position))(programContext) match {
            case Right(value) => value
            case Left((err, pos)) => HoverResponse(None, parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def rename(source: String, environment: ProgramEnvironment, position: Pos): RenameResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.rename(source, environment, position))(programContext) match {
            case Right(value) => value
            case Left((err, pos)) => RenameResponse(Array.empty, parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def goToDefinition(
      source: String,
      environment: ProgramEnvironment,
      position: Pos
  ): GoToDefinitionResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(source, lspService => lspService.definition(source, environment, position))(
            programContext
          ) match {
            case Right(value) => value
            case Left((err, pos)) => GoToDefinitionResponse(None, parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def validate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          withLspTree(
            source,
            lspService => {
              val response = lspService.validate
              if (response.errors.isEmpty) {
                // The "flexible" tree did not find any semantic errors.
                // So now we should parse with the "strict" parser/analyzer to get a proper tree and check for errors
                // in that one.
                val tree = new TreeWithPositions(source, ensureTree = false, frontend = true)(programContext)
                if (tree.valid) {
                  ValidateResponse(List.empty)
                } else {
                  ValidateResponse(tree.errors)
                }
              } else {
                // The "flexible" tree found some semantic errors, so report only those.
                response
              }
            }
          )(programContext) match {
            case Right(value) => value
            case Left((err, pos)) => ValidateResponse(parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  override def aiValidate(source: String, environment: ProgramEnvironment): ValidateResponse = {
    withTruffleContext(
      environment,
      _ => {
        val programContext = getProgramContext(environment.user, environment)
        try {
          // Will analyze the code and return only unknown declarations errors.
          val positions = new Positions()
          val parser = new FrontendSyntaxAnalyzer(positions)
          parser.parse(source) match {
            case Right(program) =>
              val sourceProgram = program.asInstanceOf[SourceProgram]
              val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
                sourceProgram
              )
              val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])

              // Selecting only a subset of the errors
              val selection = analyzer.errors.filter {
                // For the case of a function that does not exist in a package
                case UnexpectedType(_, PackageType(_), ExpectedProjType(_), _, _) => true
                case _: UnknownDecl => true
                case _: OutputTypeRequiredForRecursiveFunction => true
                case _: UnexpectedOptionalArgument => true
                case _: NoOptionalArgumentsExpected => true
                case _: KeyNotComparable => true
                case _: ItemsNotComparable => true
                case _: MandatoryArgumentAfterOptionalArgument => true
                case _: RepeatedFieldNames => true
                case _: UnexpectedArguments => true
                case _: MandatoryArgumentsMissing => true
                case _: RepeatedOptionalArguments => true
                case _: PackageNotFound => true
                case _: NamedParameterAfterOptionalParameter => true
                case _: ExpectedTypeButGotExpression => true
                case _ => false
              }
              ValidateResponse(formatErrors(selection, positions))
            case Left((err, pos)) => ValidateResponse(parseError(err, pos))
          }
        } catch {
          case NonFatal(t) => throw new CompilerServiceException(t, programContext.dumpDebugInfo)
        }
      }
    )
  }

  private def withLspTree[T](source: String, f: CompilerLspService => T)(
      implicit programContext: base.ProgramContext
  ): Either[(String, Position), T] = {
    // Parse tree with dedicated parser, which is more lose and tries to obtain an AST even with broken code.
    val positions = new Positions()
    val parser = new LspSyntaxAnalyzer(positions)
    parser.parse(source).right.map { program =>
      // Manually instantiate an analyzer to create a "flexible tree" that copes with broken code.
      val sourceProgram = program.asInstanceOf[SourceProgram]
      val kiamaTree = new org.bitbucket.inkytonik.kiama.relation.Tree[SourceNode, SourceProgram](
        sourceProgram,
        shape = EnsureTree // The LSP parser can create "cloned nodes" so this protects it.
      )
      // Do not perform any validation on errors as we fully expect the tree to be "broken" in most cases.
      val analyzer = new SemanticAnalyzer(kiamaTree)(programContext.asInstanceOf[ProgramContext])
      // Handle the LSP request.
      val lspService = new CompilerLspService(
        analyzer,
        positions,
        n => SourcePrettyPrinter.format(n)
      )(programContext.asInstanceOf[ProgramContext])
      f(lspService)
    }
  }

  private def parseError(error: String, position: Position): List[ErrorMessage] = {
    val range = ErrorRange(ErrorPosition(position.line, position.column), ErrorPosition(position.line, position.column))
    List(ErrorMessage(error, List(range)))
  }

  private def formatErrors(errors: Seq[BaseError], positions: Positions): List[ErrorMessage] = {
    errors.map { err =>
      val ranges = positions.getStart(err.node) match {
        case Some(begin) =>
          val Some(end) = positions.getFinish(err.node)
          List(ErrorRange(ErrorPosition(begin.line, begin.column), ErrorPosition(end.line, end.column)))
        case _ => List.empty
      }
      ErrorMessage(ErrorsPrettyPrinter.format(err), ranges)
    }.toList
  }

  override def doStop(): Unit = {
    compilerContextCaches.values.foreach(compilerContext => compilerContext.inferrer.stop())
    credentials.stop()
    engine.close(true)
  }

  private def javaValueOf(paramValue: ParamValue, ctx: Context) = {
    val code: String = paramValue match {
      case ParamNull() => "let x: undefined = null in x"
      case ParamByte(v) => s"let x: byte = ${v}b in x"
      case ParamShort(v) => s"let x: short = ${v}s in x"
      case ParamInt(v) => s"let x: int = $v in x"
      case ParamLong(v) => s"let x: long = ${v}L in x"
      case ParamFloat(v) => s"let x: float = ${v}f in x"
      case ParamDouble(v) => s"let x: double = $v in x"
      case ParamBool(v) => s"let x: bool = $v in x"
      case ParamString(v) => s"""let x: string = "$v" in x"""
      case ParamDecimal(v) => s"""let x: decimal = Decimal.From("$v") in x"""
      case ParamDate(v) => s"""let x: date = Date.Build(${v.getYear}, ${v.getMonthValue}, ${v.getDayOfMonth}) in x"""
      case ParamTime(v) =>
        s"""let x: time = Time.Build(${v.getHour}, ${v.getMinute}, millis=${v.getNano / 1000000}) in x"""
      case ParamTimestamp(v) =>
        s"""let x: timestamp = Timestamp.Build(${v.getYear}, ${v.getMonthValue}, ${v.getDayOfMonth}, ${v.getHour}, ${v.getMinute}, millis=${v.getNano / 1000000}) in x"""
      case ParamInterval(v) =>
        s"""let x: interval = Interval.Build(days=${v.toDaysPart}, hours=${v.toHoursPart}, minutes=${v.toMinutesPart}, seconds=${v.toSecondsPart}, millis=${v.toNanosPart / 1000000}) in x"""
    }
    val value = ctx.eval("rql", code)
    ctx.asValue(value)
  }

  private def buildTruffleContext(
      environment: ProgramEnvironment,
      maybeOutputStream: Option[OutputStream] = None
  ): Context = {
    // Add environment settings as hardcoded environment variables.
    val ctxBuilder = Context
      .newBuilder("rql", "python")
      .engine(engine)
      .environment("RAW_SETTINGS", settings.renderAsString)
      .environment("RAW_USER", environment.user.uid.toString)
      .environment("RAW_TRACE_ID", environment.user.uid.toString)
      .environment("RAW_SCOPES", environment.scopes.mkString(","))
      .allowExperimentalOptions(true)
      .allowPolyglotAccess(PolyglotAccess.ALL)
    environment.options.get("staged-compiler").foreach { stagedCompiler =>
      ctxBuilder.option("rql.staged-compiler", stagedCompiler)
    }
    maybeOutputStream.foreach(os => ctxBuilder.out(os))
    val ctx = ctxBuilder.build()
    ctx
  }

  private def withTruffleContext[T](
      environment: ProgramEnvironment,
      f: Context => T
  ): T = {
    val ctx = buildTruffleContext(environment)
    ctx.initialize("rql")
    ctx.enter()
    try {
      f(ctx)
    } finally {
      ctx.leave()
      ctx.close()
    }
  }

}

sealed trait ParseResponse
final case class ParseSuccess(program: SourceProgram) extends ParseResponse
final case class ParseFailure(error: String, pos: ErrorPosition) extends ParseResponse

sealed trait ParseTypeResponse
final case class ParseTypeSuccess(tipe: Type) extends ParseTypeResponse
final case class ParseTypeFailure(error: String) extends ParseTypeResponse

sealed trait GetTypeResponse
final case class GetTypeFailure(errors: List[ErrorMessage]) extends GetTypeResponse
final case class GetTypeSuccess(tipe: Option[Type]) extends GetTypeResponse
