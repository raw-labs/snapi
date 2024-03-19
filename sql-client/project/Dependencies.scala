import sbt._

object Dependencies {

  val rawClientVersion = IO.read(new File("../client/version")).trim
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion
  val rawSnapiFrontendVersion = IO.read(new File("../snapi-frontend/version")).trim
  val rawSnapiFrontend = "com.raw-labs" %% "raw-snapi-frontend" % rawSnapiFrontendVersion
  val hikariCP = "com.zaxxer" % "HikariCP" % "5.1.0"
  val postgresqlDeps = "org.postgresql" % "postgresql" % "42.5.4"
}
