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
import com.rawlabs.snapi.compiler.tests.SnapiTestContext

class RD5921Test extends SnapiTestContext {

  val data1 = tempFile("""<a>1</a>
    |<a>2</a>
    |<a>3<b>Hello!</b></a>
    |<a><b>Hello!</b>4</a>""".stripMargin)

  test(snapi"""Xml.Read("$data1", type collection(string))""") {
    _ should evaluateTo(""" [
      | "1",
      | "2",
      | Error.Build("failed to parse XML (line 3 column 5): expected end-element </a> but got start-element <b>"),
      | Error.Build("failed to parse XML (line 4 column 4): expected end-element </a> but got start-element <b>")
      |]  """.stripMargin)
  }

  val triple = "\"\"\""
  test(s"""let
    |   data = $triple<a>1</a>
    |       <a>2</a>
    |       <a>3<b>Hello!</b></a>
    |       <a><b>Hello!</b>4</a>$triple
    |in Xml.Parse(data, type collection(string))""".stripMargin) {
    _ should evaluateTo(""" [
      | "1",
      | "2",
      | Error.Build("failed to parse XML (line 3 column 12): expected end-element </a> but got start-element <b>"),
      | Error.Build("failed to parse XML (line 4 column 11): expected end-element </a> but got start-element <b>")
      |]  """.stripMargin)
  }

  test(s"""let
    |   data = $triple<a>1</a>
    |       <a>2</a>
    |       <a>3<b>Hello!</b></a>
    |       <a><b>Hello!</b></a>$triple
    |in Xml.Parse(data, type collection(string))""".stripMargin) {
    _ should evaluateTo(""" [
      | "1",
      | "2",
      | Error.Build("failed to parse XML (line 3 column 12): expected end-element </a> but got start-element <b>"),
      | Error.Build("failed to parse XML (line 4 column 11): expected end-element </a> but got start-element <b>")
      |]  """.stripMargin)
  }

  test(s"""let
    |   data = $triple<a>1</a>
    |       <a>2</a>
    |       <a>3<b>Hello!</b></a>
    |       <a><b>Hello!</b></a>$triple
    |in Xml.Parse(data, type collection(int))""".stripMargin) {
    _ should evaluateTo(""" [
      | 1,
      | 2,
      | Error.Build("failed to parse XML (line 3 column 12): expected end-element </a> but got start-element <b>"),
      | Error.Build("failed to parse XML (line 4 column 11): expected end-element </a> but got start-element <b>")
      |]  """.stripMargin)
  }

  val data2 = tempFile("""<a><b>Hello</b><b>World!</b></a>
    |<a><b>21<c>something</c></b><b><c>something else</c>22</b></a>
    |<a><b><c></c></b><b><c>something</c></b></a>""".stripMargin)

  test(snapi"""Xml.Read("$data2", type collection(record(b: collection(string))))""") {
    _ should evaluateTo(""" [
      | {b: ["Hello", "World!"]},
      | {b: [
      |     Error.Build("failed to parse XML (line 2 column 9): expected end-element </b> but got start-element <c>"),
      |     Error.Build("failed to parse XML (line 2 column 32): expected end-element </b> but got start-element <c>")
      | ]},
      | {b: [
      |     Error.Build("failed to parse XML (line 3 column 7): expected end-element </b> but got start-element <c>"),
      |     Error.Build("failed to parse XML (line 3 column 21): expected end-element </b> but got start-element <c>")
      | ]}
      |]  """.stripMargin)
  }

  test("""let
    |  htmlString =
    |  String.Read("s3://rawlabs-public-test-data/rd-5921.xml"),
    |  sanitizedHtml = Regex.Replace(Regex.Replace(htmlString, "(\r\n|\n|\r|\t)", ""), "&euro;", "EU"),
    |  groups = Regex.Groups(sanitizedHtml, "(.*)(<table class=\"info\">)(.*)(<\\/table>)(.*)"),
    |  data = List.Get(groups, 2),
    |  xmlDataType = type collection(record(td: collection(string))),
    |  xmlData = Xml.Parse(data, xmlDataType)
    |in
    |  xmlData""".stripMargin)(_ should run)

  test("""let
    |  htmlString =
    |  String.Read("s3://rawlabs-public-test-data/rd-5921.xml"),
    |  sanitizedHtml = Regex.Replace(Regex.Replace(htmlString, "(\r\n|\n|\r|\t)", ""), "&euro;", "EU"),
    |  groups = Regex.Groups(sanitizedHtml, "(.*)(<table class=\"info\">)(.*)(<\\/table>)(.*)"),
    |  data = List.Get(groups, 2),
    |  xmlDataType = type collection(record(td: collection(string))),
    |  xmlData = Xml.Parse(data, xmlDataType)
    |in
    |  Collection.Filter(xmlData, x -> Collection.Exists(x.td, y -> Try.IsError(y)))""".stripMargin)(
    _ should evaluateTo(
      """[{
        |  td: [
        |    Error.Build("failed to parse XML (line 1 column 24): expected end-element </td> but got start-element <strong>"),
        |    Error.Build("failed to parse XML (line 1 column 75): expected end-element </td> but got start-element <strong>")
        |  ]
        |}]""".stripMargin
    )
  )
}
