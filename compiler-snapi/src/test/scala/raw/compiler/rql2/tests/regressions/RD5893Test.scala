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

package raw.compiler.rql2.tests.regressions

import com.rawlabs.compiler.snapi.utils._
import raw.compiler.rql2.truffle.Rql2TruffleCompilerTestContext

class RD5893Test extends Rql2TruffleCompilerTestContext {

  // this has a list of text
  private val data = tempFile("""
    |<person>
    |   <name>john</name>
    |   text here
    |   <age>34</age>
    |   some more text
    |   <friend>jane</friend>
    |   and again
    |   <friend>bob</friend>
    |</person>""".stripMargin)

  test(snapi"""Collection.Transform(Xml.InferAndRead("$data").`#text`, x -> String.Trim(x))""") {
    _ should evaluateTo("""["text here", "some more text", "and again"]""".stripMargin)
  }

  test(snapi"""let
    |  data = Xml.Read("$data", type record(name: string, age: int, friend: list(string), `#text`: list(string)))
    |in
    |  List.Transform(data.`#text`, x -> String.Trim(x))""".stripMargin) {
    _ should evaluateTo("""["text here", "some more text", "and again"]""".stripMargin)
  }

  test(snapi"""let
    |  data = Xml.Read("$data", type record(name: string, age: int, friend: collection(string), `#text`: collection(string)))
    |in
    |  Collection.Transform(data.`#text`, x -> String.Trim(x))""".stripMargin) {
    _ should evaluateTo("""["text here", "some more text", "and again"]""".stripMargin)
  }

  test(
    """Xml.InferAndRead("https://rawlabs-public-test-data.s3.eu-west-1.amazonaws.com/SSO/su-d-05.04-kbob-01.xml")"""
  )(it => it should runErrorAs("duplicate field: @value-type"))

  val xmlType = """record(
    |    `@version`: double,
    |    `@mimetype`: string,
    |    meta: record(
    |        `initial-creator`: string,
    |        creator: string,
    |        `print-date`: timestamp,
    |        `creation-date`: timestamp,
    |        date: timestamp,
    |        `editing-duration`: string,
    |        generator: string,
    |        `document-statistic`: record(
    |            `@table-count`: int,
    |            `@cell-count`: int,
    |            `@object-count`: int),
    |        `user-defined`: collection(
    |            record(`@name`: string, `#text`: string, `@value-type`: string))),
    |    settings: record(
    |        `config-item-set`: collection(
    |            record(
    |                `@name`: string,
    |                `config-item`: collection(
    |                    record(
    |                        `@name`: string,
    |                        `@type`: string,
    |                        `#text`: string)),
    |                `config-item-map-indexed`: record(
    |                    `@name`: string,
    |                    `config-item-map-entry`: record(
    |                        `config-item-map-named`: record(
    |                            `@name`: string,
    |                            `config-item-map-entry`: collection(
    |                                record(
    |                                    `@name`: string,
    |                                    `config-item`: collection(
    |                                        record(
    |                                            `@name`: string,
    |                                            `@type`: string,
    |                                            `#text`: string))))),
    |                        `config-item`: collection(
    |                            record(
    |                                `@name`: string,
    |                                `@type`: string,
    |                                `#text`: string))))))),
    |    scripts: record(
    |        script: record(`@language`: string, libraries: undefined)),
    |    `font-face-decls`: record(
    |        `font-face`: collection(
    |            record(
    |                `@name`: string,
    |                `@font-family`: string,
    |                `@font-family-generic`: string,
    |                `@font-pitch`: string))),
    |    styles: record(
    |        `default-style`: record(
    |            `@family`: string,
    |            `paragraph-properties`: record(`@tab-stop-distance`: string),
    |            `text-properties`: record(
    |                `@font-name`: string,
    |                `@language`: string,
    |                `@country`: string,
    |                `@font-name-asian`: string,
    |                `@language-asian`: string,
    |                `@country-asian`: string,
    |                `@font-name-complex`: string,
    |                `@language-complex`: string,
    |                `@country-complex`: string)),
    |        `date-style`: collection(
    |            record(
    |                `@name`: string,
    |                month: record(`@textual`: bool),
    |                day: undefined,
    |                text: collection(string),
    |                year: record(`@style`: string),
    |                hours: undefined,
    |                minutes: record(`@style`: string))),
    |        `currency-style`: collection(
    |            record(
    |                `@name`: string,
    |                `@volatile`: bool,
    |                `currency-symbol`: undefined,
    |                number: record(
    |                    `@decimal-places`: int,
    |                    `@min-decimal-places`: int,
    |                    `@min-integer-digits`: int,
    |                    `@grouping`: bool),
    |                text: collection(string),
    |                map: record(
    |                    `@condition`: string,
    |                    `@apply-style-name`: string),
    |                `text-properties`: record(`@color`: string),
    |                `fill-character`: undefined)),
    |        `text-style`: collection(
    |            record(
    |                `@name`: string,
    |                `text-content`: undefined,
    |                text: collection(undefined),
    |                map: collection(
    |                    record(
    |                        `@condition`: string,
    |                        `@apply-style-name`: string)))),
    |        `time-style`: collection(
    |            record(
    |                `@name`: string,
    |                hours: undefined,
    |                minutes: record(`@style`: string),
    |                text: collection(string),
    |                `am-pm`: undefined,
    |                seconds: record(`@style`: string, `@decimal-places`: int),
    |                `@truncate-on-overflow`: bool)),
    |        `number-style`: collection(
    |            record(
    |                `@name`: string,
    |                number: record(
    |                    `@min-integer-digits`: int,
    |                    `@decimal-places`: int,
    |                    `@min-decimal-places`: int,
    |                    `@grouping`: bool),
    |                `@volatile`: bool,
    |                text: collection(string),
    |                `text-properties`: record(`@color`: string),
    |                map: record(
    |                    `@condition`: string,
    |                    `@apply-style-name`: string),
    |                `fill-character`: undefined,
    |                `scientific-number`: record(
    |                    `@decimal-places`: int,
    |                    `@min-decimal-places`: int,
    |                    `@min-integer-digits`: int,
    |                    `@min-exponent-digits`: int,
    |                    `@exponent-interval`: int,
    |                    `@forced-exponent-sign`: bool))),
    |        style: collection(
    |            record(
    |                `@name`: string,
    |                `@family`: string,
    |                `table-cell-properties`: record(
    |                    `@rotation-align`: string,
    |                    `@vertical-align`: string,
    |                    `@background-color`: string,
    |                    `@diagonal-bl-tr`: string,
    |                    `@diagonal-tl-br`: string,
    |                    `@border`: string),
    |                `text-properties`: record(
    |                    `@font-name`: string,
    |                    `@font-family`: string,
    |                    `@font-name-complex`: string,
    |                    `@font-family-complex`: string,
    |                    `@color`: string,
    |                    `@font-size`: string,
    |                    `@font-style`: string,
    |                    `@font-weight`: string,
    |                    `@text-underline-style`: string,
    |                    `@text-underline-width`: string,
    |                    `@text-underline-color`: string,
    |                    `@text-outline`: bool,
    |                    `@text-line-through-style`: string,
    |                    `@text-line-through-type`: string,
    |                    `@text-shadow`: string,
    |                    `@font-size-asian`: string,
    |                    `@font-style-asian`: string,
    |                    `@font-weight-asian`: string,
    |                    `@font-size-complex`: string,
    |                    `@font-style-complex`: string,
    |                    `@font-weight-complex`: string),
    |                `@parent-style-name`: string,
    |                `@display-name`: string))),
    |    `automatic-styles`: record(
    |        `number-style`: collection(
    |            record(
    |                `@name`: string,
    |                number: record(
    |                    `@decimal-places`: int,
    |                    `@min-decimal-places`: int,
    |                    `@min-integer-digits`: int))),
    |        `page-layout`: collection(
    |            record(
    |                `@name`: string,
    |                `page-layout-properties`: record(
    |                    `@first-page-number`: string,
    |                    `@writing-mode`: string,
    |                    `@page-width`: string,
    |                    `@page-height`: string,
    |                    `@num-format`: int,
    |                    `@print-orientation`: string,
    |                    `@margin-top`: string,
    |                    `@margin-bottom`: string,
    |                    `@margin-left`: string,
    |                    `@margin-right`: string,
    |                    `@print-page-order`: string,
    |                    `@scale-to`: string,
    |                    `@table-centering`: string,
    |                    `@print`: string),
    |                `header-style`: record(
    |                    `header-footer-properties`: record(
    |                        `@min-height`: string,
    |                        `@margin-left`: string,
    |                        `@margin-right`: string,
    |                        `@margin-bottom`: string,
    |                        `@border`: string,
    |                        `@padding`: string,
    |                        `@background-color`: string,
    |                        `background-image`: undefined)),
    |                `footer-style`: record(
    |                    `header-footer-properties`: record(
    |                        `@min-height`: string,
    |                        `@margin-left`: string,
    |                        `@margin-right`: string,
    |                        `@margin-top`: string,
    |                        `@border`: string,
    |                        `@padding`: string,
    |                        `@background-color`: string,
    |                        `background-image`: undefined)))),
    |        style: collection(
    |            record(
    |                `@name`: string,
    |                `@family`: string,
    |                `table-column-properties`: record(
    |                    `@break-before`: string,
    |                    `@column-width`: string),
    |                `table-row-properties`: record(
    |                    `@row-height`: string,
    |                    `@break-before`: string,
    |                    `@use-optimal-row-height`: bool),
    |                `@master-page-name`: string,
    |                `table-properties`: record(
    |                    `@display`: bool,
    |                    `@writing-mode`: string),
    |                `@parent-style-name`: string,
    |                `table-cell-properties`: record(
    |                    `@background-color`: string,
    |                    `@diagonal-bl-tr`: string,
    |                    `@diagonal-tl-br`: string,
    |                    `@border`: string,
    |                    `@rotation-align`: string,
    |                    `@text-align-source`: string,
    |                    `@repeat-content`: bool,
    |                    `@wrap-option`: string,
    |                    `@direction`: string,
    |                    `@rotation-angle`: int,
    |                    `@shrink-to-fit`: bool,
    |                    `@vertical-align`: string,
    |                    `@vertical-justify`: string,
    |                    `@border-bottom`: string,
    |                    `@border-left`: string,
    |                    `@border-right`: string,
    |                    `@border-top`: string,
    |                    `@cell-protect`: string,
    |                    `@print-content`: bool),
    |                `paragraph-properties`: record(
    |                    `@text-align`: string,
    |                    `@text-justify`: string,
    |                    `@margin-left`: string,
    |                    `@writing-mode`: string),
    |                `text-properties`: record(
    |                    `@color`: string,
    |                    `@text-outline`: bool,
    |                    `@text-line-through-style`: string,
    |                    `@text-line-through-type`: string,
    |                    `@font-name`: string,
    |                    `@font-size`: string,
    |                    `@font-style`: string,
    |                    `@text-shadow`: string,
    |                    `@text-underline-style`: string,
    |                    `@font-weight`: string,
    |                    `@font-size-asian`: string,
    |                    `@font-style-asian`: string,
    |                    `@font-weight-asian`: string,
    |                    `@font-name-complex`: string,
    |                    `@font-size-complex`: string,
    |                    `@font-style-complex`: string,
    |                    `@font-weight-complex`: string,
    |                    `@use-window-font-color`: bool,
    |                    `@text-underline-width`: string,
    |                    `@text-underline-color`: string,
    |                    `@text-position`: string),
    |                `@data-style-name`: string))),
    |    `master-styles`: record(
    |        `master-page`: collection(
    |            record(
    |                `@name`: string,
    |                `@page-layout-name`: string,
    |                header: record(
    |                    p: record(
    |                        `sheet-name`: string,
    |                        span: record(`@style-name`: string, s: undefined)),
    |                    `region-left`: record(
    |                        p: record(
    |                            `sheet-name`: string,
    |                            s: undefined,
    |                            title: string,
    |                            `#text`: collection(string))),
    |                    `region-right`: record(
    |                        p: record(
    |                            date: record(
    |                                `@data-style-name`: string,
    |                                `@date-value`: date,
    |                                `#text`: string),
    |                            `#text`: string,
    |                            time: time))),
    |                `header-left`: record(`@display`: bool),
    |                footer: record(
    |                    p: record(
    |                        `#text`: collection(string),
    |                        `page-number`: int,
    |                        s: undefined,
    |                        `page-count`: int),
    |                    `@display`: bool),
    |                `footer-left`: record(`@display`: bool),
    |                `@display-name`: string))),
    |    body: record(
    |        spreadsheet: record(
    |            `calculation-settings`: record(
    |                `@case-sensitive`: bool,
    |                `@precision-as-shown`: bool,
    |                `@automatic-find-labels`: bool,
    |                `@use-regular-expressions`: bool,
    |                `@use-wildcards`: bool,
    |                iteration: record(`@maximum-difference`: double)),
    |            table: collection(
    |                record(
    |                    `@name`: string,
    |                    `@style-name`: string,
    |                    forms: record(
    |                        `@automatic-focus`: bool,
    |                        `@apply-design-mode`: bool),
    |                    `table-column`: collection(
    |                        record(
    |                            `@style-name`: string,
    |                            `@default-cell-style-name`: string,
    |                            `@number-columns-repeated`: int)),
    |                    `table-header-rows`: record(
    |                        `table-row`: collection(
    |                            record(
    |                                `@style-name`: string,
    |                                `table-cell`: collection(
    |                                    record(
    |                                        `@style-name`: string,
    |                                        `@value-type`: string,
    |                                        // `@value-type`: string, duplicate field isn't supported (RD-10801)
    |                                        p: collection(
    |                                            record(
    |                                                span: collection(
    |                                                    record(
    |                                                        `@style-name`: string,
    |                                                        `#text`: string)),
    |                                                `#text`: string,
    |                                                s: record(`@c`: int))),
    |                                        `@number-columns-repeated`: int,
    |                                        `@number-columns-spanned`: int,
    |                                        `@number-rows-spanned`: int)),
    |                                `covered-table-cell`: collection(
    |                                    record(
    |                                        `@number-columns-repeated`: int,
    |                                        `@style-name`: string))))),
    |                    `table-row`: collection(
    |                        record(
    |                            `@style-name`: string,
    |                            `table-cell`: collection(
    |                                record(
    |                                    `@style-name`: string,
    |                                    `@number-columns-repeated`: int,
    |                                    `@value-type`: string,
    |                                    // `@value-type`: string, duplicate field isn't supported (RD-10801)
    |                                    p: collection(
    |                                        record(
    |                                            s: record(`@c`: int),
    |                                            `#text`: collection(string),
    |                                            a: record(
    |                                                `@href`: string,
    |                                                `@type`: string,
    |                                                `#text`: string))),
    |                                    `@value`: double,
    |                                    `@number-columns-spanned`: int,
    |                                    `@number-rows-spanned`: int)),
    |                            `covered-table-cell`: collection(
    |                                record(
    |                                    `@number-columns-repeated`: int,
    |                                    `@style-name`: string)),
    |                            `@number-rows-repeated`: int)),
    |                    `named-expressions`: record(
    |                        `named-range`: collection(
    |                            record(
    |                                `@name`: string,
    |                                `@base-cell-address`: string,
    |                                `@cell-range-address`: string,
    |                                `@range-usable-as`: string)),
    |                        `named-expression`: record(
    |                            `@name`: string,
    |                            `@base-cell-address`: string,
    |                            `@expression`: string)))),
    |            `named-expressions`: undefined,
    |            `database-ranges`: record(
    |                `database-range`: record(
    |                    `@name`: string,
    |                    `@target-range-address`: string)))))""".stripMargin

  test(
    s"""Xml.Read(
      |  "https://rawlabs-public-test-data.s3.eu-west-1.amazonaws.com/SSO/su-d-05.04-kbob-01.xml",
      |  type $xmlType
      |)""".stripMargin
  )(it => it should run)

}
