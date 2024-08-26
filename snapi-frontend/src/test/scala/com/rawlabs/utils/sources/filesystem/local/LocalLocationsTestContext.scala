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

package com.rawlabs.utils.sources.filesystem.local

import com.rawlabs.utils.core.RawUtils
import com.rawlabs.utils.core._

import java.io.File
import java.nio.file.Path

trait LocalLocationsTestContext {

  def localPath(path: Path): String = localPath(path.toAbsolutePath.toString)

  def localPath(path: String): String = "file:" + path.replace("\\", "\\\\")

  lazy val stocks = localPath(RawUtils.getResource("data/stocks"))
  lazy val stocksPortfolio = localPath(RawUtils.getResource("data/stocks").resolve("portfolio.json"))

  lazy val authorsJsonLocal = localPath(RawUtils.getResource("data/publications/authors.json"))
  lazy val authorsHJsonLocalDir = localPath(RawUtils.getResource("data/publications/authors"))
  lazy val authorsHJsonLocalFiles = localPath(
    RawUtils.getResource("data/publications/authors").toAbsolutePath.toString + File.separator + "authors*.hjson"
  )
  lazy val authorsHjsonLocal = localPath(RawUtils.getResource("data/publications/authors.hjson"))
  lazy val authorsArrowLocal = localPath(RawUtils.getResource("data/publications/authors.arrow"))
  lazy val authorsParquetLocal = localPath(RawUtils.getResource("data/parquet/authors.parquet"))

  lazy val setOfIntsParquetLocal = localPath(RawUtils.getResource("data/set_of_ints.parquet"))
  lazy val setOfIntsCsvLocal = localPath(RawUtils.getResource("data/set_of_ints.csv"))
  lazy val setOfOptionsOfIntsCsvLocal = localPath(RawUtils.getResource("data/set_of_options_ints.csv"))
  lazy val setOfOptionsOfRecordsJsonLocal = localPath(RawUtils.getResource("data/set_of_options_records.json"))

  lazy val allPrimitivesJsonLocal = localPath(RawUtils.getResource("data/primitives.json"))
  lazy val allPrimitivesCsvLocal = localPath(RawUtils.getResource("data/primitives.csv"))
  lazy val allPrimitivesOptionsCsvLocal = localPath(RawUtils.getResource("data/primitives-options.csv"))
  lazy val allPrimitivesOptionsJsonLocal = localPath(RawUtils.getResource("data/primitives-options.json"))
  lazy val allPrimitivesOptionsHjsonLocal = localPath(RawUtils.getResource("data/primitives-options.hjson"))
  lazy val arraysJsonLocal = localPath(RawUtils.getResource("data/arrays.json"))

  lazy val authorsSmallJsonLocal = localPath(RawUtils.getResource("data/publications/authorsSmall.json"))
  lazy val authorsSmallHjsonLocal = localPath(RawUtils.getResource("data/publications/authorsSmall.hjson"))

  lazy val authorsJsonLocalDirectory = localPath(RawUtils.getResource("data/publications/authors-json/"))
  lazy val authorsJsonLocalDirectoryWildcard = localPath(
    RawUtils.getResource("data/publications/authors-json").toAbsolutePath.toString + File.separator + "*.json"
  )

  lazy val analyticsJsonLocal = localPath(RawUtils.getResource("data/google_analytics.json"))

  lazy val publicationsJsonLocal = localPath(RawUtils.getResource("data/publications/publications.json"))
  lazy val publicationsHjsonLocal = localPath(RawUtils.getResource("data/publications/publications.hjson"))

  lazy val publicationsHjsonLocalDirectory = localPath(RawUtils.getResource("data/publications/publications-hjson"))

  lazy val publicationsHjsonGzipLocalDirectory =
    localPath(RawUtils.getResource("data/publications/publications-hjson-gzip"))
  lazy val publicationsHjsonBzip2LocalDirectory =
    localPath(RawUtils.getResource("data/publications/publications-hjson-bzip2"))
  lazy val publicationsHjsonLz4LocalDirectory =
    localPath(RawUtils.getResource("data/publications/publications-hjson-lz4"))
  lazy val publicationsHjsonDeflateLocalDirectory =
    localPath(RawUtils.getResource("data/publications/publications-hjson-deflate"))
  lazy val publicationsHjsonSnappyLocalDirectory =
    localPath(RawUtils.getResource("data/publications/publications-hjson-snappy"))
  lazy val publicationsHjsonBzip2LocalFile = localPath(RawUtils.getResource("data/publications/publications.hjson.bz2"))
  lazy val publicationsHjsonGzLocalFile = localPath(RawUtils.getResource("data/publications/publications.hjson.gz"))

  lazy val publicationsParquetLocal = localPath(RawUtils.getResource("data/parquet/publications.parquet"))
  lazy val publicationsSmallJsonLocal = localPath(RawUtils.getResource("data/publications/publicationsSmall.json"))
  lazy val publicationsSmallHjsonLocal = localPath(RawUtils.getResource("data/publications/publicationsSmall.hjson"))
  lazy val publicationsSmallWithDupsJsonLocal =
    localPath(RawUtils.getResource("data/publications/publicationsSmallWithDups.json"))
  lazy val airportsLocal = localPath(RawUtils.getResource("data/airports.csv"))

  lazy val intThenLongLocal = localPath(RawUtils.getResource("data/int_then_long.hjson"))

  lazy val patientsJsonLocal = localPath(RawUtils.getResource("data/patients/patients.json"))
  lazy val patientsParquetLocal = localPath(RawUtils.getResource("data/parquet/patients.parquet"))

  lazy val allTypesJsonLocal = localPath(RawUtils.getResource("data/all_data_types.json"))

  lazy val studentsCsvLocal = localPath(RawUtils.getResource("data/students/students.csv"))
  lazy val studentsParquetLocal = localPath(RawUtils.getResource("data/parquet/students.parquet"))

  lazy val testAvroDirectoryLocal = localPath(RawUtils.getResource("data/test.avro"))
  lazy val testAvroFileLocal =
    localPath(RawUtils.getResource("data/test.avro/part-00000-a52cbaa7-4f7d-41a4-8308-aa742d0b1263-c000.avro"))

  lazy val testParquetDir = localPath(RawUtils.getResource("data/table1"))
  lazy val testParquetDirWildcard =
    localPath(RawUtils.getResource("data/table1").toAbsolutePath.toString + File.separator + "*.parquet")
  lazy val testParquetFileWithExtension =
    localPath(RawUtils.getResource("data/table1/part-00000-162798bb-c09f-4dd9-a57b-dd8223d1b973-c000.snappy.parquet"))
  lazy val testParquetFileWithoutExtension =
    localPath(RawUtils.getResource("data/part-00000-162798bb-c09f-4dd9-a57b-dd8223d1b973-c000"))

  lazy val lineitemAvroDirectoryLocal = localPath(RawUtils.getResource("data/lineitem.avro"))
  lazy val lineitemAvroLocal =
    localPath(RawUtils.getResource("data/lineitem.avro/part-00000-0e8d8118-0253-40bb-974f-046ebc59e65d-c000.avro"))
  lazy val manyTypesAvroDirectoryLocal = localPath(RawUtils.getResource("data/many_types.avro"))
  lazy val manyTypesAvroLocal =
    localPath(RawUtils.getResource("data/many_types.avro/part-00000-6b57cbb6-b0b2-4e73-b060-11a73c8ea3b5-c000.avro"))

  lazy val lineitemParquetDirectoryLocal = localPath(RawUtils.getResource("data/lineitem.parquet"))
  lazy val lineitemParquetLocal = localPath(
    RawUtils.getResource("data/lineitem.parquet/part-00000-f347740c-02a4-47cd-a1a7-4b9db277fb50-c000.snappy.parquet")
  )
  lazy val manyTypesParquetDirectoryLocal = localPath(RawUtils.getResource("data/many_types.parquet"))
  lazy val manyTypesParquetLocal = localPath(
    RawUtils.getResource("data/many_types.parquet/part-00000-9f1062e8-178e-4387-9130-016b92afee9f-c000.snappy.parquet")
  )

  lazy val studentsCsvNoHeaderLocal = localPath(RawUtils.getResource("data/students/students_no_header.csv"))
  lazy val httpLogsSmallUTF16LELocal = localPath(RawUtils.getResource("data/httplogs/NASA_access_log_Aug95_small"))
  lazy val httpLogsSmallUTF8Local = localPath(RawUtils.getResource("data/httplogs/NASA_access_log_Aug95_small_utf8"))
  lazy val httpLogsSmallUTF8LocalFolder = localPath(RawUtils.getResource("data/httplogs/log-folder"))

  lazy val studentsGuideCsvLocal = localPath(RawUtils.getResource("data/transition-guide/students.csv"))
  lazy val professorsGuideCsvLocal = localPath(RawUtils.getResource("data/transition-guide/professors.csv"))

  lazy val singleObjJsonLocal = localPath(RawUtils.getResource("data/jsonObjFolder/student1.json"))
  lazy val singleObjJsonLocalFolder = localPath(RawUtils.getResource("data/jsonObjFolder"))

  lazy val gerdLocal = localPath(RawUtils.getResource("data/gerd/GSSTF_NCEP.3.2008.json"))

  lazy val intsAsFieldsJsonLocal = localPath(RawUtils.getResource("data/ints_as_fields.json"))

  lazy val tpchCustomerParquetLocal = localPath(RawUtils.getResource("data/tpch/tiny/customer.parquet"))
  lazy val tpchCustomerTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/customer.tbl"))
  lazy val tpchLineitemTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/lineitem.tbl"))
  lazy val tpchNationTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/nation.tbl"))
  lazy val tpchOrdersTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/orders.tbl"))
  lazy val tpchPartTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/part.tbl"))
  lazy val tpchPartSuppTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/partsupp.tbl"))
  lazy val tpchRegionTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/region.tbl"))
  lazy val tpchSupplierTblLocal = localPath(RawUtils.getResource("data/tpch/tiny/supplier.tbl"))

  lazy val tpchCustomerCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/customer.csv"))
  lazy val tpchLineitemCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/lineitem.csv"))
  lazy val tpchNationCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/nation.csv"))
  lazy val tpchOrdersCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/orders.csv"))
  lazy val tpchPartCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/part.csv"))
  lazy val tpchPartSuppCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/partsupp.csv"))
  lazy val tpchRegionCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/region.csv"))
  lazy val tpchSupplierCsvLocal = localPath(RawUtils.getResource("data/tpch/tiny/csv/supplier.csv"))

  lazy val donutsHjsonLocal = localPath(RawUtils.getResource("data/donuts.hjson"))
  lazy val optRecordsJsonLocal = localPath(RawUtils.getResource("data/optional_records.json"))

  lazy val peopleHjsonLocal = localPath(RawUtils.getResource("data/hwpoc/people.hjson"))
  lazy val authorsNonAsciiCharsHjsonLocal = localPath(RawUtils.getResource("data/authors-non-ascii-chars.hjson"))

  lazy val primitivesXmlLocal = localPath(RawUtils.getResource("data/xml/primitives.xml"))
  lazy val primitivesXmlLocalFolder = localPath(RawUtils.getResource("data/xml/xml-folder"))
  lazy val singleObjXmlLocalFolder = localPath(RawUtils.getResource("data/xml/xmlObjFolder"))
  lazy val singleObjXmlLocal = localPath(RawUtils.getResource("data/xml/xmlObjFolder/student1.xml"))
  lazy val gerdXml = localPath(RawUtils.getResource("data/xml/GSSTF_NCEP.3.1997.05.17.he5.xml"))
  lazy val sigmodXml = localPath(RawUtils.getResource("data/xml/SigmodRecord.xml"))
  lazy val gerdXmlSmall = localPath(RawUtils.getResource("data/xml/raw-bar_xml_GSSTF.small.xml"))

  lazy val gerdXmlSmallRegularXml = localPath(RawUtils.getResource("data/xml/raw-bar_xml_GSSTF.small2.xml"))

  lazy val rawPostSiteMapXml = localPath(RawUtils.getResource("data/xml/raw-post-sitemap.xml"))

  lazy val excelBug = localPath(RawUtils.getResource("data/excel/excel-bug.xlsx"))
  lazy val peopleExcel = localPath(RawUtils.getResource("data/excel/people.xlsx"))
  lazy val peopleExcelFolder = localPath(RawUtils.getResource("data/excel/excel-folder"))
  lazy val csvFolder = localPath(RawUtils.getResource("data/csv-folder"))
  lazy val multiLineCsv = localPath(RawUtils.getResource("data/multi_line.csv"))

  lazy val mixedFormats = localPath(RawUtils.getResource("data/mixed-formats"))

  //  StructType(Array(
  //    StructField("name", StringType),
  //    StructField("age", IntegerType),
  //    StructField("income", DoubleType),
  //  ))
  lazy val sparkParquetPeople = localPath(RawUtils.getResource("data/parquet/spark_parquet_people"))
  // StructType(Array(
  //    StructField("_string", StringType),
  //    StructField("_int", IntegerType),
  //    StructField("_double", DoubleType),
  //    StructField("_bool", BooleanType),
  //    StructField("_long", LongType),
  //    StructField("_byte", ByteType),
  //    StructField("_float", FloatType),
  //    StructField("_decimal", DecimalType(38, 10)),
  //    StructField("_array", ArrayType(IntegerType)),
  //  ))
  lazy val sparkParquetTypes = localPath(RawUtils.getResource("data/parquet/spark_parquet_types"))

  lazy val moviesCsv = localPath(RawUtils.getResource("data/movies/movies.csv"))

  lazy val jiraIssues = localPath(RawUtils.getResource("data/jiraIssues"))
}
