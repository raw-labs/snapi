import sbt._

object Dependencies {

  val rawClientVersion = IO.read(new File("../client/version")).trim
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion
  
  val rawSnapiFrontendVersion = IO.read(new File("../snapi-frontend/version")).trim
  val rawSnapiFrontend = "com.raw-labs" %% "raw-snapi-frontend" % rawSnapiFrontendVersion
  
  val rawSnapiParserVersion = IO.read(new File("../snapi-parser/version")).trim
  val rawSnapiParser = "com.raw-labs" %% "raw-snapi-parser" % rawSnapiParserVersion
  
  val kiamaVersion = IO.read(new File("../deps/kiama/version")).trim
  val kiama = "org.bitbucket.inkytonik.kiama" %% "kiama" % kiamaVersion
  
  val hikariCP = "com.zaxxer" % "HikariCP" % "5.1.0"
  val postgresqlDeps = "org.postgresql" % "postgresql" % "42.5.4"
}
