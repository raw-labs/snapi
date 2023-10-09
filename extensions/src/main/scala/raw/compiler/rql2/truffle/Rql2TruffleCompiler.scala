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

package raw.compiler.rql2.truffle

import com.oracle.truffle.api.Truffle
import com.oracle.truffle.api.frame.{FrameDescriptor, FrameSlotKind}
import com.oracle.truffle.api.interop.InteropLibrary
import com.oracle.truffle.api.nodes.RootNode
import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.util.Entity
import org.graalvm.polyglot.Context
import raw.compiler.base.source.{BaseNode, Type}
import raw.compiler.base.{BaseTree, CompilerContext}
import raw.compiler.common.source._
import raw.compiler.rql2.Rql2TypeUtils.removeProp
import raw.compiler.rql2._
import raw.compiler.rql2.api._
import raw.compiler.rql2.builtin._
import raw.compiler.rql2.source._
import raw.compiler.rql2.truffle.Rql2TruffleCompiler.WINDOWS_LINE_ENDING
import raw.compiler.rql2.truffle.builtin.{CsvWriter, JsonIO, TruffleBinaryWriter}
import raw.compiler.truffle.TruffleCompiler
import raw.compiler.{base, CompilerException, ErrorMessage}
import raw.runtime._
import raw.runtime.interpreter._
import raw.runtime.truffle._
import raw.runtime.truffle.ast._
import raw.runtime.truffle.ast.controlflow._
import raw.runtime.truffle.ast.expressions.binary._
import raw.runtime.truffle.ast.expressions.function._
import raw.runtime.truffle.ast.expressions.literals._
import raw.runtime.truffle.ast.expressions.option.OptionNoneNode
import raw.runtime.truffle.ast.expressions.record.RecordProjNodeGen
import raw.runtime.truffle.ast.expressions.unary._
import raw.runtime.truffle.ast.io.binary.BinaryWriterNode
import raw.runtime.truffle.ast.io.csv.writer.{CsvIterableWriterNode, CsvListWriterNode}
import raw.runtime.truffle.ast.io.json.writer.JsonWriterNodeGen
import raw.runtime.truffle.ast.local._
import raw.runtime.truffle.runtime.function.Function
import raw.runtime.truffle.runtime.generator.GeneratorLibrary
import raw.runtime.truffle.runtime.iterable.IterableLibrary
import raw.runtime.truffle.runtime.list.ListLibrary
import raw.runtime.truffle.runtime.option.OptionLibrary
import raw.runtime.truffle.runtime.or.OrObject
import raw.runtime.truffle.runtime.primitives._
import raw.runtime.truffle.runtime.tryable.TryableLibrary

import java.util.UUID
import scala.collection.{mutable, JavaConverters}

object Rql2TruffleCompiler {
  private val WINDOWS_LINE_ENDING = "raw.compiler.windows-line-ending"
}

final case class TruffleEntrypoint(context: Context, node: RootNode, frameDescriptor: FrameDescriptor)
    extends Entrypoint {
  def target = node
}

class Rql2TruffleCompiler(implicit compilerContext: CompilerContext)
    extends Compiler
    with TruffleCompiler[SourceNode, SourceProgram, Exp] {

  override def supportsCaching: Boolean = false

  override protected def doEval(
      tree: BaseTree[SourceNode, SourceProgram, Exp]
  )(implicit programContext: raw.compiler.base.ProgramContext): Value = {
    // FIXME (msb): This should pretty print the tree, then call Context.eval.
    //              We shouldn't have access to doCompile and hence not do be rawLanguageAsNull as null, which is broken!
    val context = Context
      .newBuilder(RawLanguage.ID)
      .environment("RAW_USER", programContext.runtimeContext.environment.user.uid.toString)
      .environment("RAW_TRACE_ID", programContext.runtimeContext.environment.user.uid.toString)
      .environment("RAW_SCOPES", programContext.runtimeContext.environment.scopes.mkString(","))
      .build()
    context.initialize(RawLanguage.ID)
    context.enter()
    try {
      val TruffleEntrypoint(_, node, _) = doCompile(tree, null).asInstanceOf[TruffleEntrypoint]
      val target = Truffle.getRuntime.createDirectCallNode(node.getCallTarget)
      val res = target.call()
      convertAnyToValue(res, tree.rootType.get)
    } finally {
      // We explicitly created and then entered the context during code emission.
      // Now we explicitly leave and close the context.
      context.leave()
      context.close()
    }
  }

  override def parseAndValidate(source: String, maybeDecl: Option[String])(
      implicit programContext: base.ProgramContext
  ): Either[List[ErrorMessage], SourceProgram] = {
    buildInputTree(source).right.flatMap { tree =>
      val Rql2Program(methods, me) = tree.root

      maybeDecl match {
        case None =>
          if (programContext.runtimeContext.maybeArguments.nonEmpty) {
            // arguments are given while no method to execute is
            // provided.
            throw new CompilerException("arguments found")
          }

          if (me.isEmpty) {
            // no method, but also no bottom expression. This isn't
            // executable.
            throw new CompilerException("no expression found")
          }

          val e = me.get
          Right(Rql2Program(methods, Some(e)))
        case Some(decl) => tree.description.expDecls.get(decl) match {
            case Some(descriptions) =>
              if (descriptions.size > 1) throw new CompilerException("multiple declarations found")
              val description = descriptions.head
              val params = description.params.get
              val mandatory = params.collect { case p if p.required => p.idn }.toSet
              val args = programContext.runtimeContext.maybeArguments.get
              val programArgs = args.map {
                case (k, v) =>
                  val e = v match {
                    case _: ParamNull => NullConst()
                    case _: ParamByte => EnvironmentPackageBuilder.Parameter(Rql2ByteType(), StringConst(k))
                    case _: ParamShort => EnvironmentPackageBuilder.Parameter(Rql2ShortType(), StringConst(k))
                    case _: ParamInt => EnvironmentPackageBuilder.Parameter(Rql2IntType(), StringConst(k))
                    case _: ParamLong => EnvironmentPackageBuilder.Parameter(Rql2LongType(), StringConst(k))
                    case _: ParamFloat => EnvironmentPackageBuilder.Parameter(Rql2FloatType(), StringConst(k))
                    case _: ParamDouble => EnvironmentPackageBuilder.Parameter(Rql2DoubleType(), StringConst(k))
                    case _: ParamDecimal => EnvironmentPackageBuilder.Parameter(Rql2DecimalType(), StringConst(k))
                    case _: ParamBool => EnvironmentPackageBuilder.Parameter(Rql2BoolType(), StringConst(k))
                    case _: ParamString => EnvironmentPackageBuilder.Parameter(Rql2StringType(), StringConst(k))
                    case _: ParamDate => EnvironmentPackageBuilder.Parameter(Rql2DateType(), StringConst(k))
                    case _: ParamTime => EnvironmentPackageBuilder.Parameter(Rql2TimeType(), StringConst(k))
                    case _: ParamTimestamp => EnvironmentPackageBuilder.Parameter(Rql2TimestampType(), StringConst(k))
                    case _: ParamInterval => EnvironmentPackageBuilder.Parameter(Rql2IntervalType(), StringConst(k))
                  }
                  // Build the argument node.
                  // If argument is mandatory, then according to RQL2's language definition, the argument is not named.
                  // However, if the argument os optional, the name is added.
                  FunAppArg(e, if (mandatory.contains(k)) None else Some(k))
              }
              Right(Rql2Program(methods, Some(FunApp(IdnExp(IdnUse(decl)), programArgs.toVector))))
            case None => throw new CompilerException("declaration not found")
          }
      }
    }
  }

  private def convertAnyToValue(v: Any, t: Type): Value = t match {
    case t: Rql2TypeWithProperties if t.props.contains(Rql2IsTryableTypeProperty()) =>
      val triables = TryableLibrary.getFactory.getUncached(v)
      if (triables.isSuccess(v)) {
        TryValue(Right(convertAnyToValue(triables.success(v), removeProp(t, Rql2IsTryableTypeProperty()))))
      } else {
        TryValue(Left(triables.failure(v)))
      }
    case t: Rql2TypeWithProperties if t.props.contains(Rql2IsNullableTypeProperty()) =>
      val options = OptionLibrary.getFactory.getUncached(v)
      if (options.isDefined(v)) {
        OptionValue(Some(convertAnyToValue(options.get(v), removeProp(t, Rql2IsNullableTypeProperty()))))
      } else {
        OptionValue(None)
      }
    case _: Rql2BoolType => BoolValue(v.asInstanceOf[java.lang.Boolean])
    case _: Rql2StringType => StringValue(v.asInstanceOf[java.lang.String])
    case _: Rql2BinaryType =>
      val v1 = v.asInstanceOf[BinaryObject]
      val vs = mutable.ArrayBuffer[Byte]()
      for (i <- 0 until java.lang.reflect.Array.getLength(v1.getBytes)) {
        vs.append(java.lang.reflect.Array.getByte(v1, i))
      }
      BinaryValue(vs.toArray)
    case _: Rql2ByteType => ByteValue(v.asInstanceOf[java.lang.Byte])
    case _: Rql2ShortType => ShortValue(v.asInstanceOf[java.lang.Short])
    case _: Rql2IntType => IntValue(v.asInstanceOf[java.lang.Integer])
    case _: Rql2LongType => LongValue(v.asInstanceOf[java.lang.Long])
    case _: Rql2FloatType => FloatValue(v.asInstanceOf[java.lang.Float])
    case _: Rql2DoubleType => DoubleValue(v.asInstanceOf[java.lang.Double])
    case _: Rql2DecimalType => DecimalValue(v.asInstanceOf[DecimalObject].getBigDecimal)
    case _: Rql2LocationType => LocationValue(v.asInstanceOf[LocationObject].getLocationDescription)
    case _: Rql2DateType => DateValue(v.asInstanceOf[DateObject].getDate)
    case _: Rql2TimeType => TimeValue(v.asInstanceOf[TimeObject].getTime)
    case _: Rql2TimestampType => TimestampValue(v.asInstanceOf[TimestampObject].getTimestamp)
    case _: Rql2IntervalType =>
      val v1 = v.asInstanceOf[IntervalObject]
      IntervalValue(
        v1.getYears,
        v1.getMonths,
        v1.getWeeks,
        v1.getDays,
        v1.getHours,
        v1.getMinutes,
        v1.getSeconds,
        v1.getMillis
      )
    //      case _: Rql2RegexType => RegexValue(v.asInstanceOf[scala.util.matching.Regex]) // a.z I think we don't have it in RQL2, yes?
    case _: Rql2UndefinedType => NothingValue()
    case Rql2RecordType(atts, _) =>
      val records = InteropLibrary.getFactory.getUncached(v)
      val keys = records.getMembers(v)
      val keysLib = InteropLibrary.getFactory.getUncached(keys)
      RecordValue(atts.zipWithIndex.map {
        case (att, idx) => convertAnyToValue(
            records.readMember(v, keysLib.readArrayElement(keys, idx).asInstanceOf[String]),
            att.tipe
          )
      })
    case Rql2ListType(t1, _) =>
      val lists = ListLibrary.getFactory.getUncached(v)
      val values = for (i <- 0 until lists.size(v).asInstanceOf[Int]) yield lists.get(v, i)
      ListValue(values.map(v1 => convertAnyToValue(v1, t1)))
    case Rql2IterableType(t1, _) =>
      val iterables = IterableLibrary.getFactory.getUncached(v)
      val generator = iterables.getGenerator(v)
      val generators = GeneratorLibrary.getFactory.getUncached(generator)
      generators.init(generator)
      val vs = mutable.ArrayBuffer[Any]()
      while (generators.hasNext(generator)) {
        vs.append(generators.next(generator))
      }
      generators.close(generator)
      IterableValue(vs.map(v1 => convertAnyToValue(v1, t1)))
    case Rql2OrType(ts, _) =>
      val orObj = v.asInstanceOf[OrObject]
      OrValue(Seq(convertAnyToValue(orObj.getValue, ts(orObj.getIndex))))
  }

  // FIXME (msb): Overridding this here shows me inheritance isn't right. parent Compiler implementing "too much"!!!
  override def prettyPrintOutput(node: BaseNode): String = {
    SourcePrettyPrinter.format(node)
  }

  override def doEmit(signature: String, program: SourceProgram, rawLanguageAsAny: Any)(
      implicit programContext: raw.compiler.base.ProgramContext
  ): Entrypoint = {

    val rawLanguage = rawLanguageAsAny.asInstanceOf[RawLanguage]

    logger.debug(s"Output final program is:\n${prettyPrintOutput(program)}")

//    // We explicitly create and then enter the context during code emission.
//    // This context will be left on close in the TruffleProgramOutputWriter.
//    val ctx: Context = Context.newBuilder(RawLanguage.ID).build()
//    ctx.initialize(RawLanguage.ID)
//    ctx.enter()
    val ctx = null

    val tree = new Tree(program)(programContext.asInstanceOf[ProgramContext])
    val emitter = new TruffleEmitterImpl(tree, rawLanguage)(programContext.asInstanceOf[ProgramContext])
    val Rql2Program(methods, me) = tree.root
    val dataType = tree.analyzer.tipe(me.get)
    val outputFormat = programContext.runtimeContext.environment.options.getOrElse("output-format", defaultOutputFormat)
    outputFormat match {
      case "csv" => if (!CsvPackage.outputWriteSupport(dataType)) throw new CompilerException("unsupported type")
      case "json" => if (!JsonPackage.outputWriteSupport(dataType)) throw new CompilerException("unsupported type")
      case "text" => if (!StringPackage.outputWriteSupport(dataType)) throw new CompilerException("unsupported type")
      case "binary" => if (!BinaryPackage.outputWriteSupport(dataType)) throw new CompilerException("unsupported type")
      case null | "" => // stage compilation
      case _ => throw new CompilerException("unknown output format")
    }

    assert(me.isDefined)
    val bodyExp = me.get

    emitter.addScope() // we need a scope for potential function declarations
    val functionDeclarations = methods.map(emitter.emitMethod).toArray
    val body = emitter.recurseExp(bodyExp)
    val bodyExpNode =
      if (functionDeclarations.nonEmpty) {
        new ExpBlockNode(functionDeclarations, body)
      } else {
        body
      }
    val frameDescriptor = emitter.dropScope()

    // Wrap output node

    val rootNode: RootNode = outputFormat match {
      case "csv" =>
        val windowsLineEnding = programContext.runtimeContext.environment.options.get("windows-line-ending") match {
          case Some("true") => true
          case Some("false") => false
          case None => programContext.settings.getBoolean(WINDOWS_LINE_ENDING)
        }
        val lineSeparator = if (windowsLineEnding) "\r\n" else "\n"
        dataType match {
          case Rql2IterableType(Rql2RecordType(atts, rProps), iProps) =>
            assert(rProps.isEmpty)
            assert(iProps.isEmpty)
            new ProgramStatementNode(
              rawLanguage,
              frameDescriptor,
              new CsvIterableWriterNode(
                bodyExpNode,
                CsvWriter(atts.map(_.tipe), rawLanguage),
                atts.map(_.idn).toArray,
                lineSeparator
              )
            )
          case Rql2ListType(Rql2RecordType(atts, rProps), iProps) =>
            assert(rProps.isEmpty)
            assert(iProps.isEmpty)
            new ProgramStatementNode(
              rawLanguage,
              frameDescriptor,
              new CsvListWriterNode(
                bodyExpNode,
                CsvWriter(atts.map(_.tipe), rawLanguage),
                atts.map(_.idn).toArray,
                lineSeparator
              )
            )
        }
      case "json" => new ProgramStatementNode(
          rawLanguage,
          frameDescriptor,
          JsonWriterNodeGen.create(
            bodyExpNode,
            JsonIO.recurseJsonWriter(dataType.asInstanceOf[Rql2TypeWithProperties], rawLanguage)
          )
        )
      case "binary" =>
        val writer = TruffleBinaryWriter(dataType.asInstanceOf[Rql2BinaryType], rawLanguage)
        new ProgramStatementNode(
          rawLanguage,
          frameDescriptor,
          new BinaryWriterNode(bodyExpNode, writer)
        )
      case "text" =>
        val writer = TruffleBinaryWriter(dataType.asInstanceOf[Rql2StringType], rawLanguage)
        new ProgramStatementNode(
          rawLanguage,
          frameDescriptor,
          new BinaryWriterNode(bodyExpNode, writer)
        )
      case null | "" => new ProgramExpressionNode(
          rawLanguage,
          frameDescriptor,
          bodyExpNode
        )
      case _ => throw new CompilerException("unknown output format")
    }

    TruffleEntrypoint(ctx, rootNode, frameDescriptor)
  }

}

final case class SlotLocation(depth: Int, slot: Int)

class TruffleEmitterImpl(tree: Tree, val rawLanguage: RawLanguage)(implicit programContext: ProgramContext)
    extends TruffleEmitter
    with StrictLogging {

  private val uniqueID = UUID.randomUUID().toString.replace("-", "").replace("_", "")

  private var idnCounter = 0
  private val idnSlot = mutable.HashMap[Entity, String]()

  private val slotMapScope = mutable.ListBuffer[mutable.HashMap[Entity, Int]]()
  private val frameDescriptorBuilderScope = mutable.ListBuffer[FrameDescriptor.Builder]()
  private var funcCounter = 0

  private val funcMap = mutable.HashMap[Entity, String]()

  private val analyzer = tree.analyzer

  private val entityDepth = mutable.HashMap[Entity, Int]()

  private def setEntityDepth(e: Entity): Unit = {
    entityDepth.put(e, getCurrentDepth)
  }

  private def getEntityDepth(e: Entity): Int = {
    entityDepth(e)
  }

  private def getCurrentDepth: Int = {
    slotMapScope.length
  }
  private def getIdnName(entity: Entity): String = {
    idnSlot.getOrElseUpdate(entity, { idnCounter += 1; s"idn${uniqueID}_$idnCounter" })
  }

  override def addScope(): Unit = {
    slotMapScope.insert(0, mutable.HashMap[Entity, Int]())
    frameDescriptorBuilderScope.insert(0, FrameDescriptor.newBuilder())
  }

  override def dropScope(): FrameDescriptor = {
    slotMapScope.remove(0)
    val frameDescriptorBuilder = frameDescriptorBuilderScope.remove(0)
    frameDescriptorBuilder.build()
  }

  private def getFrameDescriptorBuilder: FrameDescriptor.Builder = {
    frameDescriptorBuilderScope.head
  }

  private def addSlot(entity: Entity, slot: Int): Unit = {
    slotMapScope.head.put(entity, slot)
  }

  def emitMethod(m: Rql2Method): StatementNode = {
    // methods are inserted in the top frame as local variables
    val entity = analyzer.entity(m.i)
    val fp = m.p
    val f = recurseFunProto(null, fp)
    val defaultArgs = for (p <- fp.ps) yield p.e.map(recurseExp).orNull
    val functionLiteralNode = new ClosureNode(f, defaultArgs.toArray)
    val slot = getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
    addSlot(entity, slot)
    WriteLocalVariableNodeGen.create(functionLiteralNode, slot, null)
  }

  private def findSlot(entity: Entity): SlotLocation = {
    var depth = 0
    var curSlot = slotMapScope(depth)
    while (true) {
      curSlot.get(entity) match {
        case Some(slot) =>
          logger.debug(s"slot for $entity found depth $depth and slot $slot")
          return SlotLocation(depth, slot)
        case None =>
          depth += 1
          curSlot = slotMapScope(depth)
      }
    }
    throw new AssertionError("Slot not found")
  }

  private def getFuncIdn(entity: Entity): String = {
    funcMap.getOrElseUpdate(entity, { funcCounter += 1; s"func${uniqueID}_$funcCounter" })
  }

  private def getLambdaFuncIdn: String = {
    funcCounter += 1
    s"func${uniqueID}_$funcCounter"
  }

  private def tipe(e: Exp): Type = analyzer.tipe(e)

  private def recurseLetDecl(d: LetDecl): StatementNode = d match {
    case LetBind(e, i, _) =>
      val entity = analyzer.entity(i)
      val rql2Type = tipe(e).asInstanceOf[Rql2Type]

      val slot = rql2Type match {
        case _: Rql2UndefinedType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: ExpType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2ByteType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Byte, getIdnName(entity), null)
        case _: Rql2ShortType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Int, getIdnName(entity), null)
        case _: Rql2IntType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Int, getIdnName(entity), null)
        case _: Rql2LongType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Long, getIdnName(entity), null)
        case _: Rql2FloatType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Float, getIdnName(entity), null)
        case _: Rql2DoubleType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Double, getIdnName(entity), null)
        case _: Rql2DecimalType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2BoolType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Boolean, getIdnName(entity), null)
        case _: Rql2StringType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2DateType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2TimeType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2TimestampType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2IntervalType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2BinaryType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2IterableType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2ListType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: FunType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2RecordType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: Rql2LocationType => getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
        case _: PackageType | _: PackageEntryType =>
          // allocate some space but it won't be used
          getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
      }
      addSlot(entity, slot)
      WriteLocalVariableNodeGen.create(recurseExp(e), slot, rql2Type)
    case LetFun(fp, i) =>
      val entity = analyzer.entity(i)
      val funcName = getFuncIdn(entity)
      val f = recurseFunProto(funcName, fp)
      val defaultArgs = for (p <- fp.ps) yield p.e.map(recurseExp).orNull
      val functionLiteralNode = new ClosureNode(f, defaultArgs.toArray)
      // Only then add to slot.
      val slot = getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
      addSlot(entity, slot)

      WriteLocalVariableNodeGen.create(functionLiteralNode, slot, null)
    case LetFunRec(i, fp) =>
      val entity = analyzer.entity(i)
      val funcName = getFuncIdn(entity)

      // Add first the slot to the frame.
      val slot = getFrameDescriptorBuilder.addSlot(FrameSlotKind.Object, getIdnName(entity), null)
      addSlot(entity, slot)

      val f = recurseFunProto(funcName, fp)
      val defaultArgs = for (p <- fp.ps) yield p.e.map(recurseExp).orNull
      val functionLiteralNode = new ClosureNode(f, defaultArgs.toArray)
      val stmt = WriteLocalVariableNodeGen.create(functionLiteralNode, slot, null)
      stmt
  }

  private def recurseFunProto(name: String, fp: FunProto): raw.runtime.truffle.runtime.function.Function = {
    val FunProto(ps, _, FunBody(e)) = fp
    addScope()

    ps.foreach(p => setEntityDepth(analyzer.entity(p.i)))

    val functionBody = recurseExp(e)
    val funcFrameDescriptor = dropScope()

    val functionRootBody = new ProgramExpressionNode(rawLanguage, funcFrameDescriptor, functionBody)
    val rootCallTarget = functionRootBody.getCallTarget
    val argNames = fp.ps.map(_.i.idn).toArray
    new Function(rootCallTarget, argNames)
  }

  override def recurseLambda(buildBody: () => ExpressionNode): ClosureNode = {
    val name = getLambdaFuncIdn

    //    val funcFrameDescriptorBuilder = FrameDescriptor.newBuilder()
    addScope()
    val functionBody = buildBody()
    logger.debug(s"For function $name the body is:\n${functionBody.toString}")
    //    val functionBody = recurseExp(e)(funcFrameDescriptorBuilder)
    val funcFrameDescriptor = dropScope()
    //    val funcFrameDescriptor = funcFrameDescriptorBuilder.build()

    val functionRootBody = new ProgramExpressionNode(rawLanguage, funcFrameDescriptor, functionBody)
    val rootCallTarget = functionRootBody.getCallTarget
    val f = new Function(rootCallTarget, Array("x"))
    new ClosureNode(f, Array.empty) // no default values for these lambdas
  }

  override def recurseExp(in: Exp): ExpressionNode = in match {
    case _ if tipe(in).isInstanceOf[PackageType] || tipe(in).isInstanceOf[PackageEntryType] =>
      new ZeroedConstNode(Rql2ByteType())
    case TypeExp(t: Rql2Type) => new ZeroedConstNode(t)
    case NullConst() => new OptionNoneNode(tipe(in))
    case BoolConst(v) => new BoolNode(v)
    case ByteConst(v) => new ByteNode(v)
    case ShortConst(v) => new ShortNode(v)
    case IntConst(v) => new IntNode(v)
    case LongConst(v) => new LongNode(v)
    case FloatConst(v) => new FloatNode(v)
    case DoubleConst(v) => new DoubleNode(v)
    case DecimalConst(v) => new DecimalNode(v)
    case StringConst(v) => new StringNode(v)
    case TripleQuotedStringConst(v) => new StringNode(v)
    case BinaryExp(And(), e1, e2) => new AndNode(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Or(), e1, e2) => new OrNode(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Plus(), e1, e2) => new PlusNode(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Sub(), e1, e2) => SubNodeGen.create(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Mult(), e1, e2) => MultNodeGen.create(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Mod(), e1, e2) => ModNodeGen.create(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Div(), e1, e2) => DivNodeGen.create(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Gt(), e1, e2) => new GtNode(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Ge(), e1, e2) => new GeNode(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Eq(), e1, e2) => new EqNode(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Neq(), e1, e2) => NotNodeGen.create(new EqNode(recurseExp(e1), recurseExp(e2)))
    case BinaryExp(Lt(), e1, e2) => new LtNode(recurseExp(e1), recurseExp(e2))
    case BinaryExp(Le(), e1, e2) => new LeNode(recurseExp(e1), recurseExp(e2))
    case BinaryConst(bytes) => new BinaryConstNode(bytes)
    case UnaryExp(Neg(), e) => NegNodeGen.create(recurseExp(e))
    case UnaryExp(Not(), e) => NotNodeGen.create(recurseExp(e))
    case IdnExp(idn) => analyzer.entity(idn) match {
        case b: MethodEntity =>
          // methods are inserted in the top frame as local variables
          val SlotLocation(_, slot) = findSlot(b)
          ReadLocalVariableNodeGen.create(slot, null)
        case b: LetBindEntity =>
          val SlotLocation(depth, slot) = findSlot(b)
          if (depth == 0) {
            ReadLocalVariableNodeGen.create(slot, tipe(b.b.e).asInstanceOf[Rql2Type])
          } else {
            ReadClosureVariableNodeGen.create(depth, slot, tipe(b.b.e).asInstanceOf[Rql2Type])
          }
        case f: LetFunEntity =>
          val SlotLocation(depth, slot) = findSlot(f)
          if (depth == 0) {
            ReadLocalVariableNodeGen.create(slot, null)
          } else {
            ReadClosureVariableNodeGen.create(depth, slot, analyzer.idnType(f.f.i).asInstanceOf[Rql2Type])
          }
        case f: LetFunRecEntity =>
          val SlotLocation(depth, slot) = findSlot(f)
          if (depth == 0) {
            ReadLocalVariableNodeGen.create(slot, null)
          } else {
            ReadClosureVariableNodeGen.create(depth, slot, analyzer.idnType(f.f.i).asInstanceOf[Rql2Type])
          }
        case f: FunParamEntity =>
          val depth = getCurrentDepth - getEntityDepth(f)
          if (depth == 0) {
            val tree.parent(FunProto(ps, _, _)) = f.f
            val idx = ps.indexOf(f.f)
            new ReadParamNode(idx)
          } else {
            val tree.parent(FunProto(ps, _, _)) = f.f
            val idx = ps.indexOf(f.f)
            new ReadParamClosureNode(depth, idx)
          }
      }
    case IfThenElse(e1, e2, e3) => new IfThenElseNode(recurseExp(e1), recurseExp(e2), recurseExp(e3))
    case Proj(r, f) => RecordProjNodeGen.create(recurseExp(r), new StringNode(f))
    case Let(decls, e) => new ExpBlockNode(decls.map(recurseLetDecl).toArray, recurseExp(e))
    case FunAbs(fp) =>
      val funcName = getLambdaFuncIdn
      val f = recurseFunProto(funcName, fp)
      val defaultArgs = for (p <- fp.ps) yield p.e.map(recurseExp).orNull
      new ClosureNode(f, defaultArgs.toArray)
    case f @ FunApp(e, args) if tipe(e).isInstanceOf[PackageEntryType] =>
      val t = tipe(f)
      val PackageEntryType(pkgName, entName) = tipe(e)
      programContext
        .asInstanceOf[ProgramContext]
        .getPackage(pkgName)
        .get
        .getEntries(entName)
        .collectFirst {
          // (az) this is a patch for scala to truffle conversion
          case e: raw.compiler.snapi.truffle.TruffleEntryExtension => e.toTruffle(
              t,
              JavaConverters.seqAsJavaList(args.map(arg => Rql2Arg(arg.e, analyzer.tipe(arg.e), arg.idn))),
              this
            )
          case e: TruffleEntryExtension =>
            e.toTruffle(t, args.map(arg => Rql2Arg(arg.e, analyzer.tipe(arg.e), arg.idn)), this)
        }
        .getOrElse(throw new Exception(s"Could not find package entry: $pkgName.$entName"))
    case FunApp(f, args) =>
      new InvokeNode(recurseExp(f), args.map(_.idn.orNull).toArray, args.map(arg => recurseExp(arg.e)).toArray)
  }

}
