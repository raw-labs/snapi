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

package raw.creds

trait RDBMSTestCreds {
  val mysqlTestHost = sys.env("RAW_MYSQL_TEST_HOST")
  val mysqlTestDB = sys.env("RAW_MYSQL_TEST_DB")
  val mysqlTestUser = sys.env("RAW_MYSQL_TEST_USER")
  val mysqlTestPassword = sys.env("RAW_MYSQL_TEST_PASSWORD")
  val mysqlCreds = MySqlCredential(mysqlTestHost, Some(3306), mysqlTestDB, Some(mysqlTestUser), Some(mysqlTestPassword))
  val pgsqlTestHost = sys.env("RAW_PGSQL_TEST_HOST")
  val pgsqlTestDB = sys.env("RAW_PGSQL_TEST_DB")
  val pgsqlTestUser = sys.env("RAW_PGSQL_TEST_USER")
  val pgsqlTestPassword = sys.env("RAW_PGSQL_TEST_PASSWORD")
  val pgsqlCreds =
    PostgresqlCredential(pgsqlTestHost, Some(5432), pgsqlTestDB, Some(pgsqlTestUser), Some(pgsqlTestPassword))
  val oracleTestHost = sys.env("RAW_ORACLE_TEST_HOST")
  val oracleTestDB = sys.env("RAW_ORACLE_TEST_DB")
  val oracleTestUser = sys.env("RAW_ORACLE_TEST_USER")
  val oracleTestPassword = sys.env("RAW_ORACLE_TEST_PASSWORD")
  val oracleCreds =
    OracleCredential(oracleTestHost, Some(1521), oracleTestDB, Some(oracleTestUser), Some(oracleTestPassword))
  val sqlServerTestHost = sys.env("RAW_SQLSERVER_TEST_HOST")
  val sqlserverTestDB = sys.env("RAW_SQLSERVER_TEST_DB")
  val sqlServerTestUser = sys.env("RAW_SQLSERVER_TEST_USER")
  val sqlServerTestPassword = sys.env("RAW_SQLSERVER_TEST_PASSWORD")
  val sqlServerCreds = SqlServerCredential(
    sqlServerTestHost,
    Some(1433),
    sqlserverTestDB,
    Some(sqlServerTestUser),
    Some(sqlServerTestPassword)
  )
  val teradataTestHost = sys.env("RAW_TERADATA_TEST_HOST")
  val teradataTestUser = sys.env("RAW_TERADATA_TEST_USER")
  val teradataTestPassword = sys.env("RAW_TERADATA_TEST_PASSWORD")
  val teradataCreds = TeradataCredential(teradataTestHost, None, Some(teradataTestUser), Some(teradataTestPassword))
  val snowflakeTestHost = sys.env("RAW_SNOWFLAKE_TEST_HOST")
  val snowflakeTestDB = sys.env("RAW_SNOWFLAKE_TEST_DB")
  val snowflakeTestUser = sys.env("RAW_SNOWFLAKE_TEST_USER")
  val snowflakeTestPassword = sys.env("RAW_SNOWFLAKE_TEST_PASSWORD")
  val snowflakeCreds = SnowflakeCredential(
    snowflakeTestHost,
    snowflakeTestDB,
    Some(snowflakeTestUser),
    Some(snowflakeTestPassword),
    Map("timezone" -> "UTC")
  )
  val badMysqlCreds =
    MySqlCredential("does-not-exist.raw-labs.com", Some(3306), "rdbmstest", Some("t0or"), Some("$up3r$3cr3tValu3"))
}
