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

package raw.sources.filesystem.s3

import raw.creds.s3.S3TestCreds

trait S3LocationsTestContext extends S3TestCreds {

  def s3Path(bucket: String, path: String): String = s"s3://$bucket/$path"

  lazy val publicationsHjsonGzipS3Directory = s3Path(UnitTestPrivateBucket.name, "publications/publications-hjson-gzip")
  lazy val publicationsHjsonBzip2S3Directory =
    s3Path(UnitTestPrivateBucket.name, "publications/publications-hjson-bzip2")
  lazy val publicationsHjsonLz4S3Directory = s3Path(UnitTestPrivateBucket.name, "publications/publications-hjson-lz4")
  lazy val publicationsHjsonDeflateS3Directory =
    s3Path(UnitTestPrivateBucket.name, "publications/publications-hjson-deflate")
  lazy val publicationsHjsonSnappyS3Directory =
    s3Path(UnitTestPrivateBucket.name, "publications/publications-hjson-snappy")
  lazy val publicationsHjsonBzip2S3File = s3Path(UnitTestPrivateBucket.name, "publications/publications.hjson.bz2")
  lazy val publicationsHjsonGzS3File = s3Path(UnitTestPrivateBucket.name, "publications/publications.hjson.gz")

  lazy val publicationsHjsonS3 = s3Path(UnitTestPublicBucket.name, "publications/publications.hjson")
  lazy val patientsParquetAvroS3 = s3Path(UnitTestPublicBucket.name, "patients.avro.parquet")

  // Public test data
  lazy val studentsCsvS3 = s3Path(UnitTestPublicBucket.name, "students.csv")
  lazy val authorsJsonS3 = s3Path(UnitTestPublicBucket.name, "publications/authors.json")
  lazy val authorsHjsonS3 = s3Path(UnitTestPublicBucket.name, "publications/authors.hjson")
  lazy val authorsParquetS3 = s3Path(UnitTestPublicBucket.name, "publications/authors.parquet")
  lazy val textS3 = s3Path(UnitTestPublicBucket.name, "log_file.txt")
  lazy val discogsS3 = s3Path(UnitTestPublicBucket.name, "discogs_2.xml")

  // Private test data, authenticated access
  lazy val authorsHjsonPrivateS3 = s3Path(UnitTestPrivateBucket.name, "authors.hjson")
  lazy val authorsJsonPrivateS3 = s3Path(UnitTestPrivateBucket.name, "authors.json")
  lazy val studentsCsvPrivateS3 = s3Path(UnitTestPrivateBucket.name, "students.csv")
  lazy val authorsParquetPrivateS3 = s3Path(UnitTestPrivateBucket.name, "authors.parquet")

  lazy val gerdGsstfXmlS3 = s3Path(UnitTestPublicBucket.name, "GSSTF.xml")
  lazy val gerdGsstfXmlS3Wildcard = s3Path(UnitTestPublicBucket.name, "GSSTF*.xml")

  lazy val rd3652File = s3Path(UnitTestPublicBucket.name, "RD-3652.csv")

}
