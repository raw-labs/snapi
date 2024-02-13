import sbt._

object Dependencies {

  val rawClientVersion = IO.read(new File("../client/version")).trim
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion
  val hikariCP = "com.zaxxer" % "HikariCP" % "5.1.0"
  val postgresqlDeps = "org.postgresql" % "postgresql" % "42.5.4"
  val antlr4 = "org.antlr" % "antlr4-runtime" % "4.12.0"
}
