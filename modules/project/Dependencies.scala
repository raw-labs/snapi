package snapi.modules.build

import sbt._

object Dependencies {

  val rawUtilsVersion = IO.read(new File("../utils/version")).trim
  val rawUtils = "com.raw-labs" %% "raw-utils" % rawUtilsVersion

  val rawClientVersion = IO.read(new File("../client/version")).trim
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion

  val rawParsersVersion = IO.read(new File("../parsers/version")).trim
  val rawParsers = "com.raw-labs" % "raw-parsers" % rawParsersVersion

  val kiamaVersion = IO.read(new File("../deps/kiama/version")).trim
  val kiama = "org.bitbucket.inkytonik.kiama" %% "kiama" % kiamaVersion


  val aws = "software.amazon.awssdk" % "s3" % "2.20.69" exclude ("commons-logging", "commons-logging") // spring.jcl is the correct replacement for this one.

  val woodstox = "com.fasterxml.woodstox" % "woodstox-core" % "6.5.1"

  val kryo = "com.esotericsoftware" % "kryo" % "5.5.0"

  val commonsText = "org.apache.commons" % "commons-text" % "1.10.0"

  val commonsLang = "org.apache.commons" % "commons-lang3" % "3.13.0"

  val commonsCodec = "commons-codec" % "commons-codec" % "1.16.0"

  val apacheHttpClient = "org.apache.httpcomponents.client5" % "httpclient5" % "5.2.1"

  val dropboxSDK = "com.dropbox.core" % "dropbox-core-sdk" % "5.4.5"

  val postgresqlDeps = "org.postgresql" % "postgresql" % "42.5.4"

  val mysqlDeps = "com.mysql" % "mysql-connector-j" % "8.1.0-rawlabs"

  val mssqlDeps = "com.microsoft.sqlserver" % "mssql-jdbc" % "7.0.0.jre10"

  val snowflakeDeps = "net.snowflake" % "snowflake-jdbc" % "3.13.33"

  val icuDeps = "com.ibm.icu" % "icu4j" % "73.2"

  val poiDeps = Seq(
    "org.apache.poi" % "poi" % "5.2.3",
    "org.apache.poi" % "poi-ooxml" % "5.2.3",
    "org.apache.poi" % "poi-ooxml-lite" % "5.2.3"
  )

  val jwtApi = "io.jsonwebtoken" % "jjwt-api" % "0.11.5"

  val jwtImpl = "io.jsonwebtoken" % "jjwt-impl" % "0.11.5"

  val jwtCore = "com.github.jwt-scala" %% "jwt-core" % "9.4.4-rawlabs"

  val springCore = "org.springframework" % "spring-core" % "5.3.13"

  val truffleCompiler = Seq(
    "org.graalvm.truffle" % "truffle-api" % "23.1.0",
    "org.graalvm.truffle" % "truffle-api" % "23.1.0",
    "org.graalvm.truffle" % "truffle-compiler" % "23.1.0",
    "org.graalvm.truffle" % "truffle-nfi" % "23.1.0",
    "org.graalvm.truffle" % "truffle-nfi-libffi" % "23.1.0",
    "org.graalvm.truffle" % "truffle-runtime" % "23.1.0"
  )

  val scalaCompiler = Seq(
    "org.scala-lang" % "scala-compiler" % "2.12.18",
    "org.scala-lang" % "scala-reflect" % "2.12.18"
  )

}
