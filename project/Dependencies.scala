package raw.build

import sbt._

object Dependencies {

  // from utils
  val scalatest = "org.scalatest" %% "scalatest" % "3.2.16"

  val slf4j = Seq(
    "org.slf4j" % "slf4j-api" % "2.0.5",
    "org.slf4j" % "log4j-over-slf4j" % "2.0.5",
    "org.slf4j" % "jcl-over-slf4j" % "2.0.5",
    "org.slf4j" % "jul-to-slf4j" % "2.0.5"
  )

  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5-rawlabs"

  val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.4.12"

  val loki4jAppender = "com.github.loki4j" % "loki-logback-appender" % "1.4.2"

  val commonsIO = "commons-io" % "commons-io" % "2.11.0"
  val commonsText = "org.apache.commons" % "commons-text" % "1.11.0"

  val commonsCodec = "commons-codec" % "commons-codec" % "1.16.0"

  val guava = "com.google.guava" % "guava" % "32.1.2-jre"

  val jacksonDeps = Seq(
    "com.fasterxml.jackson.core" % "jackson-core" % "2.15.2",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.15.2",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.15.2",
    "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.15.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.15.2-rawlabs",
    "com.fasterxml.jackson.jakarta.rs" % "jackson-jakarta-rs-json-provider" % "2.15.2"
  )

  // Required while we are on Scala 2.12. It's built into Scala 2.13.
  val scalaJava8Compat = "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2"

  val typesafeConfig = "com.typesafe" % "config" % "1.4.2"


  // from client
  val trufflePolyglot = "org.graalvm.polyglot" % "polyglot" % "23.1.0"


  // from snapi-parser
  val antlr4Runtime = "org.antlr" % "antlr4-runtime" % "4.12.0"

  // from snapi-frontend
  val kiamaVersion = IO.read(new File("./deps/kiama/version")).trim
  val kiama = "org.bitbucket.inkytonik.kiama" %% "kiama" % kiamaVersion

  val aws = "software.amazon.awssdk" % "s3" % "2.20.69" exclude ("commons-logging", "commons-logging") // spring.jcl is the correct replacement for this one.
  val woodstox = "com.fasterxml.woodstox" % "woodstox-core" % "6.5.1"
  val kryo = "com.esotericsoftware" % "kryo" % "5.5.0"
  val commonsLang = "org.apache.commons" % "commons-lang3" % "3.13.0"
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
