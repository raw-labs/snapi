import sbt._

object Dependencies {

  val rawClientVersion = IO.read(new File("../client/version")).trim
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion

  val rawSnapiParserVersion = IO.read(new File("../snapi-parser/version")).trim
  val rawSnapiParser = "com.raw-labs" %% "raw-snapi-parser" % rawSnapiParserVersion

  val rawSnapiFrontendVersion = IO.read(new File("../snapi-frontend/version")).trim
  val rawSnapiFrontend = "com.raw-labs" %% "raw-snapi-frontend" % rawSnapiFrontendVersion

  val rawSnapiTruffleVersion = IO.read(new File("../snapi-truffle/version")).trim
  val rawSnapiTruffle = "com.raw-labs" %% "raw-snapi-truffle" % rawSnapiTruffleVersion

}
