import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._

import sbt.Keys._
import sbt._

import java.time.Year

import Dependencies._

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

name := "raw-snapi-client"

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
  rawSnapiFrontend % "compile->compile;test->test",
  rawSnapiTruffle % "test->test"
)
