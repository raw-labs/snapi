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

package raw.sources.filesystem.local

import java.io.File
import java.nio.file.Path

import raw.utils._

trait LocalLocationsTestContext {

  def localPath(path: Path): String = localPath(path.toAbsolutePath.toString)

  def localPath(path: String): String = "file:" + path.replace("\\", "\\\\")

  lazy val stocks = localPath(getResource("data/stocks"))
  lazy val stocksPortfolio = localPath(getResource("data/stocks").resolve("portfolio.json"))

  lazy val authorsJsonLocal = localPath(getResource("data/publications/authors.json"))
  lazy val authorsHJsonLocalDir = localPath(getResource("data/publications/authors"))
  lazy val authorsHJsonLocalFiles = localPath(
    getResource("data/publications/authors").toAbsolutePath.toString + File.separator + "authors*.hjson"
  )
  lazy val authorsHjsonLocal = localPath(getResource("data/publications/authors.hjson"))
  lazy val authorsArrowLocal = localPath(getResource("data/publications/authors.arrow"))
  lazy val authorsParquetLocal = localPath(getResource("data/parquet/authors.parquet"))

  lazy val setOfIntsParquetLocal = localPath(getResource("data/set_of_ints.parquet"))
  lazy val setOfIntsCsvLocal = localPath(getResource("data/set_of_ints.csv"))
  lazy val setOfOptionsOfIntsCsvLocal = localPath(getResource("data/set_of_options_ints.csv"))
  lazy val setOfOptionsOfRecordsJsonLocal = localPath(getResource("data/set_of_options_records.json"))

  lazy val allPrimitivesJsonLocal = localPath(getResource("data/primitives.json"))
  lazy val allPrimitivesCsvLocal = localPath(getResource("data/primitives.csv"))
  lazy val allPrimitivesOptionsCsvLocal = localPath(getResource("data/primitives-options.csv"))
  lazy val allPrimitivesOptionsJsonLocal = localPath(getResource("data/primitives-options.json"))
  lazy val allPrimitivesOptionsHjsonLocal = localPath(getResource("data/primitives-options.hjson"))
  lazy val arraysJsonLocal = localPath(getResource("data/arrays.json"))

  lazy val authorsSmallJsonLocal = localPath(getResource("data/publications/authorsSmall.json"))
  lazy val authorsSmallHjsonLocal = localPath(getResource("data/publications/authorsSmall.hjson"))

  lazy val authorsJsonLocalDirectory = localPath(getResource("data/publications/authors-json/"))
  lazy val authorsJsonLocalDirectoryWildcard = localPath(
    getResource("data/publications/authors-json").toAbsolutePath.toString + File.separator + "*.json"
  )

  lazy val analyticsJsonLocal = localPath(getResource("data/google_analytics.json"))

  lazy val publicationsJsonLocal = localPath(getResource("data/publications/publications.json"))
  lazy val publicationsHjsonLocal = localPath(getResource("data/publications/publications.hjson"))

  lazy val publicationsHjsonLocalDirectory = localPath(getResource("data/publications/publications-hjson"))

  lazy val publicationsHjsonGzipLocalDirectory = localPath(getResource("data/publications/publications-hjson-gzip"))
  lazy val publicationsHjsonBzip2LocalDirectory = localPath(getResource("data/publications/publications-hjson-bzip2"))
  lazy val publicationsHjsonLz4LocalDirectory = localPath(getResource("data/publications/publications-hjson-lz4"))
  lazy val publicationsHjsonDeflateLocalDirectory =
    localPath(getResource("data/publications/publications-hjson-deflate"))
  lazy val publicationsHjsonSnappyLocalDirectory = localPath(getResource("data/publications/publications-hjson-snappy"))
  lazy val publicationsHjsonBzip2LocalFile = localPath(getResource("data/publications/publications.hjson.bz2"))
  lazy val publicationsHjsonGzLocalFile = localPath(getResource("data/publications/publications.hjson.gz"))

  lazy val publicationsParquetLocal = localPath(getResource("data/parquet/publications.parquet"))
  lazy val publicationsSmallJsonLocal = localPath(getResource("data/publications/publicationsSmall.json"))
  lazy val publicationsSmallHjsonLocal = localPath(getResource("data/publications/publicationsSmall.hjson"))
  lazy val publicationsSmallWithDupsJsonLocal =
    localPath(getResource("data/publications/publicationsSmallWithDups.json"))
  lazy val airportsLocal = localPath(getResource("data/airports.csv"))

  lazy val intThenLongLocal = localPath(getResource("data/int_then_long.hjson"))

  lazy val patientsJsonLocal = localPath(getResource("data/patients/patients.json"))
  lazy val patientsParquetLocal = localPath(getResource("data/parquet/patients.parquet"))

  lazy val allTypesJsonLocal = localPath(getResource("data/all_data_types.json"))

  lazy val studentsCsvLocal = localPath(getResource("data/students/students.csv"))
  lazy val studentsParquetLocal = localPath(getResource("data/parquet/students.parquet"))

  lazy val testAvroDirectoryLocal = localPath(getResource("data/test.avro"))
  lazy val testAvroFileLocal =
    localPath(getResource("data/test.avro/part-00000-a52cbaa7-4f7d-41a4-8308-aa742d0b1263-c000.avro"))

  lazy val testParquetDir = localPath(getResource("data/table1"))
  lazy val testParquetDirWildcard =
    localPath(getResource("data/table1").toAbsolutePath.toString + File.separator + "*.parquet")
  lazy val testParquetFileWithExtension =
    localPath(getResource("data/table1/part-00000-162798bb-c09f-4dd9-a57b-dd8223d1b973-c000.snappy.parquet"))
  lazy val testParquetFileWithoutExtension =
    localPath(getResource("data/part-00000-162798bb-c09f-4dd9-a57b-dd8223d1b973-c000"))

  lazy val lineitemAvroDirectoryLocal = localPath(getResource("data/lineitem.avro"))
  lazy val lineitemAvroLocal =
    localPath(getResource("data/lineitem.avro/part-00000-0e8d8118-0253-40bb-974f-046ebc59e65d-c000.avro"))
  lazy val manyTypesAvroDirectoryLocal = localPath(getResource("data/many_types.avro"))
  lazy val manyTypesAvroLocal =
    localPath(getResource("data/many_types.avro/part-00000-6b57cbb6-b0b2-4e73-b060-11a73c8ea3b5-c000.avro"))

  lazy val lineitemParquetDirectoryLocal = localPath(getResource("data/lineitem.parquet"))
  lazy val lineitemParquetLocal = localPath(
    getResource("data/lineitem.parquet/part-00000-f347740c-02a4-47cd-a1a7-4b9db277fb50-c000.snappy.parquet")
  )
  lazy val manyTypesParquetDirectoryLocal = localPath(getResource("data/many_types.parquet"))
  lazy val manyTypesParquetLocal = localPath(
    getResource("data/many_types.parquet/part-00000-9f1062e8-178e-4387-9130-016b92afee9f-c000.snappy.parquet")
  )

  lazy val studentsCsvNoHeaderLocal = localPath(getResource("data/students/students_no_header.csv"))
  lazy val httpLogsSmallUTF16LELocal = localPath(getResource("data/httplogs/NASA_access_log_Aug95_small"))
  lazy val httpLogsSmallUTF8Local = localPath(getResource("data/httplogs/NASA_access_log_Aug95_small_utf8"))
  lazy val httpLogsSmallUTF8LocalFolder = localPath(getResource("data/httplogs/log-folder"))

  lazy val studentsGuideCsvLocal = localPath(getResource("data/transition-guide/students.csv"))
  lazy val professorsGuideCsvLocal = localPath(getResource("data/transition-guide/professors.csv"))

  lazy val singleObjJsonLocal = localPath(getResource("data/jsonObjFolder/student1.json"))
  lazy val singleObjJsonLocalFolder = localPath(getResource("data/jsonObjFolder"))

  lazy val gerdLocal = localPath(getResource("data/gerd/GSSTF_NCEP.3.2008.json"))

  lazy val intsAsFieldsJsonLocal = localPath(getResource("data/ints_as_fields.json"))

  lazy val tpchCustomerParquetLocal = localPath(getResource("data/tpch/tiny/customer.parquet"))
  lazy val tpchCustomerTblLocal = localPath(getResource("data/tpch/tiny/customer.tbl"))
  lazy val tpchLineitemTblLocal = localPath(getResource("data/tpch/tiny/lineitem.tbl"))
  lazy val tpchNationTblLocal = localPath(getResource("data/tpch/tiny/nation.tbl"))
  lazy val tpchOrdersTblLocal = localPath(getResource("data/tpch/tiny/orders.tbl"))
  lazy val tpchPartTblLocal = localPath(getResource("data/tpch/tiny/part.tbl"))
  lazy val tpchPartSuppTblLocal = localPath(getResource("data/tpch/tiny/partsupp.tbl"))
  lazy val tpchRegionTblLocal = localPath(getResource("data/tpch/tiny/region.tbl"))
  lazy val tpchSupplierTblLocal = localPath(getResource("data/tpch/tiny/supplier.tbl"))

  lazy val tpchCustomerCsvLocal = localPath(getResource("data/tpch/tiny/csv/customer.csv"))
  lazy val tpchLineitemCsvLocal = localPath(getResource("data/tpch/tiny/csv/lineitem.csv"))
  lazy val tpchNationCsvLocal = localPath(getResource("data/tpch/tiny/csv/nation.csv"))
  lazy val tpchOrdersCsvLocal = localPath(getResource("data/tpch/tiny/csv/orders.csv"))
  lazy val tpchPartCsvLocal = localPath(getResource("data/tpch/tiny/csv/part.csv"))
  lazy val tpchPartSuppCsvLocal = localPath(getResource("data/tpch/tiny/csv/partsupp.csv"))
  lazy val tpchRegionCsvLocal = localPath(getResource("data/tpch/tiny/csv/region.csv"))
  lazy val tpchSupplierCsvLocal = localPath(getResource("data/tpch/tiny/csv/supplier.csv"))

  lazy val donutsHjsonLocal = localPath(getResource("data/donuts.hjson"))
  lazy val optRecordsJsonLocal = localPath(getResource("data/optional_records.json"))

  lazy val peopleHjsonLocal = localPath(getResource("data/hwpoc/people.hjson"))
  lazy val authorsNonAsciiCharsHjsonLocal = localPath(getResource("data/authors-non-ascii-chars.hjson"))

  lazy val primitivesXmlLocal = localPath(getResource("data/xml/primitives.xml"))
  lazy val primitivesXmlLocalFolder = localPath(getResource("data/xml/xml-folder"))
  lazy val singleObjXmlLocalFolder = localPath(getResource("data/xml/xmlObjFolder"))
  lazy val singleObjXmlLocal = localPath(getResource("data/xml/xmlObjFolder/student1.xml"))
  lazy val gerdXml = localPath(getResource("data/xml/GSSTF_NCEP.3.1997.05.17.he5.xml"))
  lazy val sigmodXml = localPath(getResource("data/xml/SigmodRecord.xml"))
  lazy val gerdXmlSmall = localPath(getResource("data/xml/raw-bar_xml_GSSTF.small.xml"))

  lazy val gerdXmlSmallRegularXml = localPath(getResource("data/xml/raw-bar_xml_GSSTF.small2.xml"))

  lazy val rawPostSiteMapXml = localPath(getResource("data/xml/raw-post-sitemap.xml"))

  lazy val excelBug = localPath(getResource("data/excel/excel-bug.xlsx"))
  lazy val peopleExcel = localPath(getResource("data/excel/people.xlsx"))
  lazy val peopleExcelFolder = localPath(getResource("data/excel/excel-folder"))
  lazy val csvFolder = localPath(getResource("data/csv-folder"))
  lazy val multiLineCsv = localPath(getResource("data/multi_line.csv"))

  lazy val mixedFormats = localPath(getResource("data/mixed-formats"))

  //  StructType(Array(
  //    StructField("name", StringType),
  //    StructField("age", IntegerType),
  //    StructField("income", DoubleType),
  //  ))
  lazy val sparkParquetPeople = localPath(getResource("data/parquet/spark_parquet_people"))
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
  lazy val sparkParquetTypes = localPath(getResource("data/parquet/spark_parquet_types"))

  lazy val moviesCsv = localPath(getResource("data/movies/movies.csv"))

  lazy val jiraIssues = localPath(getResource("data/jiraIssues"))
}
