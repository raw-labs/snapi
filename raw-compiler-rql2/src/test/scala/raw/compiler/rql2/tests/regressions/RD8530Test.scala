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

import org.scalatest.BeforeAndAfterEach
import raw.compiler.rql2.tests.CompilerTestContext

trait RD8530Test extends CompilerTestContext with BeforeAndAfterEach {

  test("""List.Filter([1,2,3,4], n -> n > 2, a = 12)""".stripMargin)(
    _ should typeErrorAs("no optional arguments expected")
  )
  test("""List.Filter([1,2,3,4], a = 12, n -> n > 2)""".stripMargin)(
    _ should typeErrorAs("mandatory arguments must be before optional arguments")
  )
  test("""List.Filter(a = 12, [1,2,3,4], n -> n > 2)""".stripMargin)(
    _ should typeErrorAs("mandatory arguments must be before optional arguments")
  )

  test("""List.Filter([1,2,3,4],
    |input_record_filtered = input_dimension_filtered, l -> Collection.Count(l.dimension)>0)""".stripMargin) {
    _ should typeErrorAs("mandatory arguments must be before optional arguments")
  }

  // subset of the original crashing query
  test(
    """filterByToken(input: list(record(language: string, mytype: string, dimension: collection(string))), token: string = null): list(record(language: string, mytype: string, dimension: collection(string))) =
      |  let
      |    token_normalized = if(token=="") then null else token,
      |    output =
      |      if(Nullable.IsNull(token_normalized))
      |      then input
      |      else
      |        List.Filter(
      |          List.Transform(input, l -> {
      |            language: l.language,
      |            mytype: l.mytype,
      |            dimension: Collection.Filter(l.dimension, v -> Regex.Matches(v, ".*"+token+".*"))
      |          }),
      |    input_record_filtered = input_dimension_filtered, l -> Collection.Count(l.dimension)>0)
      |  in
      |    output
      |
      |filterByToken([],  "")
      |""".stripMargin
  )(_ should typeErrorAs("mandatory arguments must be before optional arguments"))

  // original crashing query
  test("""flatten2(use_column_headers: bool, c: list(
    |              record(
    |                  column_header: collection(string),
    |                  row_header: collection(string),
    |                  measurement: string
    |              ))) =
    |let
    |
    |  rec reduceToString(c: list(string)): string =
    |  let
    |    output =
    |      if(List.Count(c)==0) then "#"
    |      else String.Replace(reduceToString(List.Take(c, List.Count(c)-1)) + " -> " + List.Last(c), "# -> ", "")
    |  in
    |    output,
    |
    |  rec flat2(c: list(
    |                          record(
    |                              column_header: collection(string),
    |                              row_header: collection(string),
    |                              measurement: string
    |                          ))): collection(string) =
    |  let
    |    output =  if(List.Count(c)==0)
    |              then
    |                Collection.Empty(type string)
    |              else
    |                Collection.Distinct(
    |                  Collection.Union(
    |                    flat2(List.Take(c, List.Count(c)-1)),
    |                    //Collection.Empty(type string),
    |                    Collection.Build(reduceToString(List.From(
    |                        if(use_column_headers) then List.Last(c).column_header else List.Last(c).row_header
    |                      )))
    |                  )
    |                )
    |  in
    |    output,
    |
    |  output = flat2(c)
    |in
    |  output
    |
    |
    |flatten(use_column_headers: bool, c: list(
    |                record(
    |                    area: string,
    |                    worksheet_number: string,
    |                    worksheet_dataset: collection(
    |                        record(
    |                            column_header: collection(string),
    |                            row_header: collection(string),
    |                            measurement: string
    |                        )
    |                    ),
    |                    worksheet_title: string,
    |                    worksheet_notes: collection(undefined),
    |                    worksheet_name: string
    |                )
    |            )) =
    |let
    |
    |  rec flat(c: list(
    |                  record(
    |                      area: string,
    |                      worksheet_number: string,
    |                      worksheet_dataset: collection(
    |                          record(
    |                              column_header: collection(string),
    |                              row_header: collection(string),
    |                              measurement: string
    |                          )
    |                      ),
    |                      worksheet_title: string,
    |                      worksheet_notes: collection(undefined),
    |                      worksheet_name: string
    |                  )
    |              )): collection(string) =
    |  let
    |    output =  if(List.Count(c)==0)
    |              then
    |                Collection.Empty(type string)
    |              else
    |                Collection.Distinct(
    |                  Collection.Union(
    |                    flat(List.Take(c, List.Count(c)-1)),
    |                    Collection.Distinct(flatten2(use_column_headers, List.From(List.Last(c).worksheet_dataset)))
    |                  )
    |                )
    |  in
    |    output,
    |
    |  output = flat(c)
    |in
    |  output
    |
    |getTypeFromFSO(fsoNr: string) =
    |  if(String.StartsWith(fsoNr, "su-d-vz18-k")) then "su-d-vz18-k"
    |  else String.SubString(fsoNr, 1, 9)
    |
    |filterByToken(input: list(record(language: string, mytype: string, dimension: collection(string))), token: string = null): list(record(language: string, mytype: string, dimension: collection(string))) =
    |  let
    |    token_normalized = if(token=="") then null else token,
    |    output =
    |      if(Nullable.IsNull(token_normalized))
    |      then input
    |      else
    |        List.Filter(
    |          List.Transform(input, l -> {
    |            language: l.language,
    |            mytype: l.mytype,
    |            dimension: Collection.Filter(l.dimension, v -> Regex.Matches(v, ".*"+token+".*"))
    |          }),
    |    input_record_filtered = input_dimension_filtered, l -> Collection.Count(l.dimension)>0)
    |//    output = List.Filter(input, l -> Collection.Count(Collection.Filter(l.dimension, v -> Regex.Matches(v, ".*"+token+".*")))>0)
    |  in
    |    output
    |
    |main(use_column_headers: bool = true, token: string = null)=
    |let
    |  files = [
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-d-vz18-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-i-vz18-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-f-vz18-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-d-vz18-k-11.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-i-vz18-k-11.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-f-vz18-k-11.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-d-vz21-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-i-vz21-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-f-vz21-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-d-vz24-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-i-vz24-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-f-vz24-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-d-vz61-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-i-vz61-b-0101.json",
    |    "s3://rawlabs-public-test-data/SSO/json_artifacts/su-f-vz61-b-0101.json"
    |    ],
    |  output = List.Transform(List.Transform(files, f -> Json.Read(S3.Build(f), type record(
    |            originAssetUrl: string,
    |            fsoNr: string,
    |            language: string,
    |            title: string,
    |            period: string,
    |            prodimaCode: string,
    |            inquiryCode: string,
    |            embargo: date,
    |            dataset: collection(
    |                record(
    |                    area: string,
    |                    worksheet_number: string,
    |                    worksheet_dataset: collection(
    |                        record(
    |                            column_header: collection(string),
    |                            row_header: collection(string),
    |                            measurement: string
    |                        )
    |                    ),
    |                    worksheet_title: string,
    |                    worksheet_notes: collection(undefined),
    |                    worksheet_name: string
    |                )
    |            )
    |        ))),
    |        l -> {language: l.language, mytype: getTypeFromFSO(l.fsoNr), dimension: flatten(use_column_headers, List.From(l.dataset))}
    |  )
    |in
    |  filterByToken(output, token)
    |
    |main()""".stripMargin)(_ should typeErrorAs("mandatory arguments must be before optional arguments"))

}
