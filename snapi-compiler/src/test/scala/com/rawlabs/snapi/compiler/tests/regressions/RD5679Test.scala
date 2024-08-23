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

package com.rawlabs.snapi.compiler.tests.regressions

import com.rawlabs.snapi.frontend.rql2._
import com.rawlabs.snapi.compiler.tests.Rql2TestContext

class RD5679Test extends Rql2TestContext {

  // regular XML file (types as record) with a list inside
  private val xml = tempFile("""
    |<person place="world" >
    |   <name>john</name>
    |   <age>34</age>
    |   <ints>1</ints>
    |   <ints>2</ints>
    |   <ints>3</ints>
    |</person>""".stripMargin)

  // regular JSON file (types as record) with a list inside
  private val json =
    tempFile("""{"person": {"@place": "world", "name": "john", "age": 34, "ints": [1,2,3] } }""".stripMargin)

  // non standard (but supported) XML file typing as a collection
  private val xmlCollection = tempFile("""
    |<person place="world"> <name>john</name> <age>34</age> </person>
    |<person place="venus"> <name>jane</name> <age>32</age> </person>
    |<person place="moon"> <name>bob</name> <age>36</age> </person>
    |""".stripMargin)

  // a JSON collection
  private val jsonCollection = tempFile("""[
    |{"person": {"@place": "world", "name": "john", "age": 34} },
    |{"person": {"@place": "venus", "name": "jane", "age": 32} },
    |{"person": {"@place": "moon", "name": "bob", "age": 36} }
    |]""".stripMargin)

  // InferAndRead reads the whole file and types it as a record. The internal reader is asked to read a record (not tryable) but
  // we use the tryable reader in case there's a failure at connection tim. InferAndRead types as a tryable itself.
  // It's going to use RawXmlReader.read
  test(snapi"""Json.InferAndRead("$json")""".stripMargin)(it => it should evaluateTo("""{person: {
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}}
    |""".stripMargin))

  // Same but wrapped in a list otherwise we don't check the tryability
  test(snapi"""[Json.InferAndRead("$json")]""".stripMargin)(it => it should evaluateTo("""[{person: {
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}}]
    |""".stripMargin))

  // Read takes a type that happens to be tryable due to the parser. Itself types as a tryable anyway. It's going to use
  // RawJsonReader.readTry (since it reads a try, and catches errors)
  test(
    snapi"""Json.Read("$json", type record(person: record(`@place`: string, name: string, age: int, ints: list(int))))""".stripMargin
  )(it => it should evaluateTo("""{person: {
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}}
    |""".stripMargin))

  test(
    snapi"""Try.IsError(Json.Read("$json", type record(person: record(`@place`: string, name: string, age: int, ints: list(int)))))""".stripMargin
  )(it => it should evaluateTo("false"))

  test(
    snapi"""Try.IsError(Json.Read("file:/not/found", type record(person: record(`@place`: string, name: string, age: int, ints: list(int)))))""".stripMargin
  )(it => it should evaluateTo("true"))

  // reading a collection still uses `JsonRead` but since it parses as a collection, it's not tryable.
  // It generates as a RawJsonReader.readIterator which isn't tryable
  test(snapi"""Json.InferAndRead("$jsonCollection")""".stripMargin) { it =>
    it should evaluateTo("""[
      |  {person: {`@place`: "world", name: "john", age: 34}},
      |  {person: {`@place`: "venus", name: "jane", age: 32}},
      |  {person: {`@place`: "moon", name: "bob", age: 36}}
      |]""".stripMargin)
  }

  // the type argument is "a tryable collection", but we strip it since collections can't fail out of a reader.
  // It doesn't type as a tryable collection. That executes as RawJsonReader.unsafeRead as a non-tryable collection.
  test(
    snapi"""Json.Read("$jsonCollection", type collection(record(person: record(`@place`: string, name: string, age: int))))""".stripMargin
  ) { it =>
    it should evaluateTo("""[
      |  {person: {`@place`: "world", name: "john", age: 34}},
      |  {person: {`@place`: "venus", name: "jane", age: 32}},
      |  {person: {`@place`: "moon", name: "bob", age: 36}}
      |]""".stripMargin)
  }

  // The list is tryable.
  test(
    snapi"""Json.Read("$jsonCollection", type list(record(person: record(`@place`: string, name: string, age: int))))""".stripMargin
  ) { it =>
    it should evaluateTo("""[
      |  {person: {`@place`: "world", name: "john", age: 34}},
      |  {person: {`@place`: "venus", name: "jane", age: 32}},
      |  {person: {`@place`: "moon", name: "bob", age: 36}}
      |]""".stripMargin)
  }

  // The list is tryable here too. The missing file leads to an error caught by Try.IsError.
  test(
    snapi"""Try.IsError(Json.Read("file:/not/found", type list(record(person: record(`@place`: string, name: string, age: int)))))""".stripMargin
  )(it => it should evaluateTo("""true""".stripMargin))

  test(snapi"""Json.InferAndRead("$json").person.ints""".stripMargin)(it =>
    it should evaluateTo("""[1, 2, 3]""".stripMargin)
  )

  test(
    snapi"""Json.Read("$json", type record(person: record(`@place`: string, name: string, age: int, ints: list(int)))).person.ints""".stripMargin
  )(it => it should evaluateTo("""[1, 2, 3]""".stripMargin))

  test(
    snapi"""Json.Read("$json", type record(person: record(`@place`: string, name: string, age: int, ints: collection(int)))).person.ints""".stripMargin
  )(it => it should evaluateTo("""[1, 2, 3]""".stripMargin))

  test(snapi"""Xml.InferAndRead("$xml")""".stripMargin)(it => it should evaluateTo("""{
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}
    |""".stripMargin))

  test(
    snapi"""Xml.Read("$xml", type record(`@place`: string, name: string, age: int, ints: list(int)))""".stripMargin
  )(it => it should evaluateTo("""{
    |  `@place`: "world",
    |  name: "john",
    |  age: 34,
    |  ints: [1, 2, 3]
    |}
    |""".stripMargin))

  test(
    snapi"""Try.IsError(Xml.Read("$xml", type record(`@place`: string, name: string, age: int, ints: list(int))))""".stripMargin
  )(it => it should evaluateTo("false"))

  test(
    snapi"""Try.IsError(Xml.Read("file:/not/found", type record(`@place`: string, name: string, age: int, ints: list(int))))""".stripMargin
  )(it => it should evaluateTo("true"))

  // the file is inferred to be a collection, it doesn't type as a tryable collection. That runs with RawXmlReader.unsafeRead
  test(snapi"""Xml.InferAndRead("$xmlCollection")""".stripMargin) { it =>
    it should evaluateTo("""[
      |  {`@place`: "world", name: "john", age: 34},
      |  {`@place`: "venus", name: "jane", age: 32},
      |  {`@place`: "moon", name: "bob", age: 36}
      |]""".stripMargin)
  }

  test(
    snapi"""Xml.Read("$xmlCollection", type collection(record(`@place`: string, name: string, age: int)))""".stripMargin
  ) { it =>
    it should evaluateTo("""[
      |  {`@place`: "world", name: "john", age: 34},
      |  {`@place`: "venus", name: "jane", age: 32},
      |  {`@place`: "moon", name: "bob", age: 36}
      |]""".stripMargin)
  }

  test(
    snapi"""Xml.Read("$xmlCollection", type list(record(`@place`: string, name: string, age: int)))""".stripMargin
  ) { it =>
    it should evaluateTo("""[
      |  {`@place`: "world", name: "john", age: 34},
      |  {`@place`: "venus", name: "jane", age: 32},
      |  {`@place`: "moon", name: "bob", age: 36}
      |]""".stripMargin)
  }

  test(snapi"""Xml.InferAndRead("$xml").ints""".stripMargin)(it => it should evaluateTo("""[1, 2, 3]""".stripMargin))

  test(
    snapi"""Xml.Read("$xml", type record(`@place`: string, name: string, age: int, ints: list(int))).ints""".stripMargin
  )(it => it should evaluateTo("""[1, 2, 3]""".stripMargin))

  test(
    snapi"""Xml.Read("$xml", type record(`@place`: string, name: string, age: int, ints: collection(int))).ints""".stripMargin
  )(it => it should evaluateTo("""[1, 2, 3]""".stripMargin))

}
