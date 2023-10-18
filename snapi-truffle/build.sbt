import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._

import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt._

import java.io._
import java.time.Year

import Dependencies._

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

name := "raw-snapi-truffle"

developers := List(Developer("raw-labs", "RAW Labs", "engineering@raw-labs.com", url("https://github.com/raw-labs")))

licenses := List(
  "Business Source License 1.1" -> new URI(
    "https://raw.githubusercontent.com/raw-labs/snapi/main/licenses/BSL.txt"
  ).toURL
)

startYear := Some(2023)

headerLicense := Some(HeaderLicense.Custom(licenseHeader))

headerSources / excludeFilter := HiddenFileFilter

scalaVersion := Dependencies.scalacVersion

javacOptions ++= Seq(
  "-source", "21",
  "-target", "21"
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
  "-Xmx4G",
  "-XX:+CrashOnOutOfMemoryError"
)

// Add dependency resolvers
resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("releases")

// Publish settings
Test / publishArtifact := true
Compile / packageSrc / publishArtifact := true
// When doing publishLocal, also publish to the local maven repository.
publishLocal := (publishLocal dependsOn publishM2).value

// Dependencies
libraryDependencies ++= Seq(
  rawUtils % "compile->compile;test->test",
  rawSnapiFrontend % "compile->compile;test->test",
  scalaLogging,
  logbackClassic,
  guava,
  scalaJava8Compat,
  typesafeConfig,
  loki4jAppender,
  commonsIO,
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
  scalatest % Test
) ++
  slf4j ++
  jacksonDeps ++
  poiDeps ++
  scalaCompiler ++
  truffleCompiler

// auto output version to a file on compile
lazy val outputVersion = taskKey[Unit]("Outputs the version to a file")

outputVersion := {
  val versionFile = baseDirectory.value / "version"
  if (!versionFile.exists()) {
    IO.touch(versionFile)
  }
  IO.write(versionFile, version.value)
}

Compile / compile := ((Compile / compile) dependsOn outputVersion).value
