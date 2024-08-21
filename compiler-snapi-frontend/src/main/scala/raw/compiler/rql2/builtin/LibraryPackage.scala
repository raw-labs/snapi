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

//package raw.compiler.rql2.builtin
//
//import com.typesafe.scalalogging.StrictLogging
//import raw.api.RawException
//import raw.compiler.{EntryDoc, ExampleDoc, L0, PackageDoc, ParamDoc, TypeDoc}
//import raw.compiler.L0.source.{CallPackageArgNode, L0Arg}
//import raw.compiler.base.source.Type
//import raw.compiler.base.{ExpDecl, ExpDeclParam}
//import raw.compiler.common.CommonCompilerProvider
//import raw.compiler.common.source.{Exp, VoidType}
//import raw.runtime.JvmEntrypoint
//import raw.compiler.rql2._
//import raw.compiler.rql2.source._
//import com.rawlabs.utils.sources.LocationDescription
//import com.rawlabs.utils.sources.bytestream.ByteStreamLocationProvider
//
//import java.nio.charset.StandardCharsets
//import java.util.concurrent.atomic.AtomicInteger
//import scala.collection.mutable
//import scala.util.control.NonFatal
//
//class LibraryPackage extends PackageExtension {
//
//  override def name: String = "Library"
//
//  override def docs: PackageDoc = PackageDoc("""Library of functions to load remote libraries of code.""")
//
//  override protected val entryExtensions: Map[String, ProgramContext => EntryExtension] = Map(
//    "Load" -> (programContext => new LoadLibraryEntry(programContext)),
//    "Use" -> (programContext => new UseLibraryEntry(programContext))
//  )
//
//}
//
//class LoadLibraryEntry extends L0EntryExtension with StrictLogging {
//
//  override def docs: EntryDoc = EntryDoc(
//    summary = """Loads a library.""",
//    params = List(ParamDoc("url", TypeDoc(List("string")), "The publicly-accessible URL where the library is hosted.")),
//    examples =
//      List(ExampleDoc("""// Assume a library exists at http://example.org/code.snapi with the following content:
//        |// a(v: int) = v * 2
//        |// b() = 1
//        |//
//        |// This library can then be used as:
//        |let MyLib = Library.Load("http://example.org/code.snapi"),
//        |    v1 = MyLib.a(2),
//        |    v2 = MyLib.b()
//        |in
//        |    v1 + v2 // Evaluates to 5""".stripMargin))
//  )
//
//  override def nrMandatoryParams: Int = 1
//
//  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
//    Right(ValueParam(string))
//  }
//
//  override def returnType(
//      mandatoryArgs: Seq[Arg],
//      optionalArgs: Seq[(String, Arg)],
//      varArgs: Seq[Arg]
//  ): Either[String, Type] = {
//    // FIXME (msb): This code does NOT handle cycles!!!
//
//    val location = getStringValue(mandatoryArgs(0))
//    val compiler = CommonCompilerProvider.apply("rql2")(programContext.compilerContext)
//    findPackage(location) match {
//      case Right(source) => compiler.buildInputTree(source)(programContext) match {
//          case Right(tree) => compiler.compile(tree.root)(programContext) match {
//              case Right(JvmEntrypoint(className)) =>
//                val entries = tree.description.expDecls.map {
//                  case (entryName, List(ExpDecl(Some(params), outType, _))) =>
//                    val ms = mutable.ArrayBuffer[Type]()
//                    val os = mutable.ArrayBuffer[FunOptTypeParam]()
//                    params.foreach {
//                      case ExpDeclParam(idn, tipe, required) =>
//                        if (required) {
//                          ms.append(tipe)
//                        } else {
//                          os.append(FunOptTypeParam(idn, tipe))
//                        }
//                    }
//                    val funType = FunType(ms.to, os.to, outType, Set.empty)
//                    entryName -> funType
//                }
//                val pkg = new LibraryDynamicPackageExtension(className, entries)
//                // Load it into the program context.
//                programContext.addPackage(pkg)
//                Right(PackageType(pkg.name))
//              case _ => Left("library source code is not valid")
//            }
//          case Left(_) => Left("source code is not valid")
//        }
//      case Left(err) => Left(err)
//    }
//  }
//
//  private def findPackage(url: String): Either[String, String] = {
//    if (ByteStreamLocationProvider.isSupported(url)) {
//      try {
//        val location =
//          ByteStreamLocationProvider.build(LocationDescription(url))(programContext.compilerContext.sourceContext)
//        val is = location.getInputStream()
//        try {
//          val code = new String(is.readAllBytes(), StandardCharsets.UTF_8)
//          Right(code)
//        } catch {
//          case NonFatal(_) => Left("could not read remote package; is it RQL code?")
//        } finally {
//          is.close()
//        }
//      } catch {
//        case ex: RawException =>
//          logger.debug("Package reading error.", ex)
//          Left(s"error importing package at $url: ${ex.getMessage}")
//        case NonFatal(ex) =>
//          logger.debug("Package reading error.", ex)
//          Left(s"unexpected error importing package at $url")
//      }
//    } else {
//      Left("url is not a bytestream")
//    }
//  }
//
//  override def toL0(t: Type, args: Seq[L0Arg]): Exp = {
//    // The execution is a "no-op" since this node never actually gets executed.
//    // Instead, the library it "creates" is that one that gets run.
//    L0.source.ZeroedConst(VoidType())
//  }
//
//}
//
//class UseLibraryEntry extends L0EntryExtension {
//
//  override def docs: EntryDoc = ???
//
//  override def nrMandatoryParams: Int = 3
//
//  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
//    idx match {
//      case 0 => Right(ValueParam(string))
//      case 1 => Right(ValueParam(string))
//      case 2 => Right(TypeParam(anything))
//    }
//  }
//
//  override def returnType(
//      mandatoryArgs: Seq[Arg],
//      optionalArgs: Seq[(String, Arg)],
//      varArgs: Seq[Arg]
//  ): Either[String, Type] = {
//    val entrypointName = getStringValue(mandatoryArgs(0))
//    val entryName = getStringValue(mandatoryArgs(1))
//    val t = mandatoryArgs(2).t.asInstanceOf[FunType]
//    val pkg = new SingleEntryLibraryDynamicPackage(entrypointName, entryName, t)
//    // Load it into the program context.
//    programContext.addPackage(pkg)
//    Right(PackageEntryType(pkg.name, entryName))
//  }
//
//  override def toL0(t: Type, args: Seq[L0Arg]): Exp = {
//    // The execution is a "no-op" since this node never actually gets executed.
//    // Instead, the library it "creates" is that one that gets run.
//    L0.source.ZeroedConst(VoidType())
//  }
//
//}
//
//object SingleEntryLibraryDynamicPackage {
//  private val counter = new AtomicInteger()
//}
//
//class SingleEntryLibraryDynamicPackage(entrypointName: String, entryName: String, ft: FunType)
//    extends PackageExtension {
//  import SingleEntryLibraryDynamicPackage._
//
//  override val name: String = s"$$Library$$${counter.getAndIncrement()}"
//
//  override def docs: PackageDoc = ???
//
//  override protected val entryExtensions: Map[String, ProgramContext => EntryExtension] = {
//    Map(
//      entryName -> ( =>
//        new SingleEntryLibraryDynamicEntry(entrypointName, entryName, ft)
//      )
//    )
//  }
//
//}
//
//class SingleEntryLibraryDynamicEntry(entrypointName: String, entryName: String, ft: FunType) extends L0EntryExtension {
//
//  override def docs: EntryDoc = ???
//
//  override def nrMandatoryParams: Int = ft.ms.length
//
//  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
//    Right(ExpParam(ft.ms(idx)))
//  }
//
//  override def optionalParams: Option[Set[String]] = {
//    if (ft.os.nonEmpty) {
//      Some(ft.os.map(_.i).toSet)
//    } else {
//      None
//    }
//  }
//
//  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
//    val t = ft.os.collectFirst { case o if o.i == idn => o.t }.get
//    Right(ExpParam(t))
//  }
//
//  override def hasVarArgs: Boolean = false
//
//  override def returnType(
//      mandatoryArgs: Seq[Arg],
//      optionalArgs: Seq[(String, Arg)],
//      varArgs: Seq[Arg]
//  ): Either[String, Type] = {
//    Right(ft.r)
//  }
//
//  override def toL0(t: Type, args: Seq[L0Arg]): Exp = {
//    val argNodes = args.map(arg => CallPackageArgNode(arg.e, arg.t, arg.idn))
//    L0.source.CallPackage(entrypointName, entryName, argNodes.to, rql2ToCommonType(t))
//  }
//
//}
//
//object LibraryDynamicPackageExtension {
//  private val counter = new AtomicInteger()
//}
//
//class LibraryDynamicPackageExtension(entrypointName: String, entries: Map[String, FunType]) extends PackageExtension {
//  import LibraryDynamicPackageExtension._
//
//  override val name: String = s"$$Library$$${counter.getAndIncrement()}"
//
//  override def docs: PackageDoc = ???
//
//  override protected val entryExtensions: Map[String, ProgramContext => EntryExtension] = entries.map {
//    case (entryName, ft) =>
//      entryName -> ( => new LibraryDynamicEntryExtension(entrypointName, entryName, ft))
//  }
//
//}
//
//class LibraryDynamicEntryExtension(entrypointName: String, entryName: String, ft: FunType) extends L0EntryExtension {
//
//  override def docs: EntryDoc = ???
//
//  override def nrMandatoryParams: Int = ft.ms.length
//
//  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
//    Right(ExpParam(ft.ms(idx)))
//  }
//
//  override def optionalParams: Option[Set[String]] = {
//    if (ft.os.nonEmpty) {
//      Some(ft.os.collect { case o => o.i }.toSet)
//    } else {
//      None
//    }
//  }
//
//  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
//    val t = ft.os.collectFirst { case o if o.i == idn => o.t }.get
//    Right(ExpParam(t))
//  }
//
//  override def hasVarArgs: Boolean = false
//
//  override def returnType(
//      mandatoryArgs: Seq[Arg],
//      optionalArgs: Seq[(String, Arg)],
//      varArgs: Seq[Arg]
//  ): Either[String, Type] = {
//    Right(ft.r)
//  }
//
//  override def toL0(t: Type, args: Seq[L0Arg]): Exp = {
//    val argNodes = args.map(arg => CallPackageArgNode(arg.e, arg.t, arg.idn))
//    L0.source.CallPackage(entrypointName, entryName, argNodes.to, rql2ToCommonType(t))
//  }
//
//}
