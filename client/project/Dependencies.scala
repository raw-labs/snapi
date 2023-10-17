import sbt._

object Dependencies {

  val rawUtilsVersion = IO.read(new File("../utils/version")).trim
  val rawUtils = "com.raw-labs" %% "raw-utils" % rawUtilsVersion

  val trufflePolyglot = "org.graalvm.polyglot" % "polyglot" % "23.1.0"

}
