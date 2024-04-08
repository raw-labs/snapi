import sbt.Keys._
import sbt._

import snapi.modules.build.BuildSettings._
import snapi.modules.build.Dependencies._

import scala.sys.process.Process

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

ThisBuild / publish / skip := true

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / sonatypeProfileName := "com.raw-labs"

lazy val snapiFrontend = (project in file("snapi-frontend"))
  .settings(
    commonSettings,
    snapiFrontendCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      rawClient % "compile->compile;test->test",
      rawParsers % "compile->compile;test->test",
      commonsLang,
      commonsText,
      apacheHttpClient,
      icuDeps,
      woodstox,
      kiama,
      dropboxSDK,
      aws,
      jwtApi,
      jwtImpl,
      jwtCore,
      postgresqlDeps,
      mysqlDeps,
      mssqlDeps,
      snowflakeDeps,
      commonsCodec,
      springCore,
      kryo
    ) ++
      poiDeps
  )

val annotationProcessors = Seq(
  "com.oracle.truffle.dsl.processor.TruffleProcessor",
  "com.oracle.truffle.dsl.processor.verify.VerifyTruffleProcessor",
  "com.oracle.truffle.dsl.processor.LanguageRegistrationProcessor",
  "com.oracle.truffle.dsl.processor.InstrumentRegistrationProcessor",
  "com.oracle.truffle.dsl.processor.OptionalResourceRegistrationProcessor",
  "com.oracle.truffle.dsl.processor.InstrumentableProcessor",
  "com.oracle.truffle.dsl.processor.verify.VerifyCompilationFinalProcessor",
  "com.oracle.truffle.dsl.processor.OptionProcessor"
).mkString(",")

val calculateSnapiTruffleClasspath = taskKey[Seq[File]]("Calculate the full classpath for snapi-truffle")

calculateSnapiTruffleClasspath := {
  val dependencyFiles = (snapiTruffle / Compile / dependencyClasspath).value.files
  val unmanagedFiles = (snapiTruffle / Compile / unmanagedClasspath).value.files
  val classesDir = (snapiTruffle / Compile / classDirectory).value

  dependencyFiles ++ unmanagedFiles ++ Seq(classesDir)
}

val runJavaAnnotationProcessor = taskKey[Unit]("Runs the Java annotation processor")

runJavaAnnotationProcessor := {
  println("Running Java annotation processor")

  val annotationProcessorJar = baseDirectory.value / "snapi-truffle" / "truffle-dsl-processor-23.1.0.jar"

  val javaSources = baseDirectory.value / "snapi-truffle" / "src" / "main" / "java"
  val targetDir = baseDirectory.value / "snapi-truffle" / "target" / "java-processed-sources"

  val projectClasspath = calculateSnapiTruffleClasspath.value.mkString(":")

  val javacOptions = Seq(
    "javac",
    "-source",
    "21",
    "-target",
    "21",
    "-d",
    targetDir.getAbsolutePath,
    "--module-path",
    projectClasspath,
    "-cp",
    annotationProcessorJar.getAbsolutePath,
    "-processor",
    annotationProcessors,
    "-proc:only"
  ) ++ (javaSources ** "*.java").get.map(_.absolutePath)

  // Create the target directory if it doesn't exist
  targetDir.mkdirs()

  // Execute the Java compiler
  val result = Process(javacOptions).!
  if (result != 0) {
    throw new RuntimeException("Java annotation processing failed.")
  }
}

lazy val snapiTruffle = (project in file("snapi-truffle"))
  .dependsOn(
    snapiFrontend % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    snapiTruffleCompileSettings,
    testSettings,
    //WIP
    // to be fixed with auto plugin logic
    // for now this task is out of this project scope
    //(Compile / compile) := ((Compile / compile) dependsOn runJavaAnnotationProcessor).value,
    libraryDependencies ++= Seq(
      rawUtils % "compile->compile;test->test"
      ) ++ truffleCompiler ++ scalaCompiler,
  )

lazy val snapiClient = (project in file("snapi-client"))
  .dependsOn(
    snapiFrontend % "compile->compile;test->test",
    snapiTruffle % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    snapiClientCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      rawClient % "compile->compile;test->test"
    )
  )
