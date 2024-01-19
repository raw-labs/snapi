import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.*
import sbt.Keys.*
import sbt.*

import Dependencies.*

import scala.sys.process.Process

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

Compile / unmanagedSourceDirectories += baseDirectory.value / "target" / "java-processed-sources"

Compile / unmanagedResourceDirectories += baseDirectory.value / "target" / "java-processed-sources" / "META-INF"

Compile / resourceDirectories += baseDirectory.value / "target" / "java-processed-sources" / "META-INF"

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

val calculateClasspath = taskKey[Seq[File]]("Calculate the full classpath")

calculateClasspath := {
  val dependencyFiles = (Compile / dependencyClasspath).value.files
  val unmanagedFiles = (Compile / unmanagedClasspath).value.files
  val classesDir = (Compile / classDirectory).value

  dependencyFiles ++ unmanagedFiles ++ Seq(classesDir)
}

val runJavaAnnotationProcessor = taskKey[Unit]("Runs the Java annotation processor")

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
}

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
  rawSnapiFrontend % "compile->compile;test->test"
) ++ truffleCompiler ++ scalaCompiler // Add the scala compiler explicitly to the classpath so that above we copy it to the modulepath.

// Output version to a file on compile
val initializeVersion = taskKey[Unit]("Initialize the version number early")

initializeVersion := {
  println("Initializing version" + version.value)
}

val outputVersion = taskKey[Unit]("Outputs the version to a file")

outputVersion := {
  val versionFile = baseDirectory.value / "version"
  if (!versionFile.exists()) {
    IO.touch(versionFile)
  }
  IO.write(versionFile, version.value)
}

Compile / compile := ((Compile / compile) dependsOn outputVersion).value
