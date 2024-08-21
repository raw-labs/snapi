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
ThisBuild / resolvers += "Github RAW main repo" at "https://maven.pkg.github.com/raw-labs/raw"

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
  .aggregate(
    compilerProtocol,
    utilsCore,
    utilsSources,
    compilerApi,
    compilerSnapiParser,
    compilerSnapiFrontend,
    compilerSnapiTruffle,
    compilerSnapi,
    compilerSqlParser,
    compilerSql
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

lazy val utilsCore = (project in file("utils-core"))
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

lazy val utilsSources = (project in file("utils-sources"))
  .dependsOn(
    utilsCore % "compile->compile;test->test"
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

lazy val rawProtocol = (project in file("raw-protocol"))
  .enablePlugins(ProtobufPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    testSettings,
    ProtobufConfig / version := "3.25.4",
    // Include the protobuf files in the JAR
    Compile / unmanagedResourceDirectories += (ProtobufConfig / sourceDirectory).value
  )

lazy val compilerProtocol = (project in file("compiler-protocol"))
  .enablePlugins(ProtobufPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    testSettings,
    ProtobufConfig / version := "3.25.4",
    // Include the protobuf files in the JAR
    Compile / unmanagedResourceDirectories += (ProtobufConfig / sourceDirectory).value
  )

lazy val compilerApi = (project in file("compiler-api"))
  .dependsOn(
    utilsCore % "compile->compile;test->test",
    compilerProtocol % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    scalaCompileSettings,
    testSettings,
    libraryDependencies += trufflePolyglot
  )

lazy val compilerSnapiParser = (project in file("compiler-snapi-parser"))
  .enablePlugins(GenParserPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    javaSrcBasePath := s"${baseDirectory.value}/src/main/java",
    parserDefinitions := List(
      (
        s"${javaSrcBasePath.value}/com/rawlabs/compiler/snapi/generated",
        "com.rawlabs.compiler.snapi.generated",
        s"${javaSrcBasePath.value}/com/rawlabs/compiler/snapi/grammar",
        "Snapi"
      )
    ),
    Compile / doc := { file("/dev/null") },
    compileOrder := CompileOrder.JavaThenScala,
    libraryDependencies ++= Seq(
      antlr4Runtime
    )
  )

lazy val compilerSnapiFrontend = (project in file("compiler-snapi-frontend"))
  .dependsOn(
    utilsCore % "compile->compile;test->test",
    compilerApi % "compile->compile;test->test",
    utilsSources % "compile->compile;test->test",
    compilerSnapiParser % "compile->compile;test->test"
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

lazy val compilerSnapiTruffle = (project in file("compiler-snapi-truffle"))
  .dependsOn(
    utilsCore % "compile->compile;test->test",
    compilerSnapiFrontend % "compile->compile;test->test"
  )
  .enablePlugins(JavaAnnotationProcessorPlugin)
  .settings(
    commonSettings,
    snapiTruffleCompileSettings,
    testSettings,
    libraryDependencies ++= truffleCompiler,
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

lazy val compilerSnapi = (project in file("compiler-snapi"))
  .dependsOn(
    compilerApi % "compile->compile;test->test",
    compilerSnapiFrontend % "compile->compile;test->test",
    compilerSnapiTruffle % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings
  )

lazy val compilerSqlParser = (project in file("compiler-sql-parser"))
  .enablePlugins(GenParserPlugin)
  .settings(
    commonSettings,
    commonCompileSettings,
    javaSrcBasePath := s"${baseDirectory.value}/src/main/java",
    parserDefinitions := List(
      (
        s"${javaSrcBasePath.value}/com/rawlabs/compiler/sql/generated",
        "com.rawlabs.compiler.sql.generated",
        s"${javaSrcBasePath.value}/com/rawlabs/compiler/sql/grammar",
        "Psql"
      )
    ),
    Compile / doc := { file("/dev/null") },
    compileOrder := CompileOrder.JavaThenScala,
    libraryDependencies ++= Seq(
      antlr4Runtime
    )
  )

lazy val compilerSql = (project in file("compiler-sql"))
  .dependsOn(
    compilerApi % "compile->compile;test->test",
    compilerSqlParser % "compile->compile;test->test"
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

lazy val compilerPython = (project in file("compiler-python"))
  .dependsOn(
    compilerApi % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    missingInterpolatorCompileSettings,
    testSettings,
    Compile / packageBin / packageOptions += Package.ManifestAttributes("Automatic-Module-Name" -> "raw.python.client"),
    libraryDependencies += "org.graalvm.polyglot" % "python" % "23.1.0" % Provided
  )
