import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import sbt.Keys._
import sbt._

import java.time.Year

import raw.build.Dependencies._
import raw.build.BuildSettings._

import java.io.IOException
import java.nio.file.{Paths, Files}
import java.nio.charset.StandardCharsets

import scala.sys.process._

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / sonatypeProfileName := "com.raw-labs"

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
    client,
    snapiParser,
    snapiFrontend,
    snapiTruffle,
    snapiClient,
    sqlParser,
    sqlClient
    )
  .settings(
    publish := (publish dependsOn(writeVersionToFile)).value,
    publishLocal := (publishLocal dependsOn(writeVersionToFile)).value,
    publishSigned := (publishSigned dependsOn(writeVersionToFile)).value,
    publish / skip := true,
    publishSigned / skip  := true,
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

val generateSnapiParser = taskKey[Unit]("Generated antlr4 base SNAPI parser and lexer")

lazy val snapiParser = (project in file("snapi-parser"))
  .settings(
    commonSettings,
    commonCompileSettings,
    Compile / doc := { file("/dev/null") },
    compileOrder := CompileOrder.JavaThenScala,
    libraryDependencies ++= Seq(
      antlr4Runtime
    ),
    generateSnapiParser := {
      // List of output paths
      val basePath: String = s"${baseDirectory.value}/src/main/java"
      val parsers = List(
        (s"${basePath}/raw/compiler/rql2/generated", "raw.compiler.rql2.generated", s"$basePath/raw/snapi/grammar", "Snapi"),
      )
      def deleteRecursively(file: File): Unit = {
        if (file.isDirectory) {
          file.listFiles.foreach(deleteRecursively)
        }
        if (file.exists && !file.delete()) {
          throw new IOException(s"Failed to delete ${file.getAbsolutePath}")
        }
      }
      val s: TaskStreams = streams.value
      parsers.foreach(parser => {
        val outputPath = parser._1
        val file = new File(outputPath)
        if (file.exists()) {
          deleteRecursively(file)
        }
        val packageName: String = parser._2
        val jarName = "antlr-4.12.0-complete.jar"
        val command: String = s"java -jar $basePath/antlr4/$jarName -visitor -package $packageName -o $outputPath"
        val output = new StringBuilder
        val logger = ProcessLogger(
          (o: String) => output.append(o + "\n"), // for standard output
          (e: String) => output.append(e + "\n") // for standard error
        )
        val grammarPath = parser._3
        val grammarName = parser._4
        val lexerResult = s"$command  $grammarPath/${grammarName}Lexer.g4".!(logger)
        if (lexerResult == 0) {
          s.log.info("Lexer code generated successfully")
        } else {
          s.log.error("Lexer code generation failed with exit code " + lexerResult)
          s.log.error("Output:\n" + output.toString)
        }
        val parserResult = s"$command $grammarPath/${grammarName}Parser.g4".!(logger)
        if (parserResult == 0) {
          s.log.info("Parser code generated successfully")
        } else {
          s.log.error("Parser code generation failed with exit code " + lexerResult)
          s.log.error("Output:\n" + output.toString)
        }
      })
    },
    Compile / compile := (Compile / compile).dependsOn(generateSnapiParser).value,
    Compile / doc := (Compile / doc).dependsOn(generateSnapiParser).value,
    Test / compile := (Test / compile).dependsOn(generateSnapiParser).value,
    publish := (publish dependsOn(generateSnapiParser)).value,
    publishLocal := (publishLocal dependsOn(generateSnapiParser)).value,
    publishSigned := (publishSigned dependsOn(generateSnapiParser)).value
  )

lazy val snapiFrontend = (project in file("snapi-frontend"))
  .dependsOn(
    utils % "compile->compile;test->test",
    client % "compile->compile;test->test",
    snapiParser % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    nonStrictScalaCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
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
    libraryDependencies ++= truffleCompiler ++ scalaCompiler,
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
    publish := (publish dependsOn(runJavaAnnotationProcessor)).value,
    publishLocal := (publishLocal dependsOn(runJavaAnnotationProcessor)).value,
    publishSigned := (publishSigned dependsOn(runJavaAnnotationProcessor)).value,
  )


lazy val snapiClient = (project in file("snapi-client"))
  .dependsOn(
    client % "compile->compile;test->test",
    snapiFrontend % "compile->compile;test->test",
    snapiTruffle % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    snapiClientCompileSettings,
    testSettings
  )

val generateSqlParser = taskKey[Unit]("Generated antlr4 base SQL parser and lexer")

lazy val sqlParser = (project in file("sql-parser"))
  .settings(
    commonSettings,
    commonCompileSettings,
    Compile / doc := { file("/dev/null") },
    compileOrder := CompileOrder.JavaThenScala,
    libraryDependencies ++= Seq(
      antlr4Runtime
    ),
    generateSqlParser := {
      // List of output paths
      val basePath: String = s"${baseDirectory.value}/src/main/java"
      val parsers = List(
        (s"${basePath}/raw/client/sql/generated", "raw.client.sql.generated", s"$basePath/raw/psql/grammar", "Psql"),
      )
      def deleteRecursively(file: File): Unit = {
        if (file.isDirectory) {
          file.listFiles.foreach(deleteRecursively)
        }
        if (file.exists && !file.delete()) {
          throw new IOException(s"Failed to delete ${file.getAbsolutePath}")
        }
      }
      val s: TaskStreams = streams.value
      parsers.foreach(parser => {
        val outputPath = parser._1
        val file = new File(outputPath)
        if (file.exists()) {
          deleteRecursively(file)
        }
        val packageName: String = parser._2
        val jarName = "antlr-4.12.0-complete.jar"
        val command: String = s"java -jar $basePath/antlr4/$jarName -visitor -package $packageName -o $outputPath"
        val output = new StringBuilder
        val logger = ProcessLogger(
          (o: String) => output.append(o + "\n"), // for standard output
          (e: String) => output.append(e + "\n") // for standard error
        )
        val grammarPath = parser._3
        val grammarName = parser._4
        val lexerResult = s"$command  $grammarPath/${grammarName}Lexer.g4".!(logger)
        if (lexerResult == 0) {
          s.log.info("Lexer code generated successfully")
        } else {
          s.log.error("Lexer code generation failed with exit code " + lexerResult)
          s.log.error("Output:\n" + output.toString)
        }
        val parserResult = s"$command $grammarPath/${grammarName}Parser.g4".!(logger)
        if (parserResult == 0) {
          s.log.info("Parser code generated successfully")
        } else {
          s.log.error("Parser code generation failed with exit code " + lexerResult)
          s.log.error("Output:\n" + output.toString)
        }
      })
    },
    Compile / compile := (Compile / compile).dependsOn(generateSqlParser).value,
    Compile / doc := (Compile / doc).dependsOn(generateSqlParser).value,
    Test / compile := (Test / compile).dependsOn(generateSqlParser).value,
    publish := (publish dependsOn(generateSqlParser)).value,
    publishLocal := (publishLocal dependsOn(generateSqlParser)).value,
    publishSigned := (publishSigned dependsOn(generateSqlParser)).value
  )

lazy val sqlClient = (project in file("sql-client"))
  .dependsOn(
    client % "compile->compile;test->test",
    snapiFrontend % "compile->compile;test->test",
    sqlParser % "compile->compile;test->test"
  )
  .settings(
    commonSettings,
    snapiClientCompileSettings,
    testSettings,
    libraryDependencies ++= Seq(
      kiama,
      postgresqlDeps,
      hikariCP
    )
  )
