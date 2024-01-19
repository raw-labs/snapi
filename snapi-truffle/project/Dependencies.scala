import sbt._

object Dependencies {

  val rawUtilsVersion = IO.read(new File("../utils/version")).trim
  val rawUtils = "com.raw-labs" %% "raw-utils" % rawUtilsVersion

  val rawSnapiFrontendVersion = IO.read(new File("../snapi-frontend/version")).trim
  val rawSnapiFrontend = "com.raw-labs" %% "raw-snapi-frontend" % rawSnapiFrontendVersion

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
