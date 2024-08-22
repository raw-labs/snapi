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

package com.rawlabs.snapi.compiler.tests.builtin

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.truffle.Rql2TruffleCompilerTestContext

class TypePackageTest extends Rql2TruffleCompilerTestContext {

  ignore("""Types.Merge(int, float)""")(it => it should typeAs("float"))

  private val orType = tempFile("""[
    |  {"host": "server-01", "disks": ["/dev/sda1", "/dev/sda2"]},
    |  {"host": "server-03", "disks": {"partitions": ["/dev/sda1", "/dev/sda2", "/dev/sda3"]}}
    |]""".stripMargin)

  test(snapi"""Json.InferAndRead("$orType")""")(_ should run)

  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |))
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       (x: collection(int)) -> Collection.Count(x),
    |       (x: record(partitions: collection(string))) -> Collection.Count(x.partitions)
    |   )
    |)""".stripMargin)(_ should runErrorAs("expected")) // expected one of .... but got

  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |))
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       (x: collection(string)) -> Collection.Count(x),
    |       (x: collection(string)) -> Collection.Count(x),
    |       (x: record(partitions: collection(string))) -> Collection.Count(x.partitions)
    |   )
    |)""".stripMargin)(_ should runErrorAs("only one handler function per type is expected"))

  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |))
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       (x: record(partitions: collection(string))) -> Collection.Count(x.partitions)
    |   )
    |)""".stripMargin)(_ should runErrorAs("handler functions should be provided for all types"))

  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |))
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       (x: collection(string)) -> Collection.Count(x),
    |       (x: record(partitions: collection(string))) -> Collection.Count(x.partitions)
    |   )
    |)""".stripMargin)(_ should evaluateTo("[2L, 3L]"))

  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |))
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       (x: record(partitions: collection(string))) -> Collection.Count(x.partitions),
    |       (x: collection(string)) -> Collection.Count(x)
    |   )
    |)""".stripMargin)(_ should evaluateTo("[2L, 3L]"))

  test(snapi"""
    |let items = Json.InferAndRead("$orType")
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       (x: collection(string)) -> Collection.Count(x),
    |       (x: record(partitions: collection(string))) -> Collection.Count(x.partitions)
    |   )
    |)""".stripMargin)(_ should evaluateTo("[2L, 3L]"))

  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |))
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       (x: collection(string)) -> Collection.Count(x),
    |       (x: record(partitions: collection(string))) -> Collection.Count(x.partitions) * 3.14
    |   )
    |)""".stripMargin)(_ should evaluateTo("[2d, 9.42d]"))

  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |)),
    |    f1s = [(x: collection(string)) -> Collection.Count(x), null],
    |    f2s = [(x: record(partitions: collection(string))) -> Collection.Count(x.partitions) * 3.14, Error.Build("bug")]
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       List.Get(f1s, 0),
    |       List.Get(f2s, 0)
    |   )
    |)""".stripMargin)(_ should evaluateTo("[2d, 9.42d]"))

  // One of the function is null. This turns the whole `Type.Match` to `null` since it doesn't expect null function
  // arguments. That's why both applications of `Type.Match` result to `null`.
  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |)),
    |    f1s = [(x: collection(string)) -> Collection.Count(x), null],
    |    f2s = [(x: record(partitions: collection(string))) -> Collection.Count(x.partitions) * 3.14, Error.Build("bug")]
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       List.Get(f1s, 1),
    |       List.Get(f2s, 0)
    |   )
    |)""".stripMargin)(_ should evaluateTo("""[null, null]"""))

  // One of the function is an error. This turns the whole `Type.Match` to `null` since it doesn't expect null function
  // arguments. That's why both applications of `Type.Match` result to `null`.
  test(snapi"""
    |let items = Json.Read("$orType", type collection(
    |  record(
    |      host: string,
    |      disks: collection(string) or record(partitions: collection(string))
    |  )
    |)),
    |    f1s = [(x: collection(string)) -> Collection.Count(x), null],
    |    f2s = [(x: record(partitions: collection(string))) -> Collection.Count(x.partitions) * 3.14, Error.Build("bug")]
    |in Collection.Transform(
    |   items,
    |   i -> Type.Match(i.disks,
    |       List.Get(f1s, 0),
    |       List.Get(f2s, 1)
    |   )
    |)""".stripMargin)(_ should evaluateTo("""[Error.Build("bug"), Error.Build("bug")]"""))

}
