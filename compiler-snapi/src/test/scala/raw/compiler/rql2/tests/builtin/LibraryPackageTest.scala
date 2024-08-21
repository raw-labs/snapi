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

//package raw.compiler.rql2.tests.builtin
//
//import com.rawlabs.compiler.snapi.rql2.tests.{RunnerScalaTestContext, RunnerTestContext}
//import raw.testing.tags.Tier1
//
//@Tier1
//class LibraryPackageScala2Test extends RunnerScalaTestContext with LibraryPackageTest
//
//trait LibraryPackageTest extends RunnerTestContext {
//
//  val simpleLib: String = lib("""
//    |a(v: int) = v * 2
//    |b() = 1
//    |c(v: string = "Hello") = v
//    |""".stripMargin)
//
//  test(s"""
//    |let MyLib = Library.Load("$simpleLib"),
//    |    v2 = MyLib.a(2)
//    |in
//    |    v2
//    |""".stripMargin)(it => it should evaluateTo("4"))
//
//  test(s"""
//    |let MyLib = Library.Load("$simpleLib"),
//    |    v1 = MyLib.a(2),
//    |    v2 = MyLib.b()
//    |in
//    |    v1 + v2
//    |""".stripMargin)(it => it should evaluateTo("5"))
//
//  test(s"""
//    |let MyLib = Library.Load("$simpleLib")
//    |in MyLib.d()
//    |""".stripMargin)(it => it should runErrorAs("d is not declared"))
//
//  val brokenLib: String = lib("""
//    |a(v: int) = v *
//    |""".stripMargin)
//
//  test(s"""
//    |let MyLib = Library.Load("$brokenLib"),
//    |    v1 = MyLib.a(2)
//    |in
//    |    v1
//    |""".stripMargin)(it => it should runErrorAs("code is not valid"))
//
//  val notALib: String = lib("""
//    |"Hello"
//    |""".stripMargin)
//
//  test(s"""
//    |let MyLib = Library.Load("$notALib"),
//    |    v1 = MyLib.a(2)
//    |in
//    |    v1
//    |""".stripMargin)(it => it should runErrorAs("a is not declared in package"))
//
//}
