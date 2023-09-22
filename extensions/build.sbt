import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.nio.file.attribute.PosixFilePermission
import collection.JavaConverters._

import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._

import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt._

import java.io._
import java.util.jar._
import java.time.Year

import Dependencies._

//publish / skip := true // don't publish the root project

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

val scalacOptSettings = Seq(
  // See scalac -opt:help for possible values.
)

val truffleExports = Seq(
//  "--add-exports",
//  "java.base/jdk.internal.module=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.sdk/org.graalvm.polyglot=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.nodes=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.frame=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.source=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.object=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.library=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.dsl=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.instrumentation=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.exception=ALL-UNNAMED",
//  "--add-exports",
//  "org.graalvm.truffle/com.oracle.truffle.api.interop=ALL-UNNAMED"
)

headerLicense := Some(HeaderLicense.Custom(licenseHeader))
// We keep Kiama's source code in the repo, and do not override their copyright notice.
val kiamaSrc: sbt.FileFilter = (pathname: File) => pathname.getPath.contains("inkytonik")

homepage := Some(url("https://www.raw-labs.com/"))
organization := "com.raw-labs"
organizationName := "RAW Labs SA"
organizationHomepage := Some(url("https://www.raw-labs.com/"))
name := "raw-compiler-extensions"
developers := List(Developer("raw-labs", "RAW Labs", "engineering@raw-labs.com", url("https://github.com/raw-labs")))
licenses := List(
  "Business Source License 1.1" -> new URL("https://raw.githubusercontent.com/raw-labs/snapi/main/licenses/BSL.txt")
)
startYear := Some(2023)
headerLicense := Some(HeaderLicense.Custom(licenseHeader))
headerSources / excludeFilter := HiddenFileFilter || kiamaSrc
scalaVersion := Dependencies.scalacVersion
javacOptions ++= Seq(
  "-source",
  "11",
  "-target",
  "11"
)
scalacOptions ++= scalacOptSettings ++ Seq(
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
Compile / doc / sources := Seq.empty // doc generation tries to parse module-info.java and fails
Compile / javacOptions ++= Seq(
  "--module-path",
  //(Compile / dependencyClasspath).value.filter(f => f.data.toString.contains("raw")).files.absString
  (Compile / dependencyClasspath).value.files.absString
//    (Compile / dependencyClasspath).value.filterNot(f => f.data.toString.contains("kiama")).files.absString
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
Test / javaOptions ++=
  truffleExports ++ Seq(
    // Increasing stack size for Kiama chain/rewrites and codegen.
    "-Xss64m",
    // Enable assertions.
//      "-ea",
    // Limit overall memory and force crashing hard and early.
    // Useful for debugging memleaks.
    "-Xmx4G",
    "-XX:+CrashOnOutOfMemoryError",
    // Required for running with Java 17
//      "--add-opens",
//      "java.base/java.lang=ALL-UNNAMED",
//      "--add-opens",
//      "java.base/java.util=ALL-UNNAMED",
//      "--add-opens",
//      "java.base/java.nio=ALL-UNNAMED",
//      "--add-opens",
//      "java.base/java.lang.invoke=ALL-UNNAMED",
//      "--add-opens",
//      "java.base/sun.nio.ch=ALL-UNNAMED",
//      "--add-opens",
//      "java.base/sun.util.calendar=ALL-UNNAMED",
    // Truffle test settings.
//      "-Dpolyglot.engine.Inlining=false",
    "-Dpolyglot.engine.CompileImmediately=true",
    "-Dpolyglot.engine.AllowExperimentalOptions=true",
    "-Dgraal.Dump=Truffle:2",
    "-Dgraal.DumpPath=/tmp/graal_dumps",
    "-Dgraal.PrintGraph=Network",
//      "-Dpolyglot.engine.CompilationFailureAction=Throw",
//      "-Dpolyglot.engine.TreatPerformanceWarningsAsErrors=false",
    "-Dpolyglot.engine.CompilationExceptionsAreFatal=true",
    "-Dgraalvm.locatorDisabled=true",
    "-Dpolyglot.engine.BackgroundCompilation=false",
    "-Dpolyglot.engine.TraceCompilation=true",
    "-Dpolyglot.engine.TraceCompilationDetails=true",
    "-Dpolyglot.engine.TraceInlining=true"
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
publish / skip := false
Test / publishArtifact := true

//lazy val strictBuildSettings = buildSettings ++ Seq(
//  scalacOptions ++= Seq(
//    "-Xfatal-warnings"
//  )
//)
//
//lazy val strictBuildExceptDeprecationsSettings = buildSettings ++ Seq(
//  scalacOptions ++= Seq(
//    "-Wconf:cat=deprecation:ws,any:e"
//  )
//)
//
lazy val temporaryDirectory: String = {
  // Create temp files on dast directories by default.
  val fastTmp = Paths.get("/mnt/jenkins/tmp")
  if (Files.isDirectory(fastTmp) && Files.isWritable(fastTmp)) {
    fastTmp.toAbsolutePath.toString
  } else {
    System.getProperty("java.io.tmpdir")
  }
}

Test / javaOptions ++= Seq(
  s"-Xlog:gc*,thread*:file=$temporaryDirectory/raw-compiler-rql2-truffle-tests-%t-%p.log", // GC logging.
  s"-Djava.io.tmpdir=$temporaryDirectory"
)

libraryDependencies ++= Seq(rawCompiler % "compile->compile;test->test")

//libraryDependencies ++= Seq(
//  scalaLogging,
//  logbackClassic,
//  guava,
//  scalaJava8Compat,
//  typesafeConfig,
//  loki4jAppender,
//  commonsIO,
//  commonsLang,
//  commonsText,
//  apacheHttpClient,
//  scalatest % Test
//) ++
//  slf4j ++
//  jacksonDeps ++
//  Seq(icuDeps, woodstox) ++ poiDeps ++ kiama ++ Seq(dropboxSDK, aws) ++ Seq(jwtApi, jwtImpl, jwtCore) ++ Seq(
//    postgresqlDeps
//  ) ++ Seq(mysqlDeps) ++ Seq(mssqlDeps) ++ Seq(snowflakeDeps) ++ scalaCompiler ++ Seq(commonsCodec) ++ Seq(
//    springCore
//  ) ++ truffleDeps ++ Seq(kryo)

//lazy val rawSourcesApi = (project in file("raw-sources"))
//  .dependsOn(
//    rawUtils % "compile->compile;test->test",
//    rawCredsApi % "compile->compile;test->test"
//  )
//  .settings(
//    strictBuildSettings,
//    Compile / javacOptions ++= Seq(
//      "--module-path",
//      (Compile / dependencyClasspath).value
//        .filter(f => f.data.toString.contains("raw") || f.data.toString.contains("lang3"))
//        .files
//        .absString
//    )
//  )

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "raw.compiler.extensions"
)

publishLocal := (publishLocal dependsOn publishM2).value