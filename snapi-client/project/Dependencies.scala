import sbt._

object Dependencies {

  def getVersionFromPath(path: String, default: String = "0.0.0"): String = {
    val file = new File(path)
    if (file.exists()) IO.read(file).trim else default
  }

  val rawClientVersion: String = getVersionFromPath("../client/version")
  val rawClient = "com.raw-labs" %% "raw-client" % rawClientVersion

  val rawSnapiFrontendVersion: String = getVersionFromPath("../snapi-frontend/version")
  val rawSnapiFrontend = "com.raw-labs" %% "raw-snapi-frontend" % rawSnapiFrontendVersion

  val rawSnapiTruffleVersion: String = getVersionFromPath("../snapi-truffle/version")
  val rawSnapiTruffle = "com.raw-labs" %% "raw-snapi-truffle" % rawSnapiTruffleVersion
}
