import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.*
import sbt.Keys.*
import sbt.*

import java.time.Year
import Dependencies.*

import java.io.IOException
import scala.sys.process.*

import com.jsuereth.sbtpgp.PgpKeys.{publishSigned}

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

sonatypeProfileName := "com.raw-labs"

val licenseHeader = """Copyright 2023 RAW Labs S.A.

Use of this software is governed by the Business Source License
included in the file licenses/BSL.txt.

As of the Change Date specified in that file, in accordance with
the Business Source License, use of this software will be governed
by the Apache License, Version 2.0, included in the file
licenses/APL.txt."""

headerLicense := Some(HeaderLicense.Custom(licenseHeader))

homepage := Some(url("https://www.raw-labs.com/"))

organization := "com.raw-labs"

organizationName := "RAW Labs SA"

organizationHomepage := Some(url("https://www.raw-labs.com/"))

name := "raw-sql-parser"

developers := List(Developer("raw-labs", "RAW Labs", "engineering@raw-labs.com", url("https://github.com/raw-labs")))

licenses := List(
  "Business Source License 1.1" -> new URI(
    "https://raw.githubusercontent.com/raw-labs/snapi/main/licenses/BSL.txt"
  ).toURL
)

startYear := Some(2023)

headerLicense := Some(HeaderLicense.Custom(licenseHeader))

headerSources / excludeFilter := HiddenFileFilter

javacOptions ++= Seq(
  "-source",
  "21",
  "-target",
  "21"
)

// Use cached resolution of dependencies
updateOptions := updateOptions.in(Global).value.withCachedResolution(true)

// Needed for JPMS to work.
compileOrder := CompileOrder.JavaThenScala

// Doc generation breaks with Java files
Compile / doc / sources := {
  (Compile / doc / sources).value.filterNot(_.getName.endsWith(".java"))
}
Test / doc / sources := {
  (Compile / doc / sources).value.filterNot(_.getName.endsWith(".java"))
}

// Skipping javadoc generation for antlr4 broken links
Compile / doc := { file("/dev/null") } // for Unix-like systems

// Add all the classpath to the module path.
Compile / javacOptions ++= Seq(
  "--module-path",
  (Compile / dependencyClasspath).value.files.absString
)

// The tests are run in a forked JVM.
// System properties given to sbt are not automatically passed to the forked VM
// Here we copy any "raw." system properties to the java options passed to the forked JVMs.
Test / fork := true

Test / javaOptions ++= {
  import scala.collection.JavaConverters._
  val props = System.getProperties
  props
    .stringPropertyNames()
    .asScala
    .filter(p => p.startsWith("raw."))
    .map(key => s"-D$key=${props.getProperty(key)}")
    .to
}

Test / javaOptions ++= Seq(
  // Increasing stack size for Kiama chain/rewrites and codegen.
  "-Xss64m",
  // Enable assertions.
//  "-ea",
  // Limit overall memory and force crashing hard and early.
  // Useful for debugging memleaks.
  "-Xmx8G",
  "-XX:+CrashOnOutOfMemoryError"
)

// Add dependency resolvers
resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("releases")

// Output version to a file
val outputVersion = taskKey[Unit]("Outputs the version to a file")
outputVersion := {
  val versionFile = baseDirectory.value / "version"
  if (!versionFile.exists()) {
    IO.touch(versionFile)
  }
  IO.write(versionFile, (ThisBuild / version).value)
}

// Publish settings
Test / publishArtifact := true
// Useful for debugging
Test / packageSrc / publishArtifact := true

Compile / packageSrc / publishArtifact := true

// Dependencies
libraryDependencies ++= Seq(
  "org.antlr" % "antlr4-runtime" % "4.12.0"
)

val generateParser = taskKey[Unit]("Generated antlr4 base parser and lexer")

generateParser := {

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
}

Compile / compile := (Compile / compile).dependsOn(generateParser).value

publishLocal := (publishLocal dependsOn Def.sequential(outputVersion, generateParser, publishM2)).value
publish := (publish dependsOn Def.sequential(outputVersion, generateParser)).value
publishSigned := (publishSigned dependsOn Def.sequential(outputVersion, generateParser)).value
