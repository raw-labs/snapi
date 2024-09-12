import sbt.Keys._
import sbt._

import raw.build.Dependencies._
import raw.build.BuildSettings._

import scala.sys.process._

import com.jsuereth.sbtpgp.PgpKeys.publishSigned

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / sonatypeProfileName := "com.raw-labs"
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "raw-labs",
  sys.env.getOrElse("GITHUB_TOKEN", "")
)
ThisBuild / resolvers += "Github RAW snapi repo" at "https://maven.pkg.github.com/raw-labs/snapi"
ThisBuild / resolvers += "Github RAW kiama repo" at "https://maven.pkg.github.com/raw-labs/kiama"

ThisBuild / javaHome := {
  val javaHomePath = sys.env.getOrElse("JAVA_HOME", sys.props("java.home"))
  println(s"Using Java Home: $javaHomePath")
  Some(file(javaHomePath))
}

val writeVersionToFile = taskKey[Unit]("Writes the project version to a file at the root.")

writeVersionToFile := {
  val file = (ThisBuild / baseDirectory).value / "version"
  val versionString = (ThisBuild / version).value
  IO.write(file, versionString)
  streams.value.log.info(s"Project version $versionString written to ${file.getPath}")
}

lazy val root = (project in file("."))
  .doPatchDependencies()
  .aggregate(
    compiler,
    snapiParser,
    snapiFrontend,
    snapiTruffle,
    snapiCompiler,
    sqlParser,
    sqlCompiler
  )
  .settings(
    commonSettings,
    publish := (publish dependsOn writeVersionToFile).value,
    publishLocal := (publishLocal dependsOn writeVersionToFile).value,
    publishSigned := (publishSigned dependsOn writeVersionToFile).value,
    publish / skip := true,
    publishSigned / skip := true,
    publishLocal / skip := true
  )

lazy val compiler = (project in file("compiler"))
  .doPatchDependencies()
  .settings(
    commonSettings,
    // Ignore deprecation warnings in the compiler. Needed for a Jackson feature we require.
    // TODO (msb): When in Scala 2.13, use @nowarn annotation instead in the exact code location.
    nonStrictScalaCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      utilsCore % "compile->compile;test->test",
      protocolCompiler % "compile->compile;test->test",
      trufflePolyglot
    ) ++ jacksonDeps
  )

lazy val snapiParser = (project in file("snapi-parser"))
  .doPatchDependencies()
  .enablePlugins(GenParserPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    parserDefinitions := List(
      (
        s"${(Compile / sourceManaged).value}/java/com/rawlabs/snapi/parser/generated",
        "com.rawlabs.snapi.parser.generated",
        s"${(Compile / sourceDirectory).value}/java/com/rawlabs/snapi/parser/grammar",
        "Snapi"
      )
    ),
    Compile / doc := { file("/dev/null") },
    libraryDependencies += antlr4Runtime
  )

lazy val snapiFrontend = (project in file("snapi-frontend"))
  .doPatchDependencies()
  .dependsOn(
    compiler % "compile->compile;test->test",
    snapiParser % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    nonStrictScalaCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      utilsCore % "compile->compile;test->test",
      utilsSources % "compile->compile;test->test",
      commonsLang,
      commonsText,
      icuDeps,
      woodstox,
      kiama,
      commonsCodec,
      kryo
    )
  )

val calculateClasspath = taskKey[Seq[File]]("Calculate the full classpath")
val runJavaAnnotationProcessor = taskKey[Unit]("Runs the Java annotation processor")

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

lazy val snapiTruffle = (project in file("snapi-truffle"))
  .doPatchDependencies()
  .dependsOn(
    snapiFrontend % "compile->compile;test->test"
  )
  .enablePlugins(JavaAnnotationProcessorPlugin)
  .settings(
    commonSettings,
    snapiTruffleCompileSettings,
    testSettings,
    libraryDependencies ++= truffleCompiler ++ Seq(
      utilsCore % "compile->compile;test->test"
    ),
    calculateClasspath := {
      val dependencyFiles = (Compile / dependencyClasspath).value.files
      val unmanagedFiles = (Compile / unmanagedClasspath).value.files
      val classesDir = (Compile / classDirectory).value
      dependencyFiles ++ unmanagedFiles ++ Seq(classesDir)
    },
    runJavaAnnotationProcessor := {
      println("Running Java annotation processor")

      val javaHomeDir = javaHome.value.getOrElse(sys.error("JAVA_HOME is not set"))
      val javacExecutable = javaHomeDir / "bin" / "javac"
      val annotationProcessorJar = baseDirectory.value / "truffle-dsl-processor-23.1.0.jar"

      val javaSources = baseDirectory.value / "src" / "main" / "java"
      val targetDir = baseDirectory.value / "target" / "java-processed-sources"

      val projectClasspath = calculateClasspath.value.mkString(":")

      val javacOptions = Seq(
        javacExecutable.absolutePath,
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
    },
    Compile / compile := (Compile / compile).dependsOn(runJavaAnnotationProcessor).value,
    Compile / doc := (Compile / doc).dependsOn(runJavaAnnotationProcessor).value,
    Test / compile := (Test / compile).dependsOn(runJavaAnnotationProcessor).value,
    publish := (publish dependsOn runJavaAnnotationProcessor).value,
    publishLocal := (publishLocal dependsOn runJavaAnnotationProcessor).value,
    publishSigned := (publishSigned dependsOn runJavaAnnotationProcessor).value
  )

lazy val snapiCompiler = (project in file("snapi-compiler"))
  .doPatchDependencies()
  .dependsOn(
    compiler % "compile->compile;test->test",
    snapiFrontend % "compile->compile;test->test",
    snapiTruffle % "test->test"
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings
  )

lazy val sqlParser = (project in file("sql-parser"))
  .doPatchDependencies()
  .enablePlugins(GenParserPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    parserDefinitions := List(
      (
        s"${(Compile / sourceManaged).value}/java/com/rawlabs/sql/parser/generated",
        "com.rawlabs.sql.parser.generated",
        s"${(Compile / sourceDirectory).value}/java/com/rawlabs/sql/parser/grammar",
        "Psql"
      )
    ),
    Compile / doc := { file("/dev/null") },
    libraryDependencies += antlr4Runtime
  )

lazy val sqlCompiler = (project in file("sql-compiler"))
  .doPatchDependencies()
  .dependsOn(
    compiler % "compile->compile;test->test",
    sqlParser % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      kiama,
      postgresqlDeps,
      hikariCP,
      "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.41.3" % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.41.3" % Test
    )
  )

lazy val pythonCompiler = (project in file("python-compiler"))
  .doPatchDependencies()
  .dependsOn(
    compiler % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings,
    Compile / packageBin / packageOptions += Package.ManifestAttributes("Automatic-Module-Name" -> "raw.python.client"),
    libraryDependencies += "org.graalvm.polyglot" % "python" % "23.1.0" % Provided
  )
