import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._

import sbt.Keys._
import sbt._

import java.time.Year

import Dependencies._

import scala.sys.process._

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

sonatypeProfileName := "com.raw-labs"

val licenseHeader = s"""Copyright ${Year.now.getValue} RAW Labs S.A.

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

name := "raw-snapi-frontend"

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
  "-Xlint:-stars-align,_",
  "-Ywarn-dead-code",
  // Fix for false warning of unused implicit arguments in traits/interfaces.
  "-Ywarn-macros:after",
  "-Ypatmat-exhaust-depth",
  "160"
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
Compile / packageSrc / publishArtifact := true
// When doing publishLocal, also publish to the local maven repository and generate the version number file.
publishLocal := (publishLocal dependsOn Def.sequential(outputVersion, publishM2)).value

// Dependencies
libraryDependencies ++= Seq(
  rawClient % "compile->compile;test->test",
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
  kryo,
  // We depend directly on the Truffle DSL processor to use their Antlr4.
  // If we'd use ours, they would conflict as Truffle DSL package defines the org.antlr4 package.
  "org.graalvm.truffle" % "truffle-dsl-processor" % "23.1.0"
) ++
  poiDeps

val generateParser = taskKey[Unit]("Generated antlr4 base parser and lexer")

generateParser := {
  val basePath: String = s"${baseDirectory.value}/src/main/resources/antlr4"

  val outputPath: String = s"${baseDirectory.value}/src/main/java/raw/compiler/rql2/generated"

  val packageName: String = "raw.compiler.rql2.generated"

  val jarName = "antlr-4.12.0-complete.jar"

  val command: String =
    s"java -jar $basePath/$jarName -visitor -package $packageName -o $outputPath"

  val s: TaskStreams = streams.value
  val output = new StringBuilder
  val logger = ProcessLogger(
    (o: String) => output.append(o + "\n"), // for standard output
    (e: String) => output.append(e + "\n") // for standard error
  )

  val lexerResult = s"$command $basePath/SnapiLexer.g4".!(logger)
  if (lexerResult == 0) {
    s.log.info("Lexer code generated successfully")
  } else {
    s.log.error("Lexer code generation failed with exit code " + lexerResult)
    s.log.error("Output:\n" + output.toString)
  }

  val parserResult = s"$command $basePath/SnapiParser.g4".!(logger)
  if (parserResult == 0) {
    s.log.info("Parser code generated successfully")
  } else {
    s.log.error("Parser code generation failed with exit code " + lexerResult)
    s.log.error("Output:\n" + output.toString)
  }
}

Compile / packageBin / packageOptions += Package.ManifestAttributes("Automatic-Module-Name" -> "raw.snapi.frontend")

Compile / compile := (Compile / compile).dependsOn(generateParser).value

publishLocal := (publishLocal dependsOn generateParser).value
