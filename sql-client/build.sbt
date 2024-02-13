import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.*
import sbt.Keys.*
import sbt.*

import java.time.Year
import Dependencies.*

import java.io.IOException
import scala.sys.process.ProcessLogger

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

name := "raw-sql-client"

developers := List(Developer("raw-labs", "RAW Labs", "engineering@raw-labs.com", url("https://github.com/raw-labs")))

licenses := List(
  "Business Source License 1.1" -> new URI(
    "https://raw.githubusercontent.com/raw-labs/snapi/main/licenses/BSL.txt"
  ).toURL
)

startYear := Some(2023)

headerLicense := Some(HeaderLicense.Custom(licenseHeader))

headerSources / excludeFilter := HiddenFileFilter

scalaVersion := "2.12.18"

javacOptions ++= Seq(
  "-source",
  "21",
  "-target",
  "21"
)

scalacOptions ++= Seq(
  "-feature",
  "-unchecked",
  // When compiling in encrypted drives in Linux, the max size of a name is reduced to around 140.
  "-Xmax-classfile-name",
  "140",
  "-deprecation",
  "-Xlint:-stars-align,-missing-interpolator,_",
  "-Ywarn-dead-code",
  // Fix for false warning of unused implicit arguments in traits/interfaces.
  "-Ywarn-macros:after",
  "-Ypatmat-exhaust-depth",
  "160",
  // Warnings as errors.
  "-Xfatal-warnings"
)

// Use cached resolution of dependencies
updateOptions := updateOptions.in(Global).value.withCachedResolution(true)

compileOrder := CompileOrder.ScalaThenJava

// Doc generation breaks with Java files
Compile / doc / sources := {
  (Compile / doc / sources).value.filterNot(_.getName.endsWith(".java"))
}
Test / doc / sources := {
  (Compile / doc / sources).value.filterNot(_.getName.endsWith(".java"))
}

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
  "-XX:+CrashOnOutOfMemoryError",
  "-Dpolyglotimpl.CompilationFailureAction=Throw"
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
Compile / packageSrc / publishArtifact := true
// When doing publishLocal, also publish to the local maven repository and generate the version number file.
publishLocal := (publishLocal dependsOn Def.sequential(outputVersion, publishM2)).value

// Dependencies
libraryDependencies ++= Seq(
  rawClient % "compile->compile;test->test",
  postgresqlDeps,
  hikariCP,
  antlr
)


def runAntlr(antlrJar: String, inputFile: String, outputPath: String, packageName: String, s: TaskStreams): Unit = {
  val command: String = s"java -jar $antlrJar -visitor -package $packageName -o $outputPath"

  val output = new StringBuilder
  val logger = ProcessLogger(
    (o: String) => output.append(o + "\n"), // for standard output
    (e: String) => output.append(e + "\n") // for standard error
  )

  val result = s"$command  $inputFile".!(logger)
  if (result == 0) {
    s.log.info(s"Code generated successfully for file $inputFile")
  } else {
    s.log.error(s"ERROR: Code generation failed for file $inputFile exit code $result")
    s.log.error("Output:\n" + output.toString)
  }

}

val generateParser = taskKey[Unit]("Generated antlr4 base parser and lexer")

generateParser := {

  def deleteRecursively(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles.foreach(deleteRecursively)
    }
    if (file.exists && !file.delete()) {
      throw new IOException(s"Failed to delete ${file.getAbsolutePath}")
    }
  }

  val basePath: String = s"${baseDirectory.value}/src/main/java"
  val outputPath: String = s"$basePath/raw/client/sql/generated"

  val file = new File(outputPath)
  if (file.exists()) {
    deleteRecursively(file)
  }

  val packageName: String = "raw.client.sql.generated"

  val jarName = s" $basePath/../snapi-parser/antlr4/antlr-4.12.0-complete.jar"

  val s: TaskStreams = streams.value
  val grammarPath = s"$basePath/raw/client/sql/grammar"
  runAntlr(jarName, s"$grammarPath/SQLLexer.g4", outputPath, packageName, s)
  runAntlr(jarName, s"$grammarPath/SQLParser.g4", outputPath, packageName, s)

}

Compile / packageBin / packageOptions += Package.ManifestAttributes("Automatic-Module-Name" -> "raw.sql.client")
