import sbt._

object Dependencies {

  val rawClientVersion = IO.read(new File("../client/version")).trim
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion

  val postgresqlDeps = "org.postgresql" % "postgresql" % "42.5.4"
}
