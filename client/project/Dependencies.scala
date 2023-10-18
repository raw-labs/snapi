import sbt._

object Dependencies {

  def getVersionFromPath(path: String, default: String = "0.0.0"): String = {
    val file = new File(path)
    if (file.exists()) IO.read(file).trim else default
  }

  val rawUtilsVersion: String = getVersionFromPath("../utils/version")
  val rawUtils = "com.raw-labs" %% "raw-utils" % rawUtilsVersion

  val trufflePolyglot = "org.graalvm.polyglot" % "polyglot" % "23.1.0"

}
