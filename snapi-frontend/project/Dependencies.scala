import sbt._

object Dependencies {

  val rawUtilsVersion = IO.read(new File("../utils/version")).trim
  val rawUtils = "com.raw-labs" %% "raw-utils" % rawUtilsVersion

  val rawClientVersion = IO.read(new File("../client/version")).trim
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion

  val scalaLoggingVersion = IO.read(new File("../deps/scala-logging/version")).trim
  val kiamaVersion = IO.read(new File("../deps/kiama/version")).trim

  val aws =
    "software.amazon.awssdk" % "s3" % "2.20.69" exclude ("commons-logging", "commons-logging") // spring.jcl is the correct replacement for this one.

  val woodstox = "com.fasterxml.woodstox" % "woodstox-core" % "6.5.1"

  val kiama = "org.bitbucket.inkytonik.kiama" %% "kiama" % kiamaVersion

  val kryo = "com.esotericsoftware" % "kryo" % "5.5.0"

  val commonsText = "org.apache.commons" % "commons-text" % "1.10.0"

  val commonsLang = "org.apache.commons" % "commons-lang3" % "3.13.0"

  val commonsCodec = "commons-codec" % "commons-codec" % "1.16.0"

  val apacheHttpClient = "org.apache.httpcomponents.client5" % "httpclient5" % "5.2.1"

  val dropboxSDK = "com.dropbox.core" % "dropbox-core-sdk" % "5.4.5"

  val postgresqlDeps = "org.postgresql" % "postgresql" % "42.5.4"

  val mysqlDeps = "com.mysql" % "mysql-connector-j" % "8.1.0"

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

  val jwtCore = "com.github.jwt-scala" %% "jwt-core" % "9.4.4"

  val springCore = "org.springframework" % "spring-core" % "5.3.13"

}
