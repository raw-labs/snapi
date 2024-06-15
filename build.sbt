import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import sbt.Keys._
import sbt._

import java.time.Year

import raw.build.Dependencies._
import raw.build.BuildSettings._

import java.io.IOException
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets

import scala.sys.process._

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / sonatypeProfileName := "com.raw-labs"
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "raw-labs",
  sys.env.getOrElse("GITHUB_TOKEN", "")
)

ThisBuild/ resolvers += "Github RAW main repo" at "https://maven.pkg.github.com/raw-labs/raw"


val writeVersionToFile = taskKey[Unit]("Writes the project version to a file at the root.")

writeVersionToFile := {
  val file = (ThisBuild / baseDirectory).value / "version"
  val versionString = (ThisBuild / version).value
  IO.write(file, versionString)
  streams.value.log.info(s"Project version $versionString written to ${file.getPath}")
}

lazy val root = (project in file("."))
  .aggregate(
    utils,
    sources,
    client,
    snapiParser,
    snapiFrontend,
    snapiTruffle,
    snapiClient,
    sqlParser,
    sqlClient
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

lazy val utils = (project in file("utils"))
  .settings(
    commonSettings,
    scalaCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      scalaLogging,
      logbackClassic,
      guava,
      scalaJava8Compat,
      typesafeConfig,
      loki4jAppender,
      commonsIO,
      commonsText,
      scalatest % Test
    ) ++
      slf4j ++
      jacksonDeps
  )

lazy val client = (project in file("client"))
  .dependsOn(
    utils % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    scalaCompileSettings,
    testSettings,
    libraryDependencies += trufflePolyglot
  )

lazy val sources = (project in file("sources"))
  .dependsOn(
    client % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    nonStrictScalaCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      apacheHttpClient,
      jwtApi,
      jwtImpl,
      jwtCore,
      springCore,
      dropboxSDK,
      aws,
      postgresqlDeps,
      mysqlDeps,
      mssqlDeps,
      snowflakeDeps,
      oracleDeps,
      teradataDeps
    )
  )

lazy val snapiParser = (project in file("snapi-parser"))
  .enablePlugins(GenParserPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    javaSrcBasePath := s"${baseDirectory.value}/src/main/java",
    parserDefinitions := List(
      (s"${javaSrcBasePath.value}/raw/compiler/rql2/generated",
        "raw.compiler.rql2.generated",
        s"${javaSrcBasePath.value}/raw/snapi/grammar",
        "Snapi")
    ),
    Compile / doc := { file("/dev/null") },
    compileOrder := CompileOrder.JavaThenScala,
    libraryDependencies ++= Seq(
      antlr4Runtime
    ),
  )

lazy val snapiFrontend = (project in file("snapi-frontend"))
  .dependsOn(
    utils % "compile->compile;test->test",
    client % "compile->compile;test->test",
    sources % "compile->compile;test->test",
    snapiParser % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    nonStrictScalaCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
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
  .dependsOn(
    utils % "compile->compile;test->test",
    snapiFrontend % "compile->compile;test->test"
  )
  .enablePlugins(JavaAnnotationProcessorPlugin)
  .settings(
    commonSettings,
    snapiTruffleCompileSettings,
    testSettings,
    libraryDependencies ++= truffleCompile,
    calculateClasspath := {
      val dependencyFiles = (Compile / dependencyClasspath).value.files
      val unmanagedFiles = (Compile / unmanagedClasspath).value.files
      val classesDir = (Compile / classDirectory).value
      dependencyFiles ++ unmanagedFiles ++ Seq(classesDir)
    },
    runJavaAnnotationProcessor := {
      println("Running Java annotation processor")

      val annotationProcessorJar = baseDirectory.value / "truffle-dsl-processor-23.1.0.jar"

      val javaSources = baseDirectory.value / "src" / "main" / "java"
      val targetDir = baseDirectory.value / "target" / "java-processed-sources"

      val projectClasspath = calculateClasspath.value.mkString(":")

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
    },
    Compile / compile := (Compile / compile).dependsOn(runJavaAnnotationProcessor).value,
    Compile / doc := (Compile / doc).dependsOn(runJavaAnnotationProcessor).value,
    Test / compile := (Test / compile).dependsOn(runJavaAnnotationProcessor).value,
    publish := (publish dependsOn runJavaAnnotationProcessor).value,
    publishLocal := (publishLocal dependsOn runJavaAnnotationProcessor).value,
    publishSigned := (publishSigned dependsOn runJavaAnnotationProcessor).value
  )

lazy val snapiClient = (project in file("snapi-client"))
  .dependsOn(
    client % "compile->compile;test->test",
    snapiFrontend % "compile->compile;test->test",
    snapiTruffle % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings
  )


lazy val sqlParser = (project in file("sql-parser"))
  .enablePlugins(GenParserPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    javaSrcBasePath := s"${baseDirectory.value}/src/main/java",
    parserDefinitions := List(
      (s"${javaSrcBasePath.value}/raw/client/sql/generated",
      "raw.client.sql.generated",
      s"${javaSrcBasePath.value}/raw/psql/grammar",
      "Psql")
    ),
    Compile / doc := { file("/dev/null") },
    compileOrder := CompileOrder.JavaThenScala,
    libraryDependencies ++= Seq(
      antlr4Runtime,
    )
  )

lazy val sqlClient = (project in file("sql-client"))
  .dependsOn(
    client % "compile->compile;test->test",
    snapiFrontend % "compile->compile;test->test",
    sqlParser % "compile->compile;test->test",
    sources % "compile->compile;test->test",
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      kiama,
      postgresqlDeps,
      hikariCP
    )
  )

lazy val pythonClient = (project in file("python-client"))
  .dependsOn(
    client % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings,
    Compile / packageBin / packageOptions += Package.ManifestAttributes("Automatic-Module-Name" -> "raw.python.client"),
    libraryDependencies += "org.graalvm.polyglot" % "python" % "23.1.0" % Provided
  )
