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

// Read version to use
import java.util.Properties
import java.io.FileInputStream
version := {
  val properties = new Properties()
  val fs = new FileInputStream("../version.properties")
  try {
    properties.load(fs)
    properties.getProperty("language.version")
  } finally {
    fs.close()
  }
}

headerLicense := Some(HeaderLicense.Custom(licenseHeader))

homepage := Some(url("https://www.raw-labs.com/"))

organization := "com.raw-labs"

organizationName := "RAW Labs SA"

organizationHomepage := Some(url("https://www.raw-labs.com/"))

name := "raw-language"

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
  "-source",
  "11",
  "-target",
  "11"
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

// Add all the classpath to the module path.
Compile / javacOptions ++= Seq(
  "--module-path",
  (Compile / dependencyClasspath).value.files.absString
)

// The tests are run in a forked JVM.
// System properties given to sbt are not automatically passed to the forked VM
// Here we copy any "raw." system properties to the java options passed to the forked JVMs.
Test / fork := true

Test / parallelExecution := true

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
  "-XX:+CrashOnOutOfMemoryError",
  // Truffle test settings.
//  "-Dpolyglotimpl.Inlining=false",
  "-Dpolyglotimpl.CompileImmediately=true",
  "-Dpolyglotimpl.AllowExperimentalOptions=true",
  "-Dgraal.Dump=Truffle:2",
  "-Dgraal.DumpPath=/tmp/graal_dumps",
  "-Dgraal.PrintGraph=Network",
//  "-Dpolyglotimpl.CompilationFailureAction=Throw",
//  "-Dpolyglotimpl.TreatPerformanceWarningsAsErrors=false",
//  "-Dpolyglotimpl.CompilationExceptionsAreFatal=true",
  "-Dpolyglotimpl.DisableClassPathIsolation=true",
  "-Dpolyglotimpl.BackgroundCompilation=false",
  "-Dpolyglotimpl.TraceCompilation=true",
  "-Dpolyglotimpl.TraceCompilationDetails=true",
  "-Dpolyglotimpl.TraceInlining=true"
)

// Group tests by the scala package of the test
Test / testGrouping := {
  // This is called for each sbt project
  val tests = (Test / definedTests).value
  val projectName = name.value
  var javaOptionsTest = (Test / javaOptions).value
  val logsRootDir = Paths.get(sys.env.getOrElse("SBT_FORK_OUTPUT_DIR", "target/test-results"))
  val executorLogsDir = logsRootDir.resolve("executor-logs")
  val heapDumpsRoot = logsRootDir.resolve("heap-dumps")
  // Generating a random directory in order the case when tests run in parallel
  for (file <- Seq(executorLogsDir, heapDumpsRoot)) {
    Files.createDirectories(file)
  }
  javaOptionsTest = javaOptionsTest ++ Seq(
    "-XX:+HeapDumpOnOutOfMemoryError",
    s"-XX:HeapDumpPath=$heapDumpsRoot"
  )

  var outputLogsDir: Option[java.nio.file.Path] = None
  val currentLogFileName: (String, String) => String = (a, b) => s"$a-$b.log"

  def resolvePath(
      executorLogsDir: Path,
      logFileName: String
  ): Path = {
    executorLogsDir.resolve(logFileName)
  }

  val testExecutionLogGroups = resolvePath(executorLogsDir, "test-groups.list")
  val testActor = "executor"

  // This is run for each sbt project
  tests
    .grouped(10)
    .zipWithIndex
    .map {
      case (testGroup, i) => {
        val testGroupName = s"$projectName-testgroup$i"
        val names = testGroup.map(td => td.name).mkString(s"Group: $testGroupName\n", "\n", "\n\n")

        Files.write(testExecutionLogGroups, names.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE)

        val outputFile = resolvePath(
          executorLogsDir,
          currentLogFileName.apply(testActor, testGroupName)
        )
        println(s"Writing output of test group $testGroupName to $outputFile")
        Files.deleteIfExists(outputFile)
        val options = ForkOptions()
          .withRunJVMOptions(javaOptionsTest.to)
          .withOutputStrategy(Some(CustomOutput(new BufferedOutputStream(Files.newOutputStream(outputFile)))))
        Group(testGroupName, testGroup, SubProcess(options))
      }
    }
    .toSeq
}

// Enable running multiple test VMs in parallel.
concurrentRestrictions in Global := Seq(
  Tags.limit(
    Tags.ForkedTestGroup,
    sys.env
      .get("UNIT_TESTS_PARALLELISM")
      .map(_.toInt)
      .getOrElse(Math.max(java.lang.Runtime.getRuntime.availableProcessors - 2, 1))
  ),
  Tags.limit(Tags.CPU, java.lang.Runtime.getRuntime.availableProcessors),
  Tags.limit(Tags.Network, 10),
  Tags.limit(
    Tags.Test,
    sys.env
      .get("UNIT_TESTS_PARALLELISM")
      .map(_.toInt)
      .getOrElse(Math.max(java.lang.Runtime.getRuntime.availableProcessors - 2, 1))
  ),
  Tags.limitAll(
    sys.env.get("SBT_MAX_PARALLELISM").map(_.toInt).getOrElse(java.lang.Runtime.getRuntime.availableProcessors)
  ),
  Tags.limit(Tags.Publish, 1)
)

// Add dependency resolvers
resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("releases")

// Publish settings
Test / publishArtifact := true
// When doing publishLocal, also publish to the local maven repository.
publishLocal := (publishLocal dependsOn publishM2).value

// Dependencies
libraryDependencies ++= Seq(
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
  truffleDeps
